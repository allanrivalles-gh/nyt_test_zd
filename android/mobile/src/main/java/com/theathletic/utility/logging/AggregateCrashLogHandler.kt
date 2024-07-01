package com.theathletic.utility.logging

import com.theathletic.entity.user.UserEntity

/**
 * CrashLogHandler that delegates to all the given handlers
 */
class AggregateCrashLogHandler(
    private val handlers: List<ICrashLogHandler>
) : ICrashLogHandler {
    override fun logException(e: Throwable) {
        handlers.forEach { it.logException(e) }
    }

    override fun leaveBreadcrumb(message: String) {
        handlers.forEach { it.leaveBreadcrumb(message) }
    }

    override fun setUserInformation(user: UserEntity?) {
        handlers.forEach { it.setUserInformation(user) }
    }

    override fun setCurrentActivityKey(name: String) {
        handlers.forEach { it.setCurrentActivityKey(name) }
    }

    override fun setCurrentFragmentKey(name: String) {
        handlers.forEach { it.setCurrentFragmentKey(name) }
    }

    override fun setCurrentDataIdKey(id: String) {
        handlers.forEach { it.setCurrentDataIdKey(id) }
    }

    override fun trackException(
        throwable: Throwable,
        cause: String?,
        message: String?,
        log: String?
    ) {
        handlers.forEach { it.trackException(throwable, cause, message, log) }
    }
}