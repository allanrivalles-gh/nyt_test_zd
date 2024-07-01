package com.theathletic.region

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.region.remote.UserContentEditionFetcher
import com.theathletic.region.remote.UserContentEditionMutator
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserContentEditionRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userContentEditionFetcher: UserContentEditionFetcher,
    private val userContentEditionMutator: UserContentEditionMutator,
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun fetchUserContentEdition(userContentEdition: UserContentEdition) = repositoryScope.launch {
        userContentEditionFetcher.fetchRemote(
            UserContentEditionFetcher.Params(
                userContentEdition = userContentEdition
            )
        )
    }

    fun setUserContentEdition(userContentEdition: UserContentEdition) {
        repositoryScope.launch {
            userContentEditionMutator.fetchRemote(
                UserContentEditionMutator.Params(
                    userContentEdition = userContentEdition
                )
            )
        }
    }
}