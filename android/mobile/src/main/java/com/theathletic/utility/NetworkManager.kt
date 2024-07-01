package com.theathletic.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Build.VERSION
import android.os.Handler
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.theathletic.AthleticApplication
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.network.NetworkChangeListener
import com.theathletic.widget.StatefulLayout
import timber.log.Timber

interface INetworKManager {
    fun isOnline(): Boolean
    fun isOffline(): Boolean
    fun isOnMobileData(): Boolean
}

class NetworkManager private constructor() : INetworKManager {

    companion object {
        private const val DISCONNECTED_TIMEOUT_MS = 500L

        @JvmStatic
        var connected: ObservableBoolean = ObservableBoolean(true)
        @JvmStatic
        var onMobileData: ObservableBoolean = ObservableBoolean(true)

        @Volatile
        private var sInstance: NetworkManager? = null

        fun getInstance(): NetworkManager {
            return sInstance ?: synchronized(this) {
                NetworkManager().also { networkManager ->
                    sInstance = networkManager
                    networkManager.initialize()
                }
            }
        }
    }

    private val connectivityManager by lazy {
        AthleticApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    var currentNetwork: Network? = null

    val disconnectHandler = Handler()
    val disconnectRunnable = Runnable {
        currentNetwork = null
        networkListeners.toList().forEach { it.onDisconnected() }
    }

    private fun initialize() {
        connected.set(true)

        if (!hasConnectivityManagerOSBug()) {
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            try {
                connectivityManager.requestNetwork(request, networkCallback)
            } catch (e: Exception) {
                Timber.e(e)
                // Assume user is not on wifi and therefore has limited data
                connected.set(true)
                onMobileData.set(true)
            }
        } else {
            connected.set(true)
            onMobileData.set(true)
        }
    }

    private fun hasConnectivityManagerOSBug(): Boolean {
        // Android 6.0 has a network connectivity bug, so do some Huawei devices
        // https://issuetracker.google.com/issues/37067994
        val isAndroid6 = VERSION.SDK_INT == 23 && VERSION.RELEASE == "6.0"
        val isRiskyHuawei = VERSION.RELEASE == "6.0.1" &&
            (Build.MODEL.contains("HUAWEI") || Build.MODEL.contains("KIW-"))
        return isAndroid6 || isRiskyHuawei
    }

    val networkListeners = mutableListOf<NetworkChangeListener>().apply {
        add(object : NetworkChangeListener {
            override fun onConnected(isMobile: Boolean) {
                Timber.v("onConnected isMobile=$isMobile")

                connected.set(true)
                onMobileData.set(isMobile)
            }

            override fun onDisconnected() {
                Timber.v("onDisconnected")

                connected.set(false)
                onMobileData.set(false)
            }

            override fun onNetworkChanged(isMobile: Boolean) {
                Timber.v("onNetworkChanged isMobile=$isMobile")

                onMobileData.set(isMobile)
            }
        })
    }

    private var networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            disconnectHandler.removeCallbacks(disconnectRunnable)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val isMobile = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            if (currentNetwork == null) {
                networkListeners.toList().forEach { it.onConnected(isMobile) }
            } else {
                networkListeners.toList().forEach { it.onNetworkChanged(isMobile) }
            }

            currentNetwork = network
        }

        override fun onLost(network: Network) {
            disconnectHandler.postDelayed(disconnectRunnable, DISCONNECTED_TIMEOUT_MS)
        }
    }

    @Suppress("unused")
    fun addNetworkListener(listener: NetworkChangeListener) {
        networkListeners.add(listener)
    }

    @Suppress("unused")
    fun removeNetworkListener(listener: NetworkChangeListener) {
        networkListeners.remove(listener)
    }

    fun executeWithOfflineStateHandle(state: ObservableInt, whenOnline: () -> Unit) {
        executeWithOfflineStateHandle(state, whenOnline, whenOnline)
    }

    fun executeWithOfflineStateHandle(
        state: ObservableInt,
        whenOnline: () -> Unit,
        whenBackOnline: () -> Unit = whenOnline
    ) {
        if (isOnline()) {
            whenOnline()
        } else {
            // show offline
            state.set(StatefulLayout.OFFLINE)
            connected.extAddOnPropertyChangedCallback { _, _, callback ->
                whenBackOnline()
                connected.removeOnPropertyChangedCallback(callback)
            }
        }
    }

    @Deprecated("Use CoroutineScope.launchWhenOnlinen instead")
    fun executeWhenOnline(whenOnline: () -> Unit) {
        if (isOnline()) {
            whenOnline()
        } else {
            connected.extAddOnPropertyChangedCallback { _, _, callback ->
                whenOnline()
                connected.removeOnPropertyChangedCallback(callback)
            }
        }
    }

    override fun isOnline() = connected.get()

    override fun isOffline() = !connected.get()

    override fun isOnMobileData() = onMobileData.get()
}