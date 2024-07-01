package com.theathletic.region.remote

import com.theathletic.GetUserContentEditionQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider
import java.util.Locale
import com.theathletic.type.UserContentEdition as UserContentEditionGraphql

class UserContentEditionFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userContentEditionApi: UserContentEditionApi,
    private val userManager: IUserManager,
) : RemoteToLocalFetcher<
    UserContentEditionFetcher.Params,
    GetUserContentEditionQuery.Data,
    String
    >(dispatcherProvider) {

    data class Params(
        val userContentEdition: UserContentEdition
    )

    override suspend fun makeRemoteRequest(params: Params): GetUserContentEditionQuery.Data? {
        return userContentEditionApi.getUserContentEdition(
            userContent = params.userContentEdition
        ).data
    }

    override fun mapToLocalModel(params: Params, remoteModel: GetUserContentEditionQuery.Data): String {
        return remoteModel.getUserContentEdition.name
    }

    override suspend fun saveLocally(params: Params, dbModel: String) {
        userManager.setUserContentEdition(dbModel)
    }

    fun GetUserContentEditionQuery.Data.toLocal(): UserContentEdition {
        return when (getUserContentEdition) {
            UserContentEditionGraphql.us -> UserContentEdition.US
            UserContentEditionGraphql.uk -> UserContentEdition.UK
            else -> Locale.getDefault().toLanguageTag().toUserContentEdition
        }
    }

    private val String.toUserContentEdition: UserContentEdition
        get() = when (this) {
            UserContentEdition.US.value -> UserContentEdition.US
            UserContentEdition.UK.value -> UserContentEdition.UK
            else -> UserContentEdition.US
        }
}