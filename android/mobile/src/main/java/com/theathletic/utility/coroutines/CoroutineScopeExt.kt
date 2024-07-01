package com.theathletic.utility.coroutines

import com.theathletic.network.NetworkChangeListener
import com.theathletic.utility.NetworkManager
import kotlin.coroutines.startCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

fun CoroutineScope.launchWhenOnline(
    networkManager: NetworkManager,
    block: suspend CoroutineScope.() -> Unit
): Job = launch {
    suspendCancellableCoroutine<Unit> { continuation ->
        if (networkManager.isOnline()) {
            block.startCoroutine(this@launch, continuation)
        } else {
            val listener = object : NetworkChangeListener {
                override fun onConnected(isMobile: Boolean) {
                    block.startCoroutine(this@launch, continuation)
                    networkManager.removeNetworkListener(this)
                }

                override fun onNetworkChanged(isMobile: Boolean) {}

                override fun onDisconnected() {}
            }

            networkManager.addNetworkListener(listener)

            continuation.invokeOnCancellation {
                networkManager.removeNetworkListener(listener)
            }
        }
    }
}

val CoroutineScope.job get() = coroutineContext[Job]