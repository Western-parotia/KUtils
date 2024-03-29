import com.buildsrc.kts.Dependencies
import com.buildsrc.kts.Statics

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.foundation.example"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.foundation.example"
        minSdk = 21
        targetSdk = 33

        resValue("string", "app_name", Statics.APP_NAME)
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
    implementation(Dependencies.Kotlin.kotlin_stdlib)
    implementation(Dependencies.AndroidX.core_ktx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.Material.material)

//    implementation(Dependencies.Company.json)
    implementation(project(":widget"))
}
configurations.all {
    resolutionStrategy {
        // don't cache changing modules at all
        cacheChangingModulesFor(10, "seconds")
    }
}