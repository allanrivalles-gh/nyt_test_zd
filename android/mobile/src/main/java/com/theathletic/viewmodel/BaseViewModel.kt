package com.theathletic.viewmodel

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.theathletic.utility.Event
import com.theathletic.utility.LiveBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.core.component.KoinComponent

@Deprecated("We should use AthleticViewModel instead")
abstract class BaseViewModel : ViewModel(), LifecycleObserver, Observable, KoinComponent {
    // WARNING: have to be present directly in viewmodel class, including implementation of Observable methods
    // otherwise [notifyPropertyChanged] won't refresh UI
    private var callbackRegistry: PropertyChangeRegistry? = null
    private val liveEventsBus = LiveBus()
    private val compositeDisposable = CompositeDisposable()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            if (callbackRegistry == null) {
                callbackRegistry = PropertyChangeRegistry()
            }
        }
        callbackRegistry?.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            if (callbackRegistry == null) {
                return
            }
        }
        callbackRegistry?.remove(callback)
    }

    fun sendEvent(event: Event) {
        liveEventsBus.send(event)
    }

    fun <T : Event> observeEvent(owner: LifecycleOwner, eventClass: Class<T>, observer: Observer<T>) {
        liveEventsBus.observe(owner, eventClass, observer)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        synchronized(this) {
            if (callbackRegistry == null) {
                return
            }
        }
        callbackRegistry?.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with {@link Bindable} to generate a field in
     * <code>BR</code> to be used as <code>fieldId</code>.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            if (callbackRegistry == null) {
                return
            }
        }
        callbackRegistry?.notifyCallbacks(this, fieldId, null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun Disposable.disposeOnCleared() {
        compositeDisposable.add(this)
    }
}