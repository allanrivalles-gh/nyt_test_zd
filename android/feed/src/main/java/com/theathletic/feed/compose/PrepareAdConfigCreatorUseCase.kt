package com.theathletic.feed.compose

import android.util.Size
import com.theathletic.ads.AdConfig
import com.theathletic.ads.adPosition
import com.theathletic.ads.data.local.ContentType
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.ads.FeedAdsPage
import com.theathletic.location.data.LocationRepository
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.user.IUserManager
import kotlinx.coroutines.flow.first

internal class AdConfigCreator(
    private val pageViewId: String,
    private val preConfiguredAdConfigBuilder: AdConfig.Builder,
) {
    fun createConfig(adUnitPath: String?, adId: String): AdConfig {
        return preConfiguredAdConfigBuilder
            .setAdUnitPath(adUnitPath)
            .setPosition(adId.adPosition)
            .build(pageViewId)
    }
}

internal class PrepareAdConfigCreatorUseCase @AutoKoin constructor(
    private val adConfigBuilder: AdConfig.Builder,
    private val locationRepository: LocationRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val userManager: IUserManager,
) {
    data class Environment(
        val screenSize: Size,
        val appVersionName: String,
        val experiments: List<String>,
    )

    suspend operator fun invoke(page: FeedAdsPage, environment: Environment): AdConfigCreator {
        adConfigBuilder
            .subscriber(userManager.isUserSubscribed())
            .viewport(environment.screenSize.width, environment.screenSize.height)
            .appVersion(environment.appVersionName)
            .setCompassExperiments(environment.experiments)
            .contentType(
                when (page.feedType) {
                    FeedType.FOLLOWING -> ContentType.HOME_PAGE.type
                    else -> ContentType.COLLECTION.type
                }
            )
            .setGeo(locationRepository.getCountryCode(), locationRepository.getState())
            .setGDPRCountries(remoteConfigRepository.gdprSupportedCountries.first())
            .setCCPAStates(remoteConfigRepository.ccpaSupportedStates.first())

        return AdConfigCreator(page.pageViewId, adConfigBuilder)
    }
}