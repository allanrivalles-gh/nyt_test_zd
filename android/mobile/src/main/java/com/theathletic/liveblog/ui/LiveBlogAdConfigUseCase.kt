package com.theathletic.liveblog.ui

import com.theathletic.AthleticConfig
import com.theathletic.ads.AdConfig
import com.theathletic.ads.getAdKvpsAsJson
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.compass.getAdExperiments
import com.theathletic.location.data.LocationRepository
import com.theathletic.user.IUserManager

class LiveBlogAdConfigUseCase @AutoKoin constructor(
    private val userManager: IUserManager,
    private val locationRepository: LocationRepository,
    private val adConfigBuilder: AdConfig.Builder
) {
    suspend operator fun invoke(viewId: String, screenWidth: Int, screenHeight: Int): String {
        val adConfig = adConfigBuilder
            .subscriber(userManager.isUserSubscribed())
            .viewport(screenWidth, screenHeight)
            .setCompassExperiments(CompassExperiment.getAdExperiments())
            .appVersion(AthleticConfig.VERSION_NAME)
            .setGeo(locationRepository.getCountryCode(), locationRepository.getState())
            .build(viewId, true)
        return adConfig.getAdKvpsAsJson()
    }
}