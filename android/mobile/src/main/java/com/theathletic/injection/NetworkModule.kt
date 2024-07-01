package com.theathletic.injection

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.http.HttpMethod
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.apollographql.apollo3.cache.http.HttpFetchPolicy
import com.apollographql.apollo3.cache.http.httpCache
import com.apollographql.apollo3.cache.http.httpFetchPolicy
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.apollographql.apollo3.network.ws.DefaultWebSocketEngine
import com.apollographql.apollo3.network.ws.SubscriptionWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.theathletic.AthleticConfig
import com.theathletic.BuildConfig
import com.theathletic.analytics.AnalyticsEndpointConfig
import com.theathletic.entity.serialization.DatetimeAdapter
import com.theathletic.network.apollo.ApolloCache
import com.theathletic.network.rest.GsonProvider
import com.theathletic.network.rest.OkHttpClientProvider
import com.theathletic.type.Timestamp
import com.theathletic.utility.INetworKManager
import com.theathletic.utility.IPreferences
import com.theathletic.utility.NetworkManager
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    val baseClient = "base-okhttp-client"

    single { GsonProvider.buildGsonWithAllAdapters() }

    single {
        Moshi.Builder()
            .add(DatetimeAdapter())
            .build()
    }

    single(named(baseClient)) {
        get<OkHttpClientProvider>().buildBaseClient()
    }
    single(named("static-token-http-client")) {
        get<OkHttpClientProvider>()
            .buildStaticTokenAuthClient(get(named(baseClient)), AthleticConfig.REST_ACCESS_TOKEN)
    }
    single(named("analytics-token-http-client")) {
        get<OkHttpClientProvider>().buildStaticTokenAuthClient(
            get(named(baseClient)),
            get<AnalyticsEndpointConfig>().accessToken
        )
    }
    single(named("switched-token-http-client")) {
        get<OkHttpClientProvider>()
            .buildFeatureSwitchedAuthClient(get(named(baseClient)))
    }
    single(named("nytimes0location-client")) {
        get<OkHttpClientProvider>()
            .buildBaseClient()
    }
    single(named("user-agent")) {
        String.format(
            "%s/%s %s",
            BuildConfig.APPLICATION_ID,
            BuildConfig.VERSION_NAME,
            System.getProperty("http.agent")
        )
    }
    single {
        Retrofit.Builder()
            .baseUrl(AthleticConfig.REST_BASE_URL)
            .client(get(named("switched-token-http-client")))
            .addConverterFactory(GsonConverterFactory.create(get<Gson>()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
    single(named("nytimes-retrofit-client")) {
        Retrofit.Builder()
            .baseUrl(AthleticConfig.BASE_NYTIMES_LOCATION_URL)
            .client(get(named(baseClient)))
            .addConverterFactory(GsonConverterFactory.create(get<Gson>()))
            .build()
    }
    single {
        NetworkManager.getInstance()
    }
    single<INetworKManager> {
        NetworkManager.getInstance()
    }

    // Apollo
    single {
        val okHttpClient: OkHttpClient = get(named("switched-token-http-client"))
        val accessToken = get<IPreferences>().accessToken
        val apolloCache = ApolloCache(get<Context>())

        val subscriptionWsProtocol = SubscriptionWsProtocol.Factory(
            connectionPayload = {
                mapOf(
                    "x-ath-auth" to accessToken,
                    "x-ath-platform" to AthleticConfig.HEADER_ATH_PLATFORM_VALUE
                )
            }
        )
        val webSocket = WebSocketNetworkTransport.Builder()
            .serverUrl(AthleticConfig.GRAPHQL_SUBSCRIPTION_WEBSOCKET_URL)
            .protocol(subscriptionWsProtocol)
            .webSocketEngine(DefaultWebSocketEngine(okHttpClient))
            .build()

        ApolloClient.Builder()
            .serverUrl(AthleticConfig.GRAPHQL_SERVER_BASE_URL)
            .httpEngine(DefaultHttpEngine(okHttpClient))
            .httpCache(apolloCache.file, apolloCache.size)
            .httpMethod(HttpMethod.Get)
            .autoPersistedQueries()
            .subscriptionNetworkTransport(webSocket)
            .addCustomScalarAdapter(Timestamp.type, timestampAdapter)
            .httpFetchPolicy(HttpFetchPolicy.NetworkFirst)
            .build()
    }
}

private val timestampAdapter = object : Adapter<Long> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): Long {
        return try {
            reader.nextLong()
        } catch (e: NoSuchFieldError) {
            0
        }
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: Long) {
        writer.value(value)
    }
}