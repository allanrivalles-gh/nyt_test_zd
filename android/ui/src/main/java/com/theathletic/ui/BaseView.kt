package com.theathletic.ui

import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner

interface BaseView {
    fun showToast(@StringRes stringRes: Int)
    fun showToast(message: String)
    fun showSnackbar(@StringRes stringRes: Int)
    fun showSnackbar(message: String)
    fun viewLifecycleOwnerProducer(): LifecycleOwner
}