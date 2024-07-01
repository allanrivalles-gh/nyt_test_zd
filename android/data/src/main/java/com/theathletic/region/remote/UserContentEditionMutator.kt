package com.theathletic.region.remote

import com.theathletic.SetUserContentEditionMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider

class UserContentEditionMutator @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userContentEditionApi: UserContentEditionApi,
    private val userManager: IUserManager,
) : RemoteToLocalFetcher<UserContentEditionMutator.Params, SetUserContentEditionMutation.Data, String>(
    dispatcherProvider
) {

    data class Params(
        val userContentEdition: UserContentEdition
    )

    override suspend fun makeRemoteRequest(params: Params) =
        userContentEditionApi.setUserContentEdition(params.userContentEdition).data

    override fun mapToLocalModel(params: Params, remoteModel: SetUserContentEditionMutation.Data): String {
        return remoteModel.setUserContentEdition
    }

    override suspend fun saveLocally(params: Params, dbModel: String) {
        userManager.setUserContentEdition(dbModel)
    }
}