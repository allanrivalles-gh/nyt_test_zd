package com.theathletic.ads

import android.os.Bundle
import com.google.android.gms.ads.AdSize
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.theathletic.ads.data.local.ContentType
import com.theathletic.ads.data.local.ViewPortSize
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType

fun AdConfig.getAdKvpsAsJson(): String {
    val moshi = Moshi.Builder().build()
    val adRequirements = this.adRequirements.toMutableMap()
    val jsonAdapter: JsonAdapter<Map<String, String?>> = moshi.adapter(
        Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            String::class.java
        )
    )
    var config = "{\"AdRequirements\":${jsonAdapter.toJson(adRequirements)}"
    if (adPrivacy.isEnabled()) {
        config += ",\"privacy\":{" +
            "\"geo\":${jsonAdapter.toJson(adPrivacy.geo)}}"
    }
    config += ",\"viewport\":{" +
        "\"width\":${viewport.width}," +
        "\"height\":${viewport.height}}"
    config += ",\"adUnitPath\":\"$adUnitPath\""
    config += "}"
    return config
}

fun AdConfig.constructAdTargetingBundle(): Bundle {
    val bundle = Bundle()
    for (key in this.adRequirements.keys) {
        this.adRequirements[key].let {
            if (!it.isNullOrEmpty()) {
                bundle.putString(key, it)
            }
        }
    }
    return bundle
}

val String.adPosition get() = "mid$this"

private const val LARGE_AD_WIDTH = 970
private const val SMALL_AD_HEIGHT = 90
private const val STANDARD_AD_HEIGHT = 250

private val BANNER_AD_SIZE get() = AdSize(LARGE_AD_WIDTH, SMALL_AD_HEIGHT)
private val BILLBOARD_AD_SIZE get() = AdSize(LARGE_AD_WIDTH, STANDARD_AD_HEIGHT)

fun ViewPortSize.getAdSizes(): Array<AdSize> {
    return when (this) {
        ViewPortSize.SMALL -> {
            arrayOf(AdSize.MEDIUM_RECTANGLE, AdSize.FLUID)
        }
        ViewPortSize.MEDIUM -> {
            arrayOf(AdSize.MEDIUM_RECTANGLE, AdSize.LEADERBOARD, AdSize.FLUID)
        }
        else -> {
            arrayOf(BANNER_AD_SIZE, BILLBOARD_AD_SIZE, AdSize.LEADERBOARD, AdSize.FLUID)
        }
    }
}

fun FeedType.shouldDisplayAds(features: Features): Boolean {
    return when (this) {
        is FeedType.Frontpage -> features.isDiscoverAdsEnabled
        is FeedType.User -> features.isHomeFeedAdsEnabled
        is FeedType.League -> features.isLeagueFeedAdsEnabled
        is FeedType.Team -> features.isTeamFeedAdsEnabled
        is FeedType.Category -> features.isNewsTopicAdsEnabled
        is FeedType.Author -> features.isAuthorAdsEnabled
        else -> false
    }
}

fun String?.getLiveBlogTypeFromGameId(): ContentType {
    return if (this.isNullOrEmpty()) {
        ContentType.LIVE_BLOG
    } else {
        ContentType.MATCH_LIVE_BLOG
    }
}