package com.theathletic.extension

import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableList
import com.theathletic.utility.rx.DisposableListPropertyChangedCallback
import com.theathletic.utility.rx.DisposablePropertyChangedCallback
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

fun Observable.extAddOnPropertyChangedCallback(
    listener: (Observable?, Int, Observable.OnPropertyChangedCallback) -> Unit
): DisposablePropertyChangedCallback {
    val callback = object : DisposablePropertyChangedCallback() {
        override fun onDispose() {
            removeOnPropertyChangedCallback(this)
        }

        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            return listener.invoke(p0, p1, this)
        }
    }
    addOnPropertyChangedCallback(callback)
    return callback
}

fun <T> ObservableField<T>.extAddOnPropertyChangedCallback(
    listener: (value: T?) -> Unit
): DisposablePropertyChangedCallback {
    val callback = object : DisposablePropertyChangedCallback() {
        override fun onDispose() {
            removeOnPropertyChangedCallback(this)
        }

        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            return listener.invoke(get())
        }
    }
    addOnPropertyChangedCallback(callback)
    return callback
}

fun ObservableInt.extAddOnPropertyChangedCallback(
    listener: (value: Int) -> Unit
): DisposablePropertyChangedCallback {
    val callback = object : DisposablePropertyChangedCallback() {
        override fun onDispose() {
            removeOnPropertyChangedCallback(this)
        }

        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            return listener.invoke(get())
        }
    }
    addOnPropertyChangedCallback(callback)
    return callback
}

fun <T> io.reactivex.Observable<T>.delayEach(interval: Long, timeUnit: TimeUnit): io.reactivex.Observable<T> =
    io.reactivex.Observable.zip(this, io.reactivex.Observable.interval(interval, timeUnit), BiFunction { t1, _ -> t1 })

fun <T> ObservableArrayList<T>.extAddOnListChangedCallback(
    function: () -> Unit
): DisposableListPropertyChangedCallback<T> {
    val callback = object : DisposableListPropertyChangedCallback<T>() {
        override fun onDispose() {
            removeOnListChangedCallback(this)
        }

        override fun onAnyChange(sender: ObservableList<T>) {
            return function.invoke()
        }
    }

    addOnListChangedCallback(callback)
    return callback
}

/**
 * Wrapper for verbose list change callback.
 */
abstract class OnListChangedCallbackWrapper<T> : ObservableList.OnListChangedCallback<ObservableList<T>>() {
    override fun onChanged(sender: ObservableList<T>) {
        onAnyChange(sender)
    }

    override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        onAnyChange(sender)
    }

    override fun onItemRangeMoved(
        sender: ObservableList<T>,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        onAnyChange(sender)
    }

    override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        onAnyChange(sender)
    }

    override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        onAnyChange(sender)
    }

    /**
     * Convenience method when you don't care what happened and you want to trigger all the time the same (like network request)
     */
    open fun onAnyChange(sender: ObservableList<T>) {}
}

/**
 * Function to use dependant observables easier
 * @param mainDependency main dependency which is returned to the mapper
 * @param dependencies which, when changed, trigger this observable
 */
inline fun <T, J : Observable> dependantObservableField(
    mainDependency: J,
    vararg dependencies: Observable,
    crossinline mapper: (J) -> T?
) = object : ObservableField<T>(mainDependency, *dependencies) {
    override fun get(): T? = mapper(mainDependency)
}