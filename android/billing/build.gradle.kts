plugins {
    id(Plugins.library)
    id(Plugins.kapt)
    id(Plugins.athletic_compose)
    id(Plugins.athletic_test)
}

android {
    namespace = "com.theathletic.billing"
}

dependencies {
    implementation(project(":analytics"))
    implementation(project(":core"))
    implementation(project(":ui"))
    // Billing data should be moved to the feature module if possible.
    implementation(project(":data"))
    implementation(project(":db"))
    implementation("com.google.android.material:material:1.3.0")

    // Lifecycles
    implementation(Dependencies.Lifecycle.viewmodel)
    implementation(Dependencies.Lifecycle.runtime)

    // Coroutines
    implementation(Dependencies.Coroutine.core)
    implementation(Dependencies.Coroutine.android)

    // Billing
    implementation(Dependencies.Billing.billingClient)

    // WorkManager
    implementation(Dependencies.WorkManager.runtime)

    // Retrofit
    implementation(Dependencies.Retrofit.retrofit)

    // Moshi
    implementation(Dependencies.Moshi.moshi)
    kapt(Dependencies.Moshi.moshi_codegen)

    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(Dependencies.Koin.compose)
    implementation(project(":annotation"))
    kapt(project(":codegen"))
}