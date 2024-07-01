plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
    id(Plugins.athletic_compose)
}

android {
    namespace = "com.theathletic.podcast"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":api:graphql"))

    // Coroutines
    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Android
    implementation(Dependencies.Android.media)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}