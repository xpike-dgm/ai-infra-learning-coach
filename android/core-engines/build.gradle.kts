// Pure Kotlin. No Android, no network, no AI type may appear in this module (MSBX-v0).
plugins {
    alias(libs.plugins.kotlin.jvm)
    // 12F: the 3H virtual users are defined once here and reused by the application and presentation
    // tests, so a scenario cannot mean one thing to the planner and another to its explanation.
    `java-test-fixtures`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-ports"))
    testImplementation(kotlin("test"))
    testFixturesImplementation(project(":core-model"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
