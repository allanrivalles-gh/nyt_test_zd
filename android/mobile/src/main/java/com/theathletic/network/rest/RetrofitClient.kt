package com.theathletic.network.rest

import com.google.gson.Gson
import com.theathletic.AthleticConfig
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object RetrofitClient : KoinComponent {
    var retrofit: Retrofit
    private val gson by inject<Gson>()
    private val okHttpClient: OkHttpClient by inject(named("switched-token-http-client"))

    init {
        Timber.i("[RETROFIT] Build Retrofit")
        retrofit = buildRetrofit()
    }

    private fun buildRetrofit(): Retrofit {
        val builder = Retrofit.Builder()
        builder.baseUrl(AthleticConfig.REST_BASE_URL)
        builder.client(okHttpClient)
        builder.addConverterFactory(GsonConverterFactory.create(gson))
        builder.addCallAdapterFactory(createRxJavaCallAdapterFactory())
        return builder.build()
    }

    private fun createRxJavaCallAdapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.create()
}