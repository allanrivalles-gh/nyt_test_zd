plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.kotlin_parcelize)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

val androidx_lifecycle: String by project
val kotlinx_coroutines: String by project

android {
    namespace = "com.theathletic.ui"

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation("com.google.android.material:material:1.3.0")

    implementation(Dependencies.Android.appcompat)
    implementation(Dependencies.Android.appcompatresources)
    implementation(Dependencies.Android.vectordrawableanimated)

    implementation(Dependencies.Android.viewpager)
    implementation(Dependencies.Android.recyclerview)

    implementation(Dependencies.Lifecycle.viewmodel)
    implementation(Dependencies.Lifecycle.runtime)
    kapt(Dependencies.Lifecycle.compiler)

    // Compose Dependencies
    implementation(Dependencies.Compose.activity)
    implementation(Dependencies.Compose.viewbinding)
    implementation(Dependencies.Compose.viewmodel)
    implementation(Dependencies.Compose.pager)
    implementation(Dependencies.Compose.pageIndicator)
    implementation(Dependencies.Compose.swiperefresh)
    implementation(Dependencies.Compose.systemuicontroller)
    implementation(Dependencies.Compose.insets)
    implementation(Dependencies.Compose.insetsui)
    implementation(Dependencies.Compose.lottie)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.placeholder)
    implementation(Dependencies.Compose.coil)

    // Koin DI
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Deprecated, remove these as we remove RxJava
    implementation(Dependencies.RxJava.android)
    implementation(Dependencies.RxJava.rxjava)
    implementation(Dependencies.RxJava.rxkotlin)
}