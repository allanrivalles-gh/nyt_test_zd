plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.location"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":api"))

    // Coroutines
    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Retrofit
    implementation(Dependencies.Retrofit.retrofit)

    // Moshi
    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}