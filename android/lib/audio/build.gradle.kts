plugins {
    id(Plugins.library)
    id(Plugins.kapt)
}

android {
    namespace = "com.theathletic.audio"
}

dependencies {
    implementation("com.github.agorabuilder:native-voice-sdk:3.5.0.4")

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}