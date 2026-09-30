// Root build. The only logic here is the structural check that makes MSBX-v0's dependency rule
// fail the build instead of relying on everyone remembering it (TVSX-v0 tier T3).
//
// Everything the check needs is captured as plain data at configuration time and everything it
// uses at execution time is local, so the task stays compatible with the configuration cache.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

tasks.register("verifyModuleBoundaries") {
    group = "verification"
    description = "Fails the build on a forbidden module dependency, a cycle, or an unknown module."

    // Declared project dependencies, captured as strings. Nothing here resolves a configuration.
    val graph: Map<String, List<String>> = subprojects.associate { sub ->
        val deps = sub.configurations
            .filter {
                it.name == "implementation" || it.name == "api" ||
                    it.name.endsWith("Implementation") || it.name.endsWith("Api")
            }
            .flatMap { configuration -> configuration.dependencies }
            .filterIsInstance<ProjectDependency>()
            .map { it.path }
            // A module's own tests depending on its own test fixtures (12F) is not an edge between
            // modules. Only that exact self-reference is dropped; every edge to another module is kept.
            .filter { it != sub.path }
            .distinct()
        sub.path to deps
    }

    doLast {
        // MSBX-v0 / D-078 layer of each module, derived from its name prefix. The prefix is not
        // decoration: it is what the rule is expressed over.
        val layerOf: (String) -> String = { path ->
            val name = path.removePrefix(":")
            when {
                name.startsWith("core-") -> "core"
                name.startsWith("data-") -> "data"
                name.startsWith("ai-") -> "ai"
                name.startsWith("app-") -> "app"
                else -> "unknown"
            }
        }

        // Inward only: core may never depend outward. Canonical in
        // arch/9d_service_boundaries/boundaries.yaml and mirrored here.
        val forbiddenEdges = listOf("core" to "data", "core" to "ai", "core" to "app")

        // Only the composition root may depend on every layer (MSBX-v0 composition_root).
        val compositionRoot = ":app-wiring"

        val violations = mutableListOf<String>()

        // 1. Every module must sit in a known layer.
        graph.keys.forEach { module ->
            if (layerOf(module) == "unknown") {
                violations += "module '$module' has no recognised layer prefix (core-/data-/ai-/app-)"
            }
        }

        // 2. No forbidden edge. This is what turns "the core survives without AI and without a
        //    network" from a promise into a property of the build.
        graph.forEach { (module, deps) ->
            val from = layerOf(module)
            deps.forEach { dep ->
                val to = layerOf(dep)
                if (forbiddenEdges.any { it.first == from && it.second == to }) {
                    violations += "forbidden dependency: $module ($from) -> $dep ($to)"
                }
            }
        }

        // 3. Only the composition root may reach every layer.
        graph.forEach { (module, deps) ->
            val reached = deps.map(layerOf).toSet()
            if (module != compositionRoot && reached.containsAll(setOf("data", "ai", "app"))) {
                violations += "only $compositionRoot may depend on data, ai and app; $module does"
            }
        }

        // 4. The graph must be acyclic. The cycle is computed, not assumed.
        val colour = mutableMapOf<String, Int>() // 0 unvisited, 1 in progress, 2 done
        val stack = ArrayDeque<String>()
        fun visit(node: String): List<String> {
            colour[node] = 1
            stack.addLast(node)
            graph[node].orEmpty().forEach { next ->
                if (graph.containsKey(next)) {
                    when (colour[next] ?: 0) {
                        1 -> return stack.dropWhile { it != next } + next
                        0 -> {
                            val found = visit(next)
                            if (found.isNotEmpty()) return found
                        }
                    }
                }
            }
            stack.removeLast()
            colour[node] = 2
            return emptyList()
        }
        // Report the first cycle only: once one is found the traversal stack is no longer a
        // faithful path, so any further chain printed from it would be misleading.
        run {
            for (node in graph.keys) {
                if ((colour[node] ?: 0) == 0) {
                    val cycle = visit(node)
                    if (cycle.isNotEmpty()) {
                        violations += "dependency cycle: ${cycle.joinToString(" -> ")}"
                        return@run
                    }
                }
            }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                "MSBX-v0 module boundary violations:\n" + violations.joinToString("\n") { "  - $it" }
            )
        }

        logger.lifecycle("verifyModuleBoundaries: ${graph.size} modules, no forbidden edge, no cycle.")
    }
}

// The rule is not optional, so it runs with the rest of verification.
subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named("verifyModuleBoundaries"))
    }
}
