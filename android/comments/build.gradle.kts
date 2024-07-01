plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.comments"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":entity"))
    implementation(project(":db"))
    implementation(project(":api"))
    implementation(project(":ui"))
    implementation(project(":analytics"))

    // Coroutines
    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Compose
    implementation(Dependencies.Compose.swiperefresh)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}