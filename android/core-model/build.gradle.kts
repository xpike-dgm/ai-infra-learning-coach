// Pure Kotlin. No Android, no network, no AI type may appear in this module (MSBX-v0).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
