plugins {
    id(Plugins.test)
    id(Plugins.kotlin)
}

android {
    namespace = "com.theathletic.instrumentation"

    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "DEBUGGABLE"

        missingDimensionStrategy("base", "releaseTest", "prod")
    }

    compileOptions {
        sourceCompatibility = Versions.compatibilitySource
        targetCompatibility = Versions.compatibilitySource
    }

    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }

    buildTypes {
        create("instrumentation") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release", "debug")
        }
    }

    targetProjectPath = ":mobile"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "instrumentation"
    }
}

dependencies {
    implementation(Dependencies.Android.core)
    implementation("com.google.android.material:material:1.3.0")

    implementation(TestDependencies.junit4)
    implementation(Dependencies.Android.test_junit)
    implementation(Dependencies.truth)

    implementation(Dependencies.Android.test_espresso)
    implementation(Dependencies.Android.test_uiautomator)

    implementation(Dependencies.Android.benchmark)
}