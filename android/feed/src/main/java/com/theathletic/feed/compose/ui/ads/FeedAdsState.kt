package com.theathletic.feed.compose.ui.ads

import android.view.View
import com.theathletic.ads.ui.AdState

internal data class FeedAdsState(
    val loadedAds: Map<String, LoadedAd> = hashMapOf()
) {
    data class LoadedAd(val view: View? = null)

    data class UpdatedAd(
        val id: String,
        val isCollapsed: Boolean,
        val view: View?,
    )

    fun updatingAd(ad: UpdatedAd): FeedAdsState {
        val loadedAds = loadedAds.toMutableMap()
        val view = if (ad.isCollapsed) null else ad.view
        loadedAds[ad.id] = LoadedAd(view)
        return copy(loadedAds = loadedAds)
    }

    fun stateForAd(id: String): AdState {
        return loadedAds[id]?.let { ad ->
            ad.view?.let { AdState.Visible(it) } ?: AdState.Collapsed
        } ?: AdState.Placeholder
    }
}