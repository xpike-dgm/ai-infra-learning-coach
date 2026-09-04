// Implements PersistencePort. The bundled SQLite driver is what lets TVSX-v0 tier T2 run
// against a real storage engine off-device instead of only on a phone.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core-model"))
    implementation(project(":core-ports"))
    implementation(libs.sqlite)
    implementation(libs.sqlite.bundled)
    testImplementation(kotlin("test"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
