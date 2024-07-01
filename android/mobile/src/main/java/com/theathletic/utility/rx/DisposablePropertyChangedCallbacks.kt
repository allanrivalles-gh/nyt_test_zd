package com.theathletic.utility.rx

import androidx.databinding.Observable
import com.theathletic.extension.OnListChangedCallbackWrapper
import io.reactivex.disposables.Disposable

abstract class DisposablePropertyChangedCallback :
    Observable.OnPropertyChangedCallback(),
    Disposable {

    private var disposed = false

    abstract fun onDispose()

    override fun dispose() {
        disposed = true
        onDispose()
    }

    override fun isDisposed() = disposed
}

abstract class DisposableListPropertyChangedCallback<T> :
    OnListChangedCallbackWrapper<T>(),
    Disposable {

    private var disposed = false

    abstract fun onDispose()

    override fun dispose() {
        disposed = true
        onDispose()
    }

    override fun isDisposed() = disposed
}