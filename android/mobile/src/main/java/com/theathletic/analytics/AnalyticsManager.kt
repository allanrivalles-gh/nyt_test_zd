package com.theathletic.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.theathletic.AthleticApplication
import com.theathletic.entity.authentication.UserPrivilegeLevel
import com.theathletic.user.UserManager
import org.koin.core.component.KoinComponent

object AnalyticsManager : KoinComponent {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    enum class SignInServiceType(val value: String) {
        EMAIL("email")
    }

    // TT Init
    fun init() {
        // Tt Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(AthleticApplication.getContext())
    }

    fun updateAnalyticsUser() {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(AthleticApplication.getContext())
        val userEntity = UserManager.getCurrentUser()
        firebaseAnalytics.setUserId(userEntity?.id?.toString())
        firebaseAnalytics.setUserProperty("email", userEntity?.email)
        firebaseAnalytics.setUserProperty("userLevel", userEntity?.getUserLevelRaw()?.toString())
        firebaseAnalytics.setUserProperty("subscriber", if (UserManager.isUserSubscribed()) "true" else "false")
        firebaseAnalytics.setUserProperty("device_token", UserManager.getDeviceId())
        val userLevel = userEntity?.getUserLevel() ?: UserPrivilegeLevel.REGULAR_USER
        firebaseAnalytics.setUserProperty(
            "isAthleticStaff",
            if (userLevel.isAtLeastAtLevel(UserPrivilegeLevel.CONTRIBUTOR)) "true" else "false"
        )
        firebaseAnalytics.setUserProperty(
            "isAthleticAdmin",
            if (userLevel.isAtLeastAtLevel(UserPrivilegeLevel.ADMINISTRATOR)) "true" else "false"
        )
    }
}