plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.ads.models"
}

dependencies {
    implementation(project(":core"))

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}