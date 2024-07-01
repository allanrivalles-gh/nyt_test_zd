plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.boxscore"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":ui"))
    implementation(project(":api:graphql"))
    implementation(project(":feed"))
    implementation(project(":podcast"))
    implementation(project(":comments"))
    implementation(project(":analytics"))

    // Compose Dependencies
    implementation(Dependencies.Compose.pager)
    implementation(Dependencies.Compose.systemuicontroller)

    // AndroidX libs
    implementation(Dependencies.Android.media)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}