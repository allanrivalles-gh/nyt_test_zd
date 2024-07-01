package com.theathletic.auth.analytics

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope

enum class AuthenticationNavigationSource(val analyticsKey: String) {
    START_SCREEN("app_login_start_screen"),
    ONBOARDING_NO_PURCHASE("app_onboarding_no_purchase"),
    ONBOARDING_PURCHASE("app_onboarding_post_purchase"),
    PAYWALL("article_paywall"),
    PROFILE("app_settings")
}

class AuthenticationAnalyticsContext @AutoKoin(Scope.SINGLE) constructor() {
    var navigationSource = AuthenticationNavigationSource.START_SCREEN
}