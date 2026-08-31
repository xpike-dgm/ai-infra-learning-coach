// Module set is canonical in arch/9d_service_boundaries/boundaries.yaml (MSBX-v0 / D-078).
// Adding or renaming a module here without changing that contract is a boundary violation
// and is caught by :verifyModuleBoundaries and by tools/validate_project_skeleton.py.

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ai-infra-learning-coach"

// core-* — pure Kotlin, no Android, no network, no AI (MSBX-v0 §dependency_rule)
include(":core-model")
include(":core-ports")
include(":core-engines")
include(":core-application")
include(":core-presentation")

// data-* — implementations of core-owned ports
include(":data-persistence")
include(":data-curriculum")

// ai-* — optional; the app must build and run without it (MSBX-v0 §ai_absence)
val withAiAdapter: String = providers.gradleProperty("withAiAdapter").getOrElse("true")
if (withAiAdapter.toBoolean()) {
    include(":ai-adapter")
}

// app-* — presentation shell and the single composition root
include(":app-ui")
include(":app-wiring")
