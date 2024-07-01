package com.theathletic.extension

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import io.reactivex.disposables.CompositeDisposable
import kotlin.test.assertEquals
import org.junit.Test

class DisposablePropertyChangedCallbackTest {

    @Test
    fun `property change callback observes normally`() {
        val observable = ObservableInt()
        var lastValue = 0

        observable.extAddOnPropertyChangedCallback { _, _, _ ->
            lastValue = observable.get()
        }

        observable.set(5)
        observable.set(10)

        assertEquals(10, lastValue)
    }

    @Test
    fun `property change callback stops observing after dispose`() {
        val observable = ObservableInt()
        var lastValue = 0

        val listener = observable.extAddOnPropertyChangedCallback { _, _, _ ->
            lastValue = observable.get()
        }

        observable.set(5)
        listener.dispose()
        observable.set(10)

        assertEquals(5, lastValue)
    }

    @Test
    fun `property change callback stops observing after dispose on composite disposable`() {
        val observable = ObservableInt()
        val compositeDisposable = CompositeDisposable()
        var lastValue = 0

        compositeDisposable.add(
            observable.extAddOnPropertyChangedCallback { _, _, _ ->
                lastValue = observable.get()
            }
        )

        observable.set(5)
        observable.set(10)
        compositeDisposable.dispose()
        observable.set(20)

        assertEquals(10, lastValue)
    }

    @Test
    fun `list property change callback observes normally`() {
        val observable = ObservableArrayList<String>()
        var counter = 0

        observable.extAddOnListChangedCallback {
            counter++
        }

        observable.add("first")
        observable.add("second")
        observable.add("third")

        assertEquals(3, counter)
    }

    @Test
    fun `list property change callback stops observing after dispose`() {
        val observable = ObservableArrayList<String>()
        var counter = 0

        val listener = observable.extAddOnListChangedCallback {
            counter++
        }

        observable.add("first")
        observable.add("second")
        listener.dispose()
        observable.add("third")

        assertEquals(2, counter)
    }
}