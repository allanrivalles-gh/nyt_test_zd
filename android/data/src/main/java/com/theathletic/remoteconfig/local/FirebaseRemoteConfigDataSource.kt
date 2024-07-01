package com.theathletic.remoteconfig.local

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.theathletic.data.BuildConfig
import com.theathletic.remoteconfig.remote.FirebaseRemoteConfigExecutor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FirebaseRemoteConfigDataSource : RemoteConfigDataSource {
    private val remoteConfigExecutor = FirebaseRemoteConfigExecutor(
        if (BuildConfig.DEBUG) DEBUG_FIREBASE_FETCH_INTERVAL
        else FIREBASE_FETCH_INTERVAL
    )

    private val remoteConfig = MutableStateFlow(FirebaseRemoteConfig.getInstance())

    override fun getString(entry: RemoteConfigEntry): Flow<String> = remoteConfig.map { it.getString(entry.value) }

    override fun getInt(entry: RemoteConfigEntry): Flow<Int> = remoteConfig.map { it.getLong(entry.value).toInt() }

    override fun getDouble(entry: RemoteConfigEntry): Flow<Double> = remoteConfig.map { it.getDouble(entry.value) }

    override fun refresh() {
        remoteConfigExecutor.fetchRemoteConfig(
            immediateResponse = { remoteConfig.value = it },
            updatedResponse = { remoteConfig.value = it },
            trackingSource = "FeatureRemoteConfig.init()"
        )
    }

    companion object {
        private const val DEBUG_FIREBASE_FETCH_INTERVAL = 0L
        private const val FIREBASE_FETCH_INTERVAL = 3600L
    }
}