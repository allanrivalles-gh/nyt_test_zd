plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.hub"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":reader"))
    implementation(project(":scores:boxscore"))

// Compose
    implementation(Dependencies.Compose.pager)
    implementation(Dependencies.Compose.systemuicontroller)

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
}