package com.theathletic.repository.twitter

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

class TwitterRetrofitClient : KoinComponent {
    var retrofit: Retrofit
    private val gson by inject<Gson>()
    private val okHttpClient: OkHttpClient by inject(named("base-okhttp-client"))

    init {
        Timber.i("[RETROFIT] Build Retrofit")
        retrofit = buildRetrofit()
    }

    val twitterApi: TwitterApi by lazy {
        retrofit.create(TwitterApi::class.java)
    }

    private fun buildRetrofit(): Retrofit {
        val builder = Retrofit.Builder()
        builder.baseUrl(AthleticConfig.BASE_TWITTER_URL)
        builder.client(okHttpClient)
        builder.addConverterFactory(GsonConverterFactory.create(gson))
        builder.addCallAdapterFactory(createRxJavaCallAdapterFactory())
        return builder.build()
    }

    private fun createRxJavaCallAdapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.create()
}