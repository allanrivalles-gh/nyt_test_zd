import org.gradle.api.JavaVersion

object Versions {
    const val compileSdk = 34
    const val minSdk = 23
    const val targetSdk = 34
    const val jvmTarget = "17"
    val compatibilitySource = JavaVersion.VERSION_17
}

/**
 * Kotlin version and AGP versions needs to be updated at /buildSrc/build.gradle.kts file too
 **/
object Dependencies {

    const val truth = "com.google.truth:truth:1.1.3"
    const val gson = "com.google.code.gson:gson:2.8.5"

    const val timber = "com.jakewharton.timber:timber:4.7.1"

    object Kotlin {
        const val version = "1.8.22"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val test = "org.jetbrains.kotlin:kotlin-test:$version"
    }

    object Android {
        const val core = "androidx.core:core-ktx:1.9.0"
        const val appcompat = "androidx.appcompat:appcompat:1.5.1"
        const val appcompatresources = "androidx.appcompat:appcompat-resources:1.5.1"
        const val vectordrawableanimated = "androidx.vectordrawable:vectordrawable-animated:1.1.0"
        const val annotation = "androidx.annotation:annotation:1.2.0"
        const val collection = "androidx.collection:collection-ktx:1.0.0"
        const val cardview = "androidx.cardview:cardview:1.0.0"
        const val browser = "androidx.browser:browser:1.4.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:1.1.3"
        const val viewpager = "androidx.viewpager2:viewpager2:1.0.0"
        const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val concurrent = "androidx.concurrent:concurrent-futures-ktx:1.1.0"
        const val media = "androidx.media:media:1.6.0"
        const val webkit = "androidx.webkit:webkit:1.7.0"
        const val fragment = "androidx.fragment:fragment-ktx:1.5.2"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
        const val preferences = "androidx.preference:preference-ktx:1.2.0"
        const val core_test = "androidx.test:core:1.4.0"
        const val install_referrer = "com.android.installreferrer:installreferrer:2.2"
        const val core_desugar = "com.android.tools:desugar_jdk_libs:2.0.3"

        const val benchmark = "androidx.benchmark:benchmark-macro-junit4:1.1.1"

        const val test_junit = "androidx.test.ext:junit:1.1.5"
        const val test_espresso = "androidx.test.espresso:espresso-core:3.5.1"
        const val test_uiautomator = "androidx.test.uiautomator:uiautomator:2.3.0-alpha01"
        const val test_arch = "androidx.arch.core:core-testing:2.2.0"
    }

    object PlayServices {
        private const val play_services_version = "22.0.0"
        private const val ads_identifier_version = "18.0.1"
        const val play_services_ads =
            "com.google.android.gms:play-services-ads:$play_services_version"
        const val play_services_ads_identifier =
            "com.google.android.gms:play-services-ads-identifier:$ads_identifier_version"
    }

    object Compose {
        const val compiler_version = "1.4.8"

        const val bom = "androidx.compose:compose-bom:2023.10.01"
        const val foundation = "androidx.compose.foundation:foundation"
        const val material = "androidx.compose.material:material"
        const val icons = "androidx.compose.material:material-icons-extended"
        const val animation = "androidx.compose.animation:animation"
        const val graphics = "androidx.compose.animation:animation-graphics"
        const val viewbinding = "androidx.compose.ui:ui-viewbinding"
        const val tooling = "androidx.compose.ui:ui-tooling"
        const val tooling_preview = "androidx.compose.ui:ui-tooling-preview"

        private const val version_accompanist = "0.20.3"
        const val swiperefresh =
            "com.google.accompanist:accompanist-swiperefresh:$version_accompanist"
        const val systemuicontroller =
            "com.google.accompanist:accompanist-systemuicontroller:$version_accompanist"
        const val pager = "com.google.accompanist:accompanist-pager:$version_accompanist"
        const val pageIndicator =
            "com.google.accompanist:accompanist-pager-indicators:$version_accompanist"
        const val insets = "com.google.accompanist:accompanist-insets:$version_accompanist"
        const val insetsui = "com.google.accompanist:accompanist-insets-ui:$version_accompanist"
        const val placeholder = "com.google.accompanist:accompanist-placeholder:$version_accompanist"

        const val activity = "androidx.activity:activity-compose:1.3.1"
        const val navigation = "androidx.navigation:navigation-compose:2.5.1"
        const val coil = "io.coil-kt:coil-compose:2.2.2"
        const val lottie = "com.airbnb.android:lottie-compose:5.0.3"

        const val viewmodel = Lifecycle.viewmodel_compose

        const val test_junit4 = "androidx.compose.ui:ui-test-junit4"
        const val test_manifest = "androidx.compose.ui:ui-test-manifest"
    }

    object Firebase {
        const val platform = "com.google.firebase:firebase-bom:32.6.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val config = "com.google.firebase:firebase-config"
        const val messaging = "com.google.firebase:firebase-messaging"

        const val performance = "com.google.firebase:firebase-perf-ktx"
        const val rxfirebasekotlin =
            "com.androidhuman.rxfirebase2:firebase-database-kotlin:16.0.1.0"
        const val rxfirebase = "com.androidhuman.rxfirebase2:firebase-database:16.0.1.0"
    }

    object Exoplayer {
        const val version = "2.18.1"
        const val core = "com.google.android.exoplayer:exoplayer-core:$version"
        const val ui = "com.google.android.exoplayer:exoplayer-ui:$version"
        const val hls = "com.google.android.exoplayer:exoplayer-hls:$version"
    }

    object Glide {
        private const val version = "4.14.1"
        const val glide = "com.github.bumptech.glide:glide:$version"
        const val okhttp = "com.github.bumptech.glide:okhttp3-integration:$version"
        const val compiler = "com.github.bumptech.glide:compiler:$version"
    }

    object Coroutine {
        const val version = "1.6.1"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val rx = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Parcelize {
        private const val parcelize = "org.jetbrains.kotlinx.parcelize.Parcelize:2.4.1"
    }

    object Lifecycle {
        private const val version = "2.4.1"
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val viewmodel_compose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
        const val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
        const val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"

        const val runtime_test = "androidx.lifecycle:lifecycle-runtime-testing:$version"
    }

    object WorkManager {
        private const val version = "2.7.1"
        const val runtime = "androidx.work:work-runtime-ktx:$version"
        const val rxJava = "androidx.work:work-rxjava2:$version"
        const val testing = "androidx.work:work-testing:$version"
    }

    object Retrofit {
        const val retrofit_version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofit_version"
        const val converter = "com.squareup.retrofit2:converter-gson:$retrofit_version"
        const val rxjava = "com.squareup.retrofit2:adapter-rxjava2:$retrofit_version"

        const val okhttp_version = "4.9.1"
        const val okhttp = "com.squareup.okhttp3:okhttp:$okhttp_version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
    }

    object Room {
        private const val version = "2.5.2"
        const val room = "androidx.room:room-ktx:$version"
        const val runtime = "androidx.room:room-runtime:$version"
        const val compiler = "androidx.room:room-compiler:$version"
        const val rxjava = "androidx.room:room-rxjava2:$version"
    }

    object Hilt {
        const val version = "2.47"
        const val core = "com.google.dagger:hilt-android:$version"
        const val compiler = "com.google.dagger:hilt-android-compiler:$version"
        const val testing = "com.google.dagger:hilt-android-testing:$version"
    }

    object RxJava {
        const val rxjava = "io.reactivex.rxjava2:rxjava:2.2.9"
        const val rxkotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"
        const val android = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val debug = "com.akaita.java:rxjava2-debug:1.4.0"
    }

    object Koin {
        private const val koin_version = "3.2.2"
        private const val koin_android_version = "3.2.3"
        private const val koin_android_compose_version = "3.3.0"
        const val core = "io.insert-koin:koin-core:$koin_version"
        const val test = "io.insert-koin:koin-test:$koin_version"
        const val testjunit4 = "io.insert-koin:koin-test-junit4:$koin_version"
        const val android = "io.insert-koin:koin-android:$koin_android_version"
        const val compose = "io.insert-koin:koin-androidx-compose:$koin_android_compose_version"
    }

    object Moshi {
        private const val moshi_version = "1.15.0"
        const val moshi = "com.squareup.moshi:moshi:$moshi_version"
        const val moshi_adapters = "com.squareup.moshi:moshi-adapters:$moshi_version"
        const val moshi_codegen = "com.squareup.moshi:moshi-kotlin-codegen:$moshi_version"
    }

    object Kochava {
        private const val version = "4.1.1"
        const val tracker = "com.kochava.tracker:tracker:$version"
        const val events = "com.kochava.tracker:events:$version"
    }

    object Comscore {
        const val tracker = "com.comscore:android-analytics:6.9.2"
    }

    object Datadog {
        private const val version = "1.18.1"
        const val datadog = "com.datadoghq:dd-sdk-android:$version"
        const val timber = "com.datadoghq:dd-sdk-android-timber:$version"
    }

    object Apollo {
        const val version = "3.8.2"
        const val runtime = "com.apollographql.apollo3:apollo-runtime:$version"
        const val cache = "com.apollographql.apollo3:apollo-http-cache:$version"
    }

    object KotlinPoet {
        const val version = "1.13.0"
        const val kotlinpoet = "com.squareup:kotlinpoet:$version"
        const val metadata = "com.squareup:kotlinpoet-metadata:$version"
    }

    object Billing {
        const val version = "6.0.1"
        const val billingClient = "com.android.billingclient:billing-ktx:$version"
    }

    object Iterable {
        const val version = "3.4.4"
        const val iterableApi = "com.iterable:iterableapi:$version"
    }

    object Transcend {
        const val version = "1.0.1"
        const val transcendApi = "io.transcend.webview:webview:$version"
    }
}

object Plugins {
    const val application = "com.android.application"
    const val library = "com.android.library"
    const val test = "com.android.test"

    const val athletic_test = "com.theathletic.plugin.test"
    const val athletic_config = "com.theathletic.plugin.config"
    const val athletic_compose = "com.theathletic.plugin.compose"

    const val kotlin = "org.jetbrains.kotlin.android"
    const val kotlin_parcelize = "org.jetbrains.kotlin.plugin.parcelize"

    const val apollo = "com.apollographql.apollo3"
    const val apollo_version = Dependencies.Apollo.version

    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val ktlint_version = "11.0.0"

    const val kapt: String = "kotlin-kapt"

    const val hilt = "com.google.dagger.hilt.android"
    const val hilt_version = Dependencies.Hilt.version

    const val dependency_updates: String = "com.github.ben-manes.versions"
    const val dependency_updates_version: String = "0.44.0"

    const val detekt: String = "io.gitlab.arturbosch.detekt"
    const val detekt_version: String = "1.18.0"

    const val kover = "org.jetbrains.kotlinx.kover"
    const val kover_version = "0.7.5"

    const val module_graph_visualization: String = "com.savvasdalkitsis.module-dependency-graph"
    const val module_graph_visualization_version: String = "0.10"
}

object TestDependencies {
    const val junit4 = "junit:junit:4.13.2"
    const val mockito_core = "org.mockito:mockito-core:3.11.2"
    const val mockito_kotlin = "org.mockito.kotlin:mockito-kotlin:3.2.0"
    const val mockito_kotlin_inline = "org.mockito:mockito-inline:3.11.2"
    const val mockk = "io.mockk:mockk:1.13.3"
    const val roboelectric = "org.robolectric:robolectric:4.9"
}