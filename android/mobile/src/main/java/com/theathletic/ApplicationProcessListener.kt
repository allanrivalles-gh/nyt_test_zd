package com.theathletic

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.theathletic.analytics.ComscoreWrapper
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class ApplicationProcessListener @AutoKoin(Scope.SINGLE) constructor(
    val comscoreWrapper: ComscoreWrapper
) : DefaultLifecycleObserver {

    private val _isInForegroundFlow = MutableStateFlow(false)
    val isInForegroundFlow: StateFlow<Boolean> = _isInForegroundFlow
    val isInForeground: Boolean = isInForegroundFlow.value

    fun attach() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        _isInForegroundFlow.value = false
        comscoreWrapper.notifyUxInactive()
        Timber.d("Application is in background")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        _isInForegroundFlow.value = true
        comscoreWrapper.notifyUxActive()
        Timber.d("Application is in foreground")
    }
}