package com.theathletic.utility.logging

import android.util.Log
import timber.log.Timber.Tree

class BreadcrumbTree(
    val crashLogHandler: ICrashLogHandler
) : Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.INFO) {
            crashLogHandler.leaveBreadcrumb(message)
        }
    }
}