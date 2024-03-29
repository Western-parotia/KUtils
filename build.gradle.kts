// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.buildsrc.kts.GlobalConfig

//buildSrc的初始化init
GlobalConfig.init(project)

plugins {
    id("com.android.application").version("8.0.1").apply(false)
    id("com.android.library").version("8.0.1").apply(false)
    id("org.jetbrains.kotlin.android").version("1.8.22").apply(false)
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}