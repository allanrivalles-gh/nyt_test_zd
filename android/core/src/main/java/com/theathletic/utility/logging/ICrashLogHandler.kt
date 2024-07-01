package com.theathletic.utility.logging

import com.theathletic.entity.user.UserEntity

interface ICrashLogHandler {
    fun logException(e: Throwable)

    fun leaveBreadcrumb(message: String) {
        // no-op default
    }

    fun setUserInformation(user: UserEntity?) {
        // no-op default
    }

    fun setCurrentActivityKey(name: String) {
        // no-op default
    }

    fun setCurrentFragmentKey(name: String) {
        // no-op default
    }

    fun setCurrentDataIdKey(id: String) {
        // no-op default
    }

    fun trackException(
        throwable: Throwable,
        cause: String? = null,
        message: String? = null,
        log: String? = null
    )

    class PlayServicesException(source: String = "") : Exception(source)
    class ForceUpdateException(source: String = "") : Exception(source)
    class UserException(source: String = "") : Exception(source)
    class SubscriptionException(source: String = "") : Exception(source)
    class FirebaseRemoteConfigException(source: String = "") : Exception(source)
    class FeedEmptyCarouselException(source: String = "") : Exception(source)
    class DeeplinkException(source: String = "") : Exception(source)
    class ArticleDeeplinkException(cause: Throwable) : Exception(cause)
    class OtherException(source: String = "") : Exception(source)
}