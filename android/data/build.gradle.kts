plugins {
    id(Plugins.library)
    id(Plugins.athletic_test)
    id(Plugins.kapt)
}

val kotlinx_coroutines: String by project

android {
    namespace = "com.theathletic.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":entity"))
    implementation(project(":db"))
    implementation(project(":api"))

    implementation(platform(Dependencies.Firebase.platform))
    implementation(Dependencies.Firebase.config)

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.rx)

    implementation(Dependencies.Moshi.moshi)
    implementation(Dependencies.Moshi.moshi_adapters)

    testImplementation(project(":core:test"))
}