package com.theathletic.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.theathletic.utility.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

@Deprecated("Use AthleticViewModel instead")
abstract class LegacyAthleticViewModel : ViewModel(), KoinComponent {
    val compositeDisposable = CompositeDisposable()

    private val eventBus = MutableSharedFlow<Event>()
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val eventConsumer: Flow<Event> = eventBus

    override fun onCleared() {
        compositeDisposable.clear()
    }

    fun Disposable.disposeOnCleared(): Disposable {
        compositeDisposable.add(this)
        return this
    }

    fun sendEvent(event: Event) {
        viewModelScope.launch { eventBus.emit(event) }
    }

    suspend inline fun <reified T : Event> observe(
        crossinline onEvent: (T) -> Unit
    ) {
        eventConsumer.filterIsInstance<T>().collect { onEvent(it) }
    }

    inline fun <reified T : Event> observe(
        scope: CoroutineScope,
        crossinline onEvent: (T) -> Unit
    ) {
        scope.launch {
            eventConsumer.filterIsInstance<T>().collect { onEvent(it) }
        }
    }

    inline fun <reified T : Event> observe(
        lifecycleOwner: LifecycleOwner,
        crossinline onEvent: (T) -> Unit
    ) {
        observe(lifecycleOwner.lifecycleScope, onEvent)
    }
}