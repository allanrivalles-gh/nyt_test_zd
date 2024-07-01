package com.theathletic.repository.user

import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.entity.authentication.UserData
import com.theathletic.repository.resource.NetworkBoundResource
import io.reactivex.Maybe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class UserDataData : NetworkBoundResource<UserData>(), KoinComponent {
    private val roomDao by inject<UserDataDao>()
    private val authenticationRepository by inject<AuthenticationRepository>()

    init {
        callback = object : Callback<UserData> {
            override fun saveCallResult(response: UserData) {
                roomDao.insertUserData(response)
                Timber.v("[ROOM] Saved user data")
            }

            override fun loadFromDb() = roomDao.getUserData()

            override fun createNetworkCall(): Maybe<UserData> = authenticationRepository.getUserData()

            override fun mapData(data: UserData?): UserData = data ?: UserData()
        }
    }
}