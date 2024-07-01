package com.theathletic.plugin

import Dependencies
import Plugins
import Versions
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

typealias App = com.android.build.gradle.AppPlugin
typealias Library = com.android.build.gradle.LibraryPlugin
typealias AndroidExtension = com.android.build.gradle.TestedExtension

class ConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.subprojects {
            configureAndroidModule()
        }
    }
}

private fun Project.configureAndroidModule() = onAndroidPluginAdded {
    plugins.apply(Plugins.kotlin)

    android {
        compileSdkVersion(Versions.compileSdk)

        defaultConfig {
            minSdk = Versions.minSdk
            targetSdk = Versions.targetSdk
        }

        compileOptions {
            sourceCompatibility = Versions.compatibilitySource
            targetCompatibility = Versions.compatibilitySource
        }

        flavorDimensions("base")
        productFlavors {
            create("dev") {
                dimension = "base"
                buildConfigField("boolean", "DEV_ENVIRONMENT", "true")
            }
            create("prod") {
                dimension = "base"
                buildConfigField("boolean", "DEV_ENVIRONMENT", "false")
            }
            create("releaseTest") {
                dimension = "base"
                buildConfigField("boolean", "DEV_ENVIRONMENT", "false")
            }
        }
    }

    library {
        libraryVariants.all {
            javaCompileOptions
                .annotationProcessorOptions
                .arguments["theathletic.packagename"] = namespace.orEmpty()
        }
    }

    kotlinCompile {
        kotlinOptions {
            jvmTarget = Versions.jvmTarget
            freeCompilerArgs = listOf(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlin.Experimental"
            )
        }
    }

    dependencies {
        add("implementation", Dependencies.Kotlin.stdlib)
        add("implementation", Dependencies.timber)
        debugImplementation("androidx.compose.runtime:runtime-tracing:1.0.0-alpha03")
    }
}

internal fun Project.onAndroidPluginAdded(action: () -> Unit) {
    plugins.whenPluginAdded {
        if (this is App || this is Library) action()
    }
}

internal fun Project.android(action: AndroidExtension.() -> Unit) {
    val nonAndroidPluginException = Throwable(
        "Make sure that you have declared the Application or Library plugin"
    )

    extensions.findByType<AndroidExtension>()?.also { it.action() } ?: throw nonAndroidPluginException
}

/**
 * Used to specify Library modules configuration (the ones that apply  "com.android.library" plugin)
 */
internal fun Project.library(action: LibraryExtension.() -> Unit) {
    extensions.findByType<LibraryExtension>()?.action()
}

/**
 * Used to specify Application modules configuration (the ones that apply  "com.android.application" plugin)
 */
internal fun Project.application(action: ApplicationExtension.() -> Unit) {
    extensions.findByType<ApplicationExtension>()?.action()
}

internal fun Project.applicationAndroidComponents(action: ApplicationAndroidComponentsExtension.() -> Unit) {
    extensions.findByType<ApplicationAndroidComponentsExtension>()?.action()
}

internal fun Project.libraryAndroidComponents(action: LibraryAndroidComponentsExtension.() -> Unit) {
    extensions.findByType<LibraryAndroidComponentsExtension>()?.action()
}

internal fun Project.kotlinCompile(action: KotlinCompile.() -> Unit) {
    tasks.withType<KotlinCompile>(action)
}