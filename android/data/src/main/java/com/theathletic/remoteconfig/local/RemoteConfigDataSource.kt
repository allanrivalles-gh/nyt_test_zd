package com.theathletic.remoteconfig.local

import kotlinx.coroutines.flow.Flow

interface RemoteConfigDataSource {
    fun getString(entry: RemoteConfigEntry): Flow<String>
    fun getInt(entry: RemoteConfigEntry): Flow<Int>
    fun getDouble(entry: RemoteConfigEntry): Flow<Double>
    fun refresh()
}