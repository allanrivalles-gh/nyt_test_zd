package com.theathletic.plugin

import Dependencies
import TestDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies

class TestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            android {
                testOptions {
                    unitTests.all {
                        it.testLogging {
                            events = setOf(
                                TestLogEvent.FAILED,
                                TestLogEvent.PASSED,
                                TestLogEvent.SKIPPED
                            )
                        }
                    }

                    unitTests.isIncludeAndroidResources = true
                    unitTests.isReturnDefaultValues = true
                    animationsDisabled = true
                }
            }

            dependencies {
                testImplementation(project(":core:test"))

                testImplementation(TestDependencies.junit4)
                testImplementation(Dependencies.Kotlin.test)
                testImplementation(Dependencies.Coroutine.test)

                testImplementation(Dependencies.truth)
                testImplementation(TestDependencies.mockito_core)
                testImplementation(TestDependencies.mockito_kotlin)
                testImplementation(TestDependencies.mockito_kotlin_inline)

                testImplementation(TestDependencies.mockk)

                testImplementation(Dependencies.Android.test_junit)
                testImplementation(Dependencies.Android.core_test)
                testImplementation(Dependencies.Android.test_arch)
                testImplementation("org.json:json:20180130")

                testImplementation(Dependencies.Lifecycle.runtime_test)

                testImplementation(Dependencies.Koin.test)
                testImplementation(Dependencies.Koin.testjunit4)

                //Robolectric
                testImplementation(TestDependencies.roboelectric)

                //Compose Test
                testImplementation(Dependencies.Compose.test_junit4)
                testImplementation(Dependencies.Compose.test_manifest)
            }
        }
    }
}

fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

fun DependencyHandlerScope.testImplementation(dependencyNotation: Any) {
    add("testImplementation", dependencyNotation)
}

fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any) {
    add("androidTestImplementation", dependencyNotation)
}