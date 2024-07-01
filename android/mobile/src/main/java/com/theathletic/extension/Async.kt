package com.theathletic.extension

import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService

internal object BackgroundExecutor {
    private var executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())

    fun <T> submit(task: () -> T): Future<T> {
        return executor.submit(task)
    }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
    val mainThread: Thread = Looper.getMainLooper().thread
}

class AsyncContext<T>(val weakRef: WeakReference<T>)

@Suppress("unused")
fun <T> AsyncContext<T>.doOnUiThread(f: (T) -> Unit): Boolean {
    val ref = weakRef.get() ?: return false
    if (ContextHelper.mainThread == Thread.currentThread()) {
        f(ref)
    } else {
        ContextHelper.handler.post { f(ref) }
    }
    return true
}

fun runOnUiThread(f: () -> Unit): Boolean {
    if (ContextHelper.mainThread == Thread.currentThread()) {
        f()
    } else {
        ContextHelper.handler.post { f() }
    }
    return true
}

fun <T> T.doAsync(exceptionHandler: ((Throwable) -> Unit)? = null, task: AsyncContext<T>.() -> Unit): Future<Unit> {
    val context = AsyncContext(WeakReference(this))
    return BackgroundExecutor.submit {
        try {
            context.task()
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr) ?: Unit
        }
    }
}