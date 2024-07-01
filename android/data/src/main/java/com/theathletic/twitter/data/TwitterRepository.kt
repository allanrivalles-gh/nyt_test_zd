package com.theathletic.twitter.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.network.ResponseStatus
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.safeApiRequest
import com.theathletic.repository.twitter.TwitterApi
import com.theathletic.twitter.data.local.TwitterLocalDataSource
import com.theathletic.twitter.data.local.TwitterUrl
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

class TwitterRepository @AutoKoin(Scope.SINGLE) constructor(
    private val twitterApi: TwitterApi,
    dispatcherProvider: DispatcherProvider,
    private val twitterLocalDataSource: TwitterLocalDataSource
) : CoroutineRepository {
    override val repositoryScope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
    private val twitterUrlMutex = Mutex()

    private suspend fun fetchTwitterUrl(url: String, theme: String) = try {
        when (val response = safeApiRequest { twitterApi.getTwitterUrl(url, theme) }) {
            is ResponseStatus.Success -> {
                response.body.body()?.let {
                    TwitterUrl(
                        url = it.url ?: "",
                        html = it.html ?: "",
                        theme = theme,
                        authorName = it.authorName,
                    )
                }
            }
            is ResponseStatus.Error -> {
                Timber.i("Failed to load twitter url for: $url")
                null
            }
        }
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

    suspend fun getTwitterUrl(url: String, isLightTheme: Boolean) = withContext(repositoryScope.coroutineContext) {
        val theme = if (isLightTheme) "light" else "dark"
        val localDataKey = TwitterLocalDataSource.Key(url, theme)
        if (!twitterLocalDataSource.containsKey(localDataKey)) {
            val remoteTwitterUrl = fetchTwitterUrl(url, theme)
            if (remoteTwitterUrl != null) {
                twitterUrlMutex.withLock {
                    twitterLocalDataSource.put(localDataKey, remoteTwitterUrl)
                }
            }
        }
        twitterUrlMutex.withLock {
            twitterLocalDataSource.get(localDataKey)
        }
    }
}