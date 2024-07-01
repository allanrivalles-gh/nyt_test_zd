plugins {
    id(Plugins.library)
    id(Plugins.kapt)
}

val kotlinx_coroutines: String by project

android {
    namespace = "com.theathletic.db"

    defaultConfig {
        javaCompileOptions.annotationProcessorOptions {
            arguments += mapOf(
                "room.schemaLocation" to "${project.projectDir}/schemas",
                "theathletic.packagename" to namespace.orEmpty(),
                "room.incremental" to "true",
            )
        }
    }
}
dependencies {
    implementation(project(":core"))
    implementation(project(":entity"))

    implementation(Dependencies.Coroutine.core)

    implementation("com.google.code.gson:gson:2.8.5")
    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    api(Dependencies.Room.runtime)
    implementation(Dependencies.Room.room)
    implementation(Dependencies.Room.rxjava)
    kapt(Dependencies.Room.compiler)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    implementation(Dependencies.RxJava.rxjava)
}