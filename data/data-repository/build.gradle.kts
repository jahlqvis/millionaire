plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":data:data-local-content"))
    implementation(project(":domain:domain-model"))
    implementation(project(":domain:domain-usecase"))
}
