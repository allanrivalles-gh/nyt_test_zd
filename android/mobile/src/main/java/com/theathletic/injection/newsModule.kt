package com.theathletic.injection

import com.theathletic.repository.twitter.TwitterRetrofitClient
import org.koin.dsl.module

val newsModule = module {
    single {
        TwitterRetrofitClient()
            .twitterApi
    }
}