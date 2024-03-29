plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
maven("https://maven.aliyun.com/repository/public/")
maven("https://maven.aliyun.com/repository/google/")
maven("https://jitpack.io")
}
dependencies {
implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
implementation(localGroovy())
}
java {
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
}
