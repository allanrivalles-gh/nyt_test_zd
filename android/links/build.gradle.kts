plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.links"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":analytics"))
    implementation(project(":billing"))
    implementation(project(":data"))
    implementation(project(":db"))
    implementation(project(":followables"))
    implementation(project(":entity"))

    implementation(Dependencies.Iterable.iterableApi)

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