plugins {
    id(Plugins.library)
    id(Plugins.kapt)
}

android {
    namespace = "com.theathletic.api.rest"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":entity"))

    implementation("com.google.code.gson:gson:2.8.5")
    implementation(Dependencies.Room.runtime)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    implementation(Dependencies.Retrofit.retrofit)
    implementation(Dependencies.RxJava.rxjava)
}