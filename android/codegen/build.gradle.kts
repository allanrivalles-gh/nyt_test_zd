plugins {
    kotlin("jvm")
    id("kotlin-kapt")
}

kapt {
    correctErrorTypes = true
}

java {
    sourceCompatibility = Versions.compatibilitySource
    targetCompatibility = Versions.compatibilitySource
}

sourceSets {
    val main by getting
    main.java.srcDir("${buildDir.absolutePath}/tmp/kapt/main/kotlinGenerated/")
}

dependencies {
    compileOnly(project(":annotation", configuration = "default"))

    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")
    implementation("com.google.auto.service:auto-service:1.0-rc6")
    kapt("com.google.auto.service:auto-service:1.0-rc6")
    implementation(Dependencies.Kotlin.stdlib)

    implementation(Dependencies.KotlinPoet.kotlinpoet)
    implementation(Dependencies.KotlinPoet.metadata)

    implementation(Dependencies.Kotlin.reflect)

    compileOnly("net.ltgt.gradle.incap:incap:0.2")
    kapt("net.ltgt.gradle.incap:incap-processor:0.2")

    testImplementation("junit:junit:4.13")
    testImplementation(Dependencies.Kotlin.test)
}