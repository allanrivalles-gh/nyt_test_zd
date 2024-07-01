plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.ads"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    api(project(":ads:ui"))
    implementation(Dependencies.Android.preferences)
    implementation(Dependencies.PlayServices.play_services_ads)

    implementation(Dependencies.Moshi.moshi)
    implementation(Dependencies.Moshi.moshi_adapters)
    kapt(Dependencies.Moshi.moshi_codegen)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}