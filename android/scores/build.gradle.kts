plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.scores"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":db"))
    implementation(project(":ui"))
    implementation(project(":api:graphql"))
    implementation(project(":analytics"))
    implementation(project(":followables"))

    // Compose
    implementation(Dependencies.Compose.swiperefresh)

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