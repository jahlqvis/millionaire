plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":domain:domain-model"))
    implementation(project(":domain:domain-usecase"))
}
