package com.theathletic.utility

import android.util.ArrayMap
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Rewritten to Kotlin from STRV Alfonz library (arch module):
 * https://github.com/petrnohejl/Alfonz/tree/master/alfonz-arch
 */
class LiveBus {
    private val eventMap: MutableMap<Class<out Event>, LiveEvent<out Event>> = ArrayMap()

    fun <T : Event> observe(owner: LifecycleOwner, eventClass: Class<T>, observer: Observer<T>) {
        @Suppress("UNCHECKED_CAST")
        val liveEvent = eventMap.getOrPut(eventClass) { LiveEvent<T>() } as LiveEvent<T>
        liveEvent.observe(owner, observer)
    }

    fun <T : Event> send(event: T) {
        val liveEvent = eventMap.getOrPut(event.javaClass) { LiveEvent<T>() }
        liveEvent.value = event
    }
}

// Source: https://github.com/googlesamples/android-architecture-components/issues/63
class LiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(lifecycleOwner: LifecycleOwner, observer: Observer<in T>) {
        // observe the internal MutableLiveData
        super.observe(
            lifecycleOwner,
            Observer<T> { value ->
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(value)
                }
            }
        )
    }

    @MainThread
    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }

    @MainThread
    fun call() {
        value = null
    }
}

abstract class Event