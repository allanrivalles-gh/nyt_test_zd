plugins {
    id(Plugins.library)
    id(Plugins.kapt)
}

android {
    namespace = "com.theathletic.api"
}

dependencies {
    implementation(project(":core"))
    api(project(":api:graphql"))
    api(project(":api:rest"))

    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.core)
    implementation(project(":annotation"))
    kapt(project(":codegen"))

    api(Dependencies.Retrofit.retrofit)
}