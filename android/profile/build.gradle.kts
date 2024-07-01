plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
    id(Plugins.athletic_compose)
}

android {
    namespace = "com.theathletic.profile"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":analytics"))
    implementation(project(":billing"))
    implementation(project(":data"))
    implementation(project(":api"))
    implementation(project(":location"))

    // Lifecycles
    implementation(Dependencies.Lifecycle.viewmodel)
    implementation(Dependencies.Lifecycle.runtime)

    // Coroutines
    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    // Moshi
    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    // Transcend
    implementation(Dependencies.Transcend.transcendApi)

    implementation(Dependencies.Android.preferences)
    testImplementation(TestDependencies.mockk)
}