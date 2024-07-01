plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.reader"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":ads:models"))
    implementation(project(":ads:ui"))

// Compose
    implementation(Dependencies.Compose.pager)

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