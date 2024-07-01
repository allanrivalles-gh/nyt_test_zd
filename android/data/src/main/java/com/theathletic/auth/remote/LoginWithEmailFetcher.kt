package com.theathletic.auth.remote

import com.theathletic.WebLoginMutation
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.auth.data.remote.AuthenticationGraphqlApi
import com.theathletic.auth.local.AuthenticationUser
import com.theathletic.data.RemoteToLocalFetcherWithResponse
import com.theathletic.utility.coroutines.DispatcherProvider

class LoginWithEmailFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val authenticationApi: AuthenticationGraphqlApi
) : RemoteToLocalFetcherWithResponse<
    LoginWithEmailFetcher.Params,
    WebLoginMutation.Data,
    AuthenticationUser?
    >(dispatcherProvider) {

    data class Params(
        val email: String,
        val password: String
    )

    override suspend fun makeRemoteRequest(params: Params) =
        authenticationApi.authWithEmail(params.email, params.password).data

    override fun mapToLocalModel(params: Params, remoteModel: WebLoginMutation.Data) =
        with(remoteModel.webLogin.fragments.userCredentials) {
            val userEntity = this.user.fragments.customerDetail.toUserEntity()
            if (access_token.isNotEmpty() && userEntity != null) {
                AuthenticationUser(access_token, userEntity)
            } else {
                null
            }
        }

    override suspend fun saveLocally(params: Params, dbModel: AuthenticationUser?) {
        // TODO(Todd): save preferences and userEntity to local storage here instead of presenter
    }
}