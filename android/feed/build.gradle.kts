plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.feed"
}

dependencies {
    implementation(project(":ads"))
    implementation(project(":api"))
    implementation(project(":api:graphql"))
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":db"))
    implementation(project(":entity"))
    implementation(project(":ui"))
    implementation(project(":podcast"))
    implementation(project(":analytics"))
    implementation(project(":links"))
    implementation(project(":location"))

    // Lifecycles
    implementation(Dependencies.Lifecycle.viewmodel)
    implementation(Dependencies.Lifecycle.runtime)

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

    // necessary for `FeedAdsControllerImproveImpressionsTest`
    testImplementation(Dependencies.PlayServices.play_services_ads)
}