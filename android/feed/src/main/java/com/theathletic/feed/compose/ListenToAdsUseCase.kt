package com.theathletic.feed.compose

import com.theathletic.ads.data.local.AdLocalModel
import com.theathletic.ads.repository.AdsRepository
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.featureswitch.Features
import com.theathletic.feed.compose.data.Dropzone
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge

internal enum class FeedChangeReason {
    INITIAL_PAGE_LOAD,
    PULL_TO_REFRESH,
    REFRESH_STALE,
    NEXT_PAGE_LOADED,
}

internal class ListenToAdsUseCase @AutoKoin constructor(
    private val features: Features,
    private val adsRepository: AdsRepository,
) {
    private val listeningAdsIds = hashSetOf<String>()

    data class Configuration(
        val shouldReplaceAdsAfterImpression: Boolean,
        val configCreator: AdConfigCreator,
        val changeReason: FeedChangeReason,
    )

    operator fun invoke(
        page: FeedAdsPage,
        ads: List<Dropzone>,
        configuration: Configuration,
    ): Flow<AdLocalModel> {
        // we don't do any work if ads disabled for the feed
        if (features.shouldDisplayAds(page.feedType).not()) return flowOf()

        adsRepository.shouldAllowDiscardingAds = configuration.shouldReplaceAdsAfterImpression

        // we call `toSet()` to create a copy of the keys since we will be mutating `subscriptions`
        var toRemove: Collection<String> = listeningAdsIds.toSet()
        var toAdd: Collection<Dropzone> = ads
        when (configuration.changeReason) {
            FeedChangeReason.NEXT_PAGE_LOADED -> {
                val adsIds = ads.map { it.id }.toSet()
                toRemove = toRemove.filter { adsIds.contains(it).not() }
                toAdd = toAdd.filter { listeningAdsIds.contains(it.id).not() }
            }
            FeedChangeReason.PULL_TO_REFRESH -> {
                if (configuration.shouldReplaceAdsAfterImpression.not()) {
                    adsRepository.clearCache(page.pageViewId)
                }
            }
            FeedChangeReason.INITIAL_PAGE_LOAD,
            FeedChangeReason.REFRESH_STALE -> {}
        }

        for (adId in toRemove) listeningAdsIds.remove(adId)

        for (ad in toAdd) {
            listeningAdsIds.add(ad.id)
            val config = configuration.configCreator.createConfig(ad.unitPath, ad.id)
            adsRepository.fetchAd(page.pageViewId, ad.id, config, configuration.shouldReplaceAdsAfterImpression)
        }

        val subscriptions = ads.map { adsRepository.getAd(page.pageViewId, it.id).filterNotNull() }
        return merge(*subscriptions.toTypedArray())
    }
}

internal fun Features.shouldDisplayAds(feedType: FeedType): Boolean {
    return when (feedType) {
        FeedType.DISCOVER -> isDiscoverAdsEnabled
        FeedType.FOLLOWING -> isHomeFeedAdsEnabled
        FeedType.LEAGUE -> isLeagueFeedAdsEnabled
        FeedType.TEAM -> isTeamFeedAdsEnabled
        FeedType.TAG -> isNewsTopicAdsEnabled
        FeedType.AUTHOR -> isAuthorAdsEnabled
        else -> false
    }
}