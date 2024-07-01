package com.theathletic.utility

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.user.IUserManager

class PaywallUtility @AutoKoin(Scope.SINGLE) constructor(
    private val userManager: IUserManager,
) {
    fun shouldUserSeePaywall(): Boolean {
        if (userManager.isUserLoggedIn() && userManager.isUserSubscribed()) {
            return false
        }
        return true
    }
}