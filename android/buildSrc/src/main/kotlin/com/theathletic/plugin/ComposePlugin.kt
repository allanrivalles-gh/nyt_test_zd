package com.theathletic.plugin

import Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            android {
                buildFeatures.apply {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = Dependencies.Compose.compiler_version
                }
            }

            dependencies {
                val bom = platform(Dependencies.Compose.bom)
                implementation(bom)
                androidTestImplementation(bom)

                implementation(Dependencies.Compose.foundation)
                implementation(Dependencies.Compose.material)
                implementation(Dependencies.Compose.animation)
                implementation(Dependencies.Compose.icons)
                implementation(Dependencies.Compose.graphics)

                implementation(Dependencies.Compose.tooling_preview)
                debugImplementation(Dependencies.Compose.tooling)

                testImplementation(Dependencies.Compose.test_junit4)
                debugImplementation(Dependencies.Compose.test_manifest)
            }
        }
    }
}

