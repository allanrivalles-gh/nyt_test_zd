package com.theathletic.extension

import timber.log.Timber

fun Throwable.extLogError() {
    Timber.e(this)
}