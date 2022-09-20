import com.buildsrc.kts.Dependencies
import com.buildsrc.kts.Statics

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdkVersion(31)

    defaultConfig {
        applicationId = "com.foundation.example"
        minSdkVersion(21)
        targetSdkVersion(31)

        resValue("string", "app_name", Statics.APP_NAME)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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