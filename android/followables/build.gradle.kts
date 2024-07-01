plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.followables"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":db"))
    implementation(project(":api"))

    // Coroutines
    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}