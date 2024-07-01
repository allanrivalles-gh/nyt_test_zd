package com.theathletic.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleObserver
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.util.UUID

interface DataState
interface ViewState

abstract class AthleticViewModel<
    DState : DataState,
    VState : ViewState
    > :
    ViewModel(),
    KoinComponent,
    LifecycleObserver,
    EventEmittingViewModel,
    Transformer<DState, VState> {

    private val eventBus = MutableSharedFlow<Event>()
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)

    protected abstract val initialState: DState

    val compositeDisposable = CompositeDisposable()
    override val eventConsumer: Flow<Event> = eventBus

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state get() = _state.value
    private val _state by lazy { MutableStateFlow(initialState) }

    // this is meant to uniquely identify a page
    // be aware that this is being used for the in memory cache of the feed ads
    val pageViewId: String = UUID.randomUUID().toString()

    val viewState by lazy {
        _state.map { transform(it) }
            .distinctUntilChanged()
    }

    fun updateState(state: DState.() -> DState) {
        val newState = state(_state.value)
        _state.value = newState
    }

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
}

interface EventEmittingViewModel {
    val eventConsumer: Flow<Event>
}

suspend inline fun <reified T : Event> EventEmittingViewModel.observe(
    crossinline onEvent: (T) -> Unit
) {
    eventConsumer.filterIsInstance<T>().collect { onEvent(it) }
}

inline fun <reified T : Event> EventEmittingViewModel.observe(
    scope: CoroutineScope,
    crossinline onEvent: (T) -> Unit
) {
    scope.launch {
        eventConsumer.filterIsInstance<T>().collect { onEvent(it) }
    }
}

inline fun <reified T : Event> EventEmittingViewModel.observe(
    lifecycleOwner: LifecycleOwner,
    crossinline onEvent: (T) -> Unit
) {
    observe(lifecycleOwner.lifecycleScope, onEvent)
}

/**
 * Implement this interface if the [AthleticViewModel] is to be injected via Compose rather than
 * via [AthleticFragment]. If we are injecting via compose, then we do not have (and probably don't
 * want access) to the same lifecycle events specified via [@OnLifecycleEvent].
 */
interface ComposeViewModel {
    /**
     * Called when the Composable which requested this [ViewModel] gets added to the composition.
     */
    fun initialize() {}

    /**
     * Called when the Composable which requested this [ViewModel] gets removed from the
     * composition.
     */
    fun dispose() {}
}