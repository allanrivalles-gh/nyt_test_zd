package com.theathletic.repository

import kotlinx.coroutines.CoroutineScope

interface Repository
interface CoroutineRepository : Repository {
    val repositoryScope: CoroutineScope
}