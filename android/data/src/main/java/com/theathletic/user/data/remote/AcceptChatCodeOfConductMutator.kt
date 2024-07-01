package com.theathletic.user.data.remote

import com.theathletic.AcceptChatCodeOfConductMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider

class AcceptChatCodeOfConductMutator @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userApi: UserApi,
    private val userManager: IUserManager,
) : RemoteToLocalFetcher<
    EmptyParams,
    AcceptChatCodeOfConductMutation.Data,
    Boolean,
    >(dispatcherProvider) {

    override suspend fun makeRemoteRequest(
        params: EmptyParams
    ) = userApi.acceptChatCodeOfConduct().data

    override fun mapToLocalModel(
        params: EmptyParams,
        remoteModel: AcceptChatCodeOfConductMutation.Data,
    ) = remoteModel.acceptCodeOfConduct.run {
        asCustomer?.code_of_conduct_2022 ?: asStaff?.code_of_conduct_2022 ?: true
    }

    override suspend fun saveLocally(
        params: EmptyParams,
        accepted: Boolean
    ) {
        userManager.saveCurrentUser(
            userManager.getCurrentUser()?.apply {
                isCodeOfConductAccepted = accepted
            }
        )
    }
}