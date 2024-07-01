package com.theathletic.ads

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.device.IsTabletProvider

class AdConfigClientImpl @AutoKoin(scope = Scope.SINGLE) constructor(
    private val isTabletProvider: IsTabletProvider
) : AdConfigClient {
    override val platform: String
        get() = if (isTabletProvider.isTablet) "tablet" else "phone"
}