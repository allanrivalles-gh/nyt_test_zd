plugins {
    id(Plugins.library)
    id(Plugins.kotlin)
}

android {
    namespace = "com.theathletic.test"
}

dependencies {
    implementation(project(":core"))

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)

    implementation(TestDependencies.junit4)
    implementation(Dependencies.Coroutine.test)
    implementation(Dependencies.Kotlin.test)
    implementation(Dependencies.truth)

    implementation(TestDependencies.roboelectric)

    implementation(platform(Dependencies.Compose.bom))
    implementation(Dependencies.Compose.test_junit4)
}