package com.theathletic.injection

import com.theathletic.analytics.data.remote.AnalyticsApi
import com.theathletic.announcement.AnnouncementApi
import com.theathletic.article.data.remote.ArticleRestApi
import com.theathletic.auth.data.remote.AuthenticationApi
import com.theathletic.author.data.remote.AuthorApi
import com.theathletic.billing.BillingApi
import com.theathletic.gifts.data.remote.GiftsApi
import com.theathletic.location.data.remote.CurrentLocationApi
import com.theathletic.podcast.data.remote.PodcastRestApi
import com.theathletic.referrals.data.remote.ReferralsApi
import com.theathletic.search.data.remote.SearchArticlesApi
import com.theathletic.settings.data.remote.SettingsRestApi
import com.theathletic.user.data.remote.UserRestApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single { get<Retrofit>().create(GiftsApi::class.java) }
    single { get<Retrofit>().create(SettingsRestApi::class.java) }
    single { get<Retrofit>().create(AuthorApi::class.java) }
    single { get<Retrofit>().create(ArticleRestApi::class.java) }
    single { get<Retrofit>().create(AnalyticsApi::class.java) }
    single { get<Retrofit>().create(AuthenticationApi::class.java) }
    single { get<Retrofit>().create(SearchArticlesApi::class.java) }
    single { get<Retrofit>().create(PodcastRestApi::class.java) }
    single { get<Retrofit>().create(ReferralsApi::class.java) }
    single { get<Retrofit>().create(UserRestApi::class.java) }
    single { get<Retrofit>().create(BillingApi::class.java) }
    single { get<Retrofit>(named("nytimes-retrofit-client")).create(CurrentLocationApi::class.java)}
    single { get<Retrofit>().create(AnnouncementApi::class.java) }
}