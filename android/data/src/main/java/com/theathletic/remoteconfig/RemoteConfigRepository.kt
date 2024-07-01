package com.theathletic.remoteconfig

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.remoteconfig.local.RemoteConfigDataSource
import com.theathletic.remoteconfig.local.RemoteConfigEntry
import com.theathletic.remoteconfig.local.getStringList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteConfigRepository @AutoKoin(Scope.SINGLE) constructor(private val dataSource: RemoteConfigDataSource) {
    val androidForceUpdateVersions: Flow<List<String>>
        get() = dataSource.getString(RemoteConfigEntry.FORCE_UPDATE_VERSIONS).map { it.split("_") }

    val articleScrollPercentToConsiderRead: Flow<Int>
        get() = dataSource.getInt(RemoteConfigEntry.ARTICLE_SCROLL_PERCENT_TO_CONSIDER_READ)

    val gdprSupportedCountries: Flow<List<String>>
        get() = dataSource.getStringList(RemoteConfigEntry.PRIVACY_GDPR_SUPPORTED_COUNTRIES)

    val ccpaSupportedStates: Flow<List<String>>
        get() = dataSource.getStringList(RemoteConfigEntry.PRIVACY_CCPA_SUPPORTED_STATES)

    val freeArticlesPerMonthCount: Flow<Int>
        get() = dataSource.getInt(RemoteConfigEntry.FREE_ARTICLES_PER_MONTH_COUNT)

    val articleSubscriberScoreThreshold: Flow<Double>
        get() = dataSource.getDouble(RemoteConfigEntry.ARTICLE_SUBSCRIBER_SCORE_THRESHOLD)

    fun fetchRemoteConfig() = dataSource.refresh()
}