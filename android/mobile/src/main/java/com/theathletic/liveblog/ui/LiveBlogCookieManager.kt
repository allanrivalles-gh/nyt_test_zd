package com.theathletic.liveblog.ui

import com.theathletic.AthleticConfig
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.auth.AthleticCookieManager

class LiveBlogCookieManager @AutoKoin(Scope.SINGLE) constructor() : AthleticCookieManager(AthleticConfig.SITE_URL)