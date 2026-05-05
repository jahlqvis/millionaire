plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "se.yourcompany.miljonaren.data.repository"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":data:data-local-db"))
    implementation(project(":data:data-local-content"))
    implementation(project(":domain:domain-model"))
    implementation(project(":domain:domain-usecase"))
    implementation(libs.androidx.room.runtime)
}
