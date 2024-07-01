package com.theathletic.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    crossinline provider: () -> VM
): VM {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }.let {
        return ViewModelProvider(this, it)[VM::class.java]
    }
}

inline fun <reified VM : ViewModel> Fragment.viewModelProvider(parentFragment: Fragment): VM =
    ViewModelProvider(parentFragment)[VM::class.java]