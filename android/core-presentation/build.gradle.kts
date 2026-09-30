// Pure Kotlin. No Android, no network, no AI type may appear in this module (MSBX-v0).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-engines"))
    testImplementation(kotlin("test"))
    testImplementation(testFixtures(project(":core-engines")))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
