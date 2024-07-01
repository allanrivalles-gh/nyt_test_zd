plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    implementation(kotlin("stdlib"))

    implementation("org.eclipse.jgit:org.eclipse.jgit:6.2.0.202206071550-r")
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.apache:6.2.0.202206071550-r")
    implementation("commons-io:commons-io:2.6")

    implementation(Dependencies.Retrofit.okhttp)
    implementation(Dependencies.Retrofit.logging)
}

tasks.register("cutStable", JavaExec::class) {
    group = "Train Conducting"
    mainClass.set("com.theathletic.release.AthleticReleaseBot")
    args = listOf("cutStable")
    description = "Cuts and pushes a stable branch from develop and creates a minor version bump PR"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("createMergebackPR", JavaExec::class) {
    group = "Train Conducting"
    mainClass.set("com.theathletic.release.AthleticReleaseBot")
    args = listOf("createMergeback")
    description = "Creates a PR that merges the current stable branch back into develop"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("finalizeRelease", JavaExec::class) {
    group = "Train Conducting"
    mainClass.set("com.theathletic.release.AthleticReleaseBot")
    args = listOf("finalizeRelease")
    description = "Tags the current stable branch with a final release tag"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("validateAuthentication", JavaExec::class) {
    group = "Train Conducting"
    mainClass.set("com.theathletic.release.AthleticReleaseBot")
    args = listOf("validateAuthentication")
    description = "Just clones the repo to confirm auth token is working"
    classpath = sourceSets["main"].runtimeClasspath
}