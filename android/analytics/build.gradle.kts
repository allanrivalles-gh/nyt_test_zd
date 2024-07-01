plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.analytics"

    defaultConfig {
        javaCompileOptions.annotationProcessorOptions {
            arguments += mapOf(
                "room.schemaLocation" to "${project.projectDir}/schemas",
                "room.incremental" to "true",
            )
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    implementation(Dependencies.Android.core)

    implementation(Dependencies.Retrofit.retrofit)
    implementation(Dependencies.Retrofit.converter)
    implementation(Dependencies.Retrofit.rxjava)

    implementation("com.google.code.gson:gson:2.8.5")

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)

    // Room
    implementation(Dependencies.Room.room)
    implementation(Dependencies.Room.runtime)
    kapt(Dependencies.Room.compiler)
}