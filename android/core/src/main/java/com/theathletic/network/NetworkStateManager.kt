package com.theathletic.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkStateManager @AutoKoin(Scope.SINGLE) constructor(
    connectivityManager: ConnectivityManager
) {

    private val _isNetworkConnected = MutableStateFlow(true)
    val isNetworkConnected = _isNetworkConnected.asStateFlow()

    private val networkChangeListener = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkConnected.value = true
        }

        override fun onLost(network: Network) {
            _isNetworkConnected.value = false
        }
    }

    init {
        if (hasConnectivityManagerOSBug()) {
            _isNetworkConnected.value = true
        } else {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            connectivityManager.requestNetwork(networkRequest, networkChangeListener)
            _isNetworkConnected.value = connectivityManager.activeNetwork != null
        }
    }

    private fun hasConnectivityManagerOSBug(): Boolean {
        // Android 6.0 has a network connectivity bug, so do some Huawei devices
        // https://issuetracker.google.com/issues/37067994
        val isAndroid6 = Build.VERSION.SDK_INT == 23 && Build.VERSION.RELEASE == "6.0"
        val isRiskyHuawei = Build.VERSION.RELEASE == "6.0.1" &&
            (Build.MODEL.contains("HUAWEI") || Build.MODEL.contains("KIW-"))
        return isAndroid6 || isRiskyHuawei
    }
}