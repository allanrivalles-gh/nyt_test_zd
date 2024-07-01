package com.theathletic.navigation.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.navigation.data.local.NavigationDao
import com.theathletic.repository.CoroutineRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NavigationRepository @AutoKoin(Scope.SINGLE) constructor(
    private val navigationDao: NavigationDao,
    dispatcherProvider: DispatcherProvider
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    fun clearAllCachedData() = repositoryScope.launch {
        navigationDao.deleteAllNavigationEntities()
    }
}