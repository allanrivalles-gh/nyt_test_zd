package com.theathletic.utility.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.theathletic.AthleticConfig
import com.theathletic.entity.user.UserEntity
import com.theathletic.user.UserManager
import com.theathletic.utility.NetworkManager

/**
 * Wrapper class around crash logging so we can easily switch providers and verify these functions were called in
 * unit tests
 */
class CrashlyticsLogHandler : ICrashLogHandler {

    override fun logException(e: Throwable) {
        trackException(e)
    }

    override fun leaveBreadcrumb(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

    override fun setUserInformation(user: UserEntity?) {
        if (!AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED)
            return
        with(FirebaseCrashlytics.getInstance()) {
            setUserId(user?.id.toString())
            setCustomKey("SUBSCRIBED", UserManager.isUserSubscribed())
            setCustomKey("FB_LINKED", user?.isFbLinked == 1)
            setCustomKey("FB_ID", user?.fbId ?: "null")
        }
    }

    override fun trackException(
        throwable: Throwable,
        cause: String?,
        message: String?,
        log: String?
    ) {
        if (!AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED)
            return

        with(FirebaseCrashlytics.getInstance()) {
            if (cause != null)
                setCustomKey("CAUSE", cause)
            if (message != null)
                setCustomKey("MESSAGE", message)
            if (log != null)
                setCustomKey("ERROR", log)

            setCustomKey("IS_ONLINE", NetworkManager.getInstance().isOnline())
            recordException(throwable)
        }
    }

    override fun setCurrentActivityKey(name: String) {
        if (!AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED)
            return

        FirebaseCrashlytics.getInstance().setCustomKey("ACTIVITY", name)
    }

    override fun setCurrentFragmentKey(name: String) {
        if (!AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED)
            return

        FirebaseCrashlytics.getInstance().setCustomKey("FRAGMENT", name)
    }

    override fun setCurrentDataIdKey(id: String) {
        if (!AthleticConfig.IS_EXCEPTION_TRACKING_ENABLED)
            return

        FirebaseCrashlytics.getInstance().setCustomKey("DATA_ID", id)
    }
}