package com.theathletic.utility.logging

import com.theathletic.entity.user.UserEntity
import timber.log.Timber

class LocalCrashLogHandler : ICrashLogHandler {
    override fun logException(e: Throwable) {
        Timber.e(e)
    }

    override fun leaveBreadcrumb(message: String) {
    }

    override fun trackException(
        throwable: Throwable,
        cause: String?,
        message: String?,
        log: String?
    ){
        Timber.e(throwable, message, cause, log)
    }

    override fun setUserInformation(user: UserEntity?) {
    }

    override fun setCurrentActivityKey(name: String) {
    }

    override fun setCurrentFragmentKey(name: String) {
    }

    override fun setCurrentDataIdKey(id: String) {
    }
}