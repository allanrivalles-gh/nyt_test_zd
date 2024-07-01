plugins {
    id(Plugins.library)
    id(Plugins.athletic_test)
    id(Plugins.kapt)
}

android {
    namespace = "com.theathletic.entity"
}

dependencies {
    implementation(project(":core"))

    implementation(Dependencies.Android.core)

    implementation("com.google.code.gson:gson:2.8.5")

    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.rxjava)
    implementation(Dependencies.Room.room)
    kapt(Dependencies.Room.compiler)

    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    androidTestImplementation(Dependencies.Android.test_junit)
}