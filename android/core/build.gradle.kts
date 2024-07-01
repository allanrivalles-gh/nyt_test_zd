plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.kotlin_parcelize)
    id(Plugins.athletic_test)
}

android {

    namespace = "com.theathletic.core"

    buildFeatures {
        // TODO: Remove when ObservableBoolean is no longer used in the `core` module
        dataBinding = true
    }
}

dependencies {
    implementation(Dependencies.Android.appcompat)
    implementation(Dependencies.Android.core)
    implementation(Dependencies.Android.collection)

    implementation(Dependencies.Android.recyclerview)
    implementation(Dependencies.Android.annotation)

    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    // Temp List
    // TODO: Remove these when whatever in `core` is using them switchers to newer stuff
    implementation(Dependencies.gson)
    implementation(Dependencies.Room.runtime)
    kapt(Dependencies.Room.compiler)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}