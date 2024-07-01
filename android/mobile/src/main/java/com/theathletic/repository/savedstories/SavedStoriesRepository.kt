package com.theathletic.repository.savedstories

import com.theathletic.extension.doAsync
import com.theathletic.user.IUserManager.Companion.NO_USER
import com.theathletic.user.UserManager
import com.theathletic.utility.NetworkManager
import io.reactivex.Maybe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SavedStoriesRepository : KoinComponent {

    private val savedStoriesDao by inject<SavedStoriesDao>()

    private fun getSavedStoriesData() = SavedStoriesData()

    fun cacheData() {
        if (UserManager.getCurrentUserId() != NO_USER) {
            NetworkManager.getInstance().executeWhenOnline {
                getSavedStoriesData().fetchNetwork(true)
            }
        }
    }

    fun clearAllCachedData() = doAsync {
        savedStoriesDao.clear()
    }

    fun markItemRead(articleId: Long, isRead: Boolean) = doAsync {
        savedStoriesDao.markItemRead(articleId, isRead)
    }

    fun markItemBookmarked(articleId: Long, isBookmarked: Boolean) = doAsync {
        if (!isBookmarked)
            savedStoriesDao.delete(articleId)
        else
            cacheData()
    }

    fun getUnreadSavedStoriesIds() = savedStoriesDao.getSavedStories()
        .flatMap { Maybe.just(it.filter { item -> !item.isReadByUser }.mapNotNull { item -> item.id.toLongOrNull() }) }
}