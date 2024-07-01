import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Versions.jvmTarget
}