// The composition root: the only module that knows every implementation. It holds no domain
// logic, no policy and no state (MSBX-v0 composition_root).
//
// The AI adapter is optional on purpose. Building with -PwithAiAdapter=false must succeed and
// must still produce a usable app on the null-evaluator path — that is how V1 criterion 8 is
// satisfied by wiring rather than by hope (MSBX-v0 ai_absence, TVSX-v0 T5-NULLEVAL-01).
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val withAiAdapter: Boolean =
    (project.findProperty("withAiAdapter") as String? ?: "true").toBoolean()

android {
    namespace = "coach.wiring"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "coach.wiring"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0-10a"
        buildConfigField("boolean", "AI_ADAPTER_PRESENT", withAiAdapter.toString())
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // The evaluator seam. Exactly one of these source sets is compiled, so the no-adapter build
    // is a real configuration rather than a stubbed-out branch.
    sourceSets {
        getByName("main") {
            kotlin.srcDir(if (withAiAdapter) "src/withAi/kotlin" else "src/withoutAi/kotlin")
            // 14G: only the AI build declares the network permission; with AI absent nothing can leave the device.
            manifest.srcFile(if (withAiAdapter) "src/withAi/AndroidManifest.xml" else "src/main/AndroidManifest.xml")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-ports"))
    implementation(project(":core-engines"))
    implementation(project(":core-application"))
    implementation(project(":core-presentation"))
    implementation(project(":data-persistence"))
    implementation(project(":data-curriculum"))
    implementation(project(":app-ui"))
    if (withAiAdapter) {
        implementation(project(":ai-adapter"))
    }

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)

    testImplementation(kotlin("test"))
    // 15A: the first JVM test in this module (the shipped package planned by the real planner). An Android module does
    // not infer the JUnit 5 flavour of kotlin-test the way the JVM modules do, so it is named.
    testImplementation(kotlin("test-junit5"))
    // 15B: the shipped course is published into the real SQLite store by a JVM test. The Android flavour of the bundled
    // driver carries only device libraries, so the unit tests run the JVM flavour (the one `data-persistence` tests use).
    testRuntimeOnly("androidx.sqlite:sqlite-bundled-jvm:${libs.versions.sqlite.get()}")
}

configurations.matching { it.name.endsWith("UnitTestRuntimeClasspath") }.configureEach {
    exclude(group = "androidx.sqlite", module = "sqlite-bundled-android")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
