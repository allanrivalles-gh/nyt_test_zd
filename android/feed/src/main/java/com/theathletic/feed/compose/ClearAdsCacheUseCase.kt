package com.theathletic.feed.compose

import com.theathletic.ads.repository.AdsRepository
import com.theathletic.annotation.autokoin.AutoKoin

internal class ClearAdsCacheUseCase @AutoKoin constructor(
    private val adsRepository: AdsRepository,
) {
    operator fun invoke(pageViewId: String) {
        adsRepository.clearCache(pageViewId = pageViewId)
    }
}