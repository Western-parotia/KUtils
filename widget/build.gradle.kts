import com.buildsrc.kts.Dependencies
import com.buildsrc.kts.Publish

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

val versionTimestamp = Publish.Version.getVersionTimestamp()

dependencies {
    implementation(Dependencies.Kotlin.kotlin_stdlib)
    implementation(Dependencies.AndroidX.core_ktx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.Material.material)
    implementation(Dependencies.Foundation.recyclerviewAdapter)
    implementation(Dependencies.OpenSourceLibrary.smartRefreshLayout)
    implementation(Dependencies.OpenSourceLibrary.smartRefreshLayoutHeader)
    implementation(Dependencies.OpenSourceLibrary.smartRefreshLayoutFooter)
    implementation(Dependencies.OpenSourceLibrary.flexBox)
    implementation(Dependencies.Google.gson)
}

android {
    namespace = Publish.Maven.getFourPackage(project.projectDir)
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        buildConfigField("String", "versionName", "\"${Publish.Version.versionName}\"")
        buildConfigField("String", "versionTimeStamp", "\"$versionTimestamp\"")
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs =
            freeCompilerArgs + arrayOf("-module-name", Publish.Maven.getFourPackage(projectDir))
    }
    viewBinding {
        enable = true
    }
}

val sourceCodeTask: Jar = tasks.register("sourceCode", Jar::class.java) {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}
    .get()


tasks.register("createGitTagAndPush", Exec::class.java) {
    commandLine("git", "push", "origin", versionTimestamp)
}
    .get()
    .dependsOn(tasks.register("createGitTag", Exec::class.java) {
        commandLine("git", "tag", versionTimestamp, "-m", "autoCreateWithMavenPublish")
    })

publishing {
    publications {
        create<MavenPublication>("tools") {
            groupId = Publish.Maven.getThreePackage(projectDir)
            artifactId = Publish.Version.artifactId
            version = Publish.Version.versionName
            artifact(sourceCodeTask)
            afterEvaluate {//在脚本读取完成后绑定
                val bundleReleaseAarTask: Task = tasks.getByName("bundleReleaseAar")
                bundleReleaseAarTask.finalizedBy("createGitTagAndPush")
                artifact(bundleReleaseAarTask)
            }
//            artifact("$buildDir/outputs/aar/loading-release.aar")//直接制定文件
            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    if (it.version != "unspecified" && it.name != "unspecified") {
                        val depNode = dependenciesNode.appendNode("dependency")
                        depNode.appendNode("groupId", it.group)
                        depNode.appendNode("artifactId", it.name)
                        depNode.appendNode("version", it.version)
                    }
                }
            }

        }
        repositories {
            if (Publish.SNAPSHOT) {
                Publish.Maven.aliyunSnapshotRepositories(this)
            } else {
                Publish.Maven.aliyunReleaseRepositories(this)
            }
        }
    }
}