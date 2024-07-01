import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.detekt
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    id(Plugins.application).apply(false)
    id(Plugins.library).apply(false)
    id(Plugins.kotlin).apply(false)
    id(Plugins.ktlint).version(Plugins.ktlint_version).apply(true)
    id(Plugins.detekt).version(Plugins.detekt_version).apply(false)
    id(Plugins.athletic_config)
    id(Plugins.dependency_updates).version(Plugins.dependency_updates_version)
    id(Plugins.module_graph_visualization).version(Plugins.module_graph_visualization_version)
    id(Plugins.hilt).version(Plugins.hilt_version).apply(false)
    id(Plugins.kover).version(Plugins.kover_version).apply(false)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-plugins:2.0.0")
        classpath("com.google.firebase:perf-plugin:1.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.2")
        classpath("io.embrace:embrace-swazzler:6.2.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.2")
        classpath("com.android.tools.build:gradle:8.0.0")
    }
}

subprojects {
    plugins.apply(Plugins.ktlint)
    plugins.apply(Plugins.detekt)

    tasks.withType<BaseKtLintCheckTask> { workerMaxHeapSize.set("512m") }

    detekt {
        config = files("${project.rootDir}/extras/detekt.yml")
        baseline = file("$projectDir/detekt-baseline.xml")
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    outputFormatter = "html"
    outputDir = "report/dependencies"
    reportfileName = "available-versions"

    rejectVersionIf {
        candidate.version.contains("alpha", ignoreCase = true) ||
            candidate.version.contains("beta", ignoreCase = true) ||
            candidate.version.contains("snapshot", ignoreCase = true) ||
            candidate.version.contains("-rc", ignoreCase = true)
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.register<GradleBuild>("lintCheck") {
    tasks = listOf("ktlintCheck", "detekt", ":mobile:lint")
}

tasks.register<GradleBuild>("lintFormat") {
    tasks = listOf("ktlintFormat", "detekt", ":mobile:lint")
}