plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.ads.ui"

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":ui"))
    implementation(project(":core"))
    api(project(":ads:models"))

    implementation(Dependencies.Android.constraintlayout)
    implementation(Dependencies.Android.webkit)

    implementation(Dependencies.PlayServices.play_services_ads)

    implementation(Dependencies.Compose.activity)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}