package com.theathletic.repository.savedstories

import com.theathletic.article.data.remote.toEntity
import com.theathletic.datetime.DateUtility
import com.theathletic.entity.SavedStoriesEntity
import com.theathletic.feed.data.remote.ArticleGraphqlApi
import com.theathletic.manager.UserDataManager
import com.theathletic.repository.resource.NetworkBoundResource
import com.theathletic.utility.coroutines.DispatcherProvider
import io.reactivex.Maybe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SavedStoriesData : NetworkBoundResource<MutableList<SavedStoriesEntity>>(), KoinComponent {
    private val roomDao by inject<SavedStoriesDao>()
    private val articleApi by inject<ArticleGraphqlApi>()
    private val dateUtility by inject<DateUtility>()
    private val dispatcherProvider by inject<DispatcherProvider>()
    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)

    init {
        callback = object : Callback<MutableList<SavedStoriesEntity>> {
            override fun saveCallResult(response: MutableList<SavedStoriesEntity>) {
                roomDao.updateSavedStoriesList(response)
                UserDataManager.unreadSavedStoriesIds.clear()
                UserDataManager.unreadSavedStoriesIds.addAll(response.filter { !it.isReadByUser }.mapNotNull { it.id.toLongOrNull() })
            }

            override fun loadFromDb() = roomDao.getSavedStories()

            override fun createNetworkCall() = maybeFromSuspendFunction(coroutineScope) {
                val data = articleApi.getSavedStories().data?.userArticles?.map {
                    it.fragments.savedArticle.toEntity()
                } ?: emptyList()
                data.toMutableList()
            }

            override fun mapData(data: MutableList<SavedStoriesEntity>?): MutableList<SavedStoriesEntity>? {
                data?.sortByDescending { dateUtility.parseDateFromGMT(it.postDateGmt).time }
                return data
            }
        }
    }
}

private fun <T> maybeFromSuspendFunction(scope: CoroutineScope, function: suspend () -> T) = Maybe.create { emitter ->
    scope.launch {
        try {
            emitter.onSuccess(function())
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}