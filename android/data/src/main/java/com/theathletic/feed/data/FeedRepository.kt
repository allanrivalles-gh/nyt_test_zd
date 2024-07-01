package com.theathletic.feed.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.local.AthleticEntity
import com.theathletic.entity.local.EntityDataSource
import com.theathletic.entity.local.filterTypes
import com.theathletic.entity.main.FeedItem
import com.theathletic.feed.FeedType
import com.theathletic.feed.data.local.AuthorDetailLocalDataSource
import com.theathletic.feed.data.local.FeedLocalDataSource
import com.theathletic.feed.data.remote.AuthorDetailFetcher
import com.theathletic.feed.data.remote.FeedFetcher
import com.theathletic.repository.CoroutineRepository
import com.theathletic.user.IUserManager
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Deprecated
 * In order to distinguish what's the user following operations from the Followable operations itself
 * we decided to split this repository* into two different repositories, the UserFollowingRepository and
 * FollowableRepository (Now is the FollowableRepositoryNew)
 */
@Deprecated("Some functions of this repository will be moved to some other places")
class FeedRepository @AutoKoin constructor(
    private val feedLocalDataSource: FeedLocalDataSource,
    private val entityDataSource: EntityDataSource,
    private val authorDetailFetcher: AuthorDetailFetcher,
    private val authorDetailLocalDataSource: AuthorDetailLocalDataSource,
    private val userManager: IUserManager,
    dispatcherProvider: DispatcherProvider,
    private val feedFetcher: FeedFetcher
) : CoroutineRepository {
    override val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    private val flowCache = mutableMapOf<FeedType, StateFlow<List<FeedItem>>>()

    fun getFeed(
        feedType: FeedType,
        filterTypes: List<AthleticEntity.Type>? = null
    ): Flow<List<FeedItem>> = flowCache.getOrPut(feedType) {
        MutableStateFlow<List<FeedItem>>(emptyList()).apply {
            val feedFlow = feedLocalDataSource.getFeedResponseDistinct(feedType.compositeId)
            val updateFlow = when {
                filterTypes != null -> entityDataSource.updateFlow.filterTypes(*filterTypes.toTypedArray())
                else -> entityDataSource.updateFlow
            }

            merge(feedFlow, updateFlow)
                .map { loadLocalFeed(feedType.compositeId) }
                .collectIn(repositoryScope) { value = it }
        }
    }

    suspend fun hasCachedFeed(feedType: FeedType) = loadLocalFeed(feedType.compositeId).isNotEmpty()

    private suspend fun loadLocalFeed(feedId: String?): List<FeedItem> {
        val id = feedId ?: return emptyList()

        return withContext(repositoryScope.coroutineContext) {
            feedLocalDataSource.getFeed(id)
                .sortedWith(compareBy(FeedItem::page, FeedItem::pageIndex))
        }
    }

    fun fetchFeed(
        feedType: FeedType,
        forceRefresh: Boolean,
        page: Int,
        isAdsEnabled: Boolean
    ) = repositoryScope.launch {
        feedFetcher.fetchRemote(
            FeedFetcher.Params(
                feedType = feedType,
                forceRefresh = forceRefresh,
                page = page,
                isAdsEnabled = isAdsEnabled,
                contentEdition = userManager.getUserContentEdition()
            )
        )
    }

    fun fetchAuthorDetails(
        authorId: Long
    ) = repositoryScope.launch {
        authorDetailFetcher.fetchRemote(
            AuthorDetailFetcher.Params(
                authorId = authorId
            )
        )
    }

    fun getAuthorDetails(
        authorId: Long
    ) = authorDetailLocalDataSource.observeItem(authorId)
}