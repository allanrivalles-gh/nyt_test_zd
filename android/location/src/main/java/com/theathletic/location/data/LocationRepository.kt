package com.theathletic.location.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.location.data.remote.CurrentLocationApi
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.safeApiRequest
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LocationRepository @AutoKoin(Scope.SINGLE) constructor(
    private val locationApi: CurrentLocationApi,
    dispatcherProvider: DispatcherProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
    private var ipBasedLocation: CurrentLocationResponse? = null
    private val locationJob: Job

    init {
        locationJob = scope.launch {
            fetchLocationFromIP()
        }
    }

    suspend fun getCountryCode(): String? {
        fetchLocation().join()
        return ipBasedLocation?.country
    }

    suspend fun getState(): String? {
        fetchLocation().join()
        return ipBasedLocation?.state
    }

    private suspend fun fetchLocation(): Job {
        if (locationJob.isActive) {
            return locationJob
        }
        return scope.launch {
            ipBasedLocation?.let {
                return@launch
            }
            fetchLocationFromIP()
        }
    }

    private suspend fun fetchLocationFromIP() {
        ipBasedLocation = when (val response = safeApiRequest { locationApi.getCurrentLocation() }) {
            is ResponseStatus.Success -> {
                response.body
            }
            else -> CurrentLocationResponse()
        }
    }
}