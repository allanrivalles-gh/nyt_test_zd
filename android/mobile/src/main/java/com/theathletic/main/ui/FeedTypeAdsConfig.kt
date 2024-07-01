package com.theathletic.main.ui

import com.theathletic.AthleticConfig
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.compass.getAdExperiments
import com.theathletic.compass.getHomeAdExperiments
import com.theathletic.compass.shouldImproveAdImpressions
import com.theathletic.feed.FeedType
import com.theathletic.feed.compose.FeedGraphAdsConfig

fun FeedType.adsConfig() = FeedGraphAdsConfig(
    shouldImproveImpressions = CompassExperiment.HOME_ADS_V2.shouldImproveAdImpressions(this),
    experiments = when (this) {
        is FeedType.User -> CompassExperiment.getHomeAdExperiments()
        else -> CompassExperiment.getAdExperiments()
    },
    appVersionName = AthleticConfig.VERSION_NAME,
)