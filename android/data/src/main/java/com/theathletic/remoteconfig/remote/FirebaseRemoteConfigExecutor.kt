package com.theathletic.remoteconfig.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigClientException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigServerException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.theathletic.data.R
import com.theathletic.extension.extLogError
import timber.log.Timber

// NOTE: do not ever add a "_prerelease" feature to the Firebase RemoteConfig Production backend
class FirebaseRemoteConfigExecutor(private val cacheExpiration: Long) {

    fun fetchRemoteConfig(
        immediateResponse: (remoteConfig: FirebaseRemoteConfig) -> Unit = {},
        updatedResponse: (remoteConfig: FirebaseRemoteConfig) -> Unit = {},
        error: (exception: Exception) -> Unit = {},
        trackingSource: String
    ) {
        // cache expiration in seconds. Expire the cache immediately for development mode.
        val firebaseRemoteConfigBuilder = FirebaseRemoteConfigSettings.Builder()
        firebaseRemoteConfigBuilder.minimumFetchIntervalInSeconds = cacheExpiration

        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(firebaseRemoteConfigBuilder.build())
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        // Set immediate response
        immediateResponse(remoteConfig)

        // fetch
        remoteConfig.fetch(cacheExpiration)
            .addOnSuccessListener {
                // Task successful. Activate the fetched data
                Timber.i("RemoteConfig Updated")
                remoteConfig.activate()
                updatedResponse(remoteConfig)
            }
            .addOnCanceledListener {
                Timber.i("RemoteConfig Canceled")
                // Task canceled. Activate the fetched data
                updatedResponse(remoteConfig)
            }
            .addOnFailureListener {
                Timber.i("Error updating RemoteConfig: $it")
                when (it) {
                    is FirebaseRemoteConfigFetchThrottledException -> updatedResponse(remoteConfig)
                    is FirebaseRemoteConfigClientException,
                    is FirebaseRemoteConfigServerException -> {
                        // Firebase client/server exceptions, expected sometimes
                        error(it)
                    }
                    else -> {
                        // Unexpected error with RemoteConfig retrieval
                        var log = "[FirebaseRemoteConfig] Fetch time: ${remoteConfig.info.fetchTimeMillis}\n"
                        log += "[FirebaseRemoteConfig] Fetch status: ${remoteConfig.info.lastFetchStatus}\n"
                        log += "[FirebaseRemoteConfig] Error message: ${it.message}"
                        log += "[FirebaseRemoteConfig] Tracking Source: $trackingSource"
                        it.extLogError()
                        error(it)
                    }
                }
            }
    }
}