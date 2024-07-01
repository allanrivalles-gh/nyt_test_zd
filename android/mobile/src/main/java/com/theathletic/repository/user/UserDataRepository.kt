package com.theathletic.repository.user

import android.annotation.SuppressLint
import com.theathletic.entity.authentication.UserData
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.doAsync
import com.theathletic.extension.extLogError
import com.theathletic.extension.uniqueBy
import com.theathletic.manager.UserDataManager
import com.theathletic.repository.savedstories.SavedStoriesRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserDataRepository : IUserDataRepository, KoinComponent {

    private val userDataDao by inject<UserDataDao>()

    fun getUserData() = UserDataData()

    fun clearAllCachedData() = doAsync {
        userDataDao.clearUserData()
    }

    override val userDataFlow
        get() = userDataDao.getUserDataFlow()

    var userData: UserData? = null

        set(value) {
            field = value
            if (value != null) {
                userDataDao.insertUserData(value)
            }
        }

    override val totalReadArticleCount
        get() = userData?.articlesRead?.size ?: 0

    @SuppressLint("CheckResult")
    fun updateUnreadSavedStoriesList() {
        SavedStoriesRepository.getUnreadSavedStoriesIds().applySchedulers().subscribe(
            {
                val readArticles = userData?.articlesRead ?: arrayListOf()
                val newList = ArrayList(UserDataManager.unreadSavedStoriesIds)
                newList.addAll(it)
                newList.uniqueBy { id -> id }
                newList.removeAll(readArticles)

                UserDataManager.unreadSavedStoriesIds.clear()
                UserDataManager.unreadSavedStoriesIds.addAll(newList)
            },
            Throwable::extLogError
        )
    }

    override fun isItemRead(id: Long) = userData?.articlesRead?.contains(id) ?: false

    override fun isItemRated(id: Long) = userData?.articlesRated?.contains(id) ?: false

    override fun isItemBookmarked(id: Long) = userData?.articlesSaved?.contains(id) ?: false

    override fun isCommentLiked(id: Long) = userData?.commentsLiked?.contains(id) ?: false

    override fun isCommentFlagged(id: Long) = userData?.commentsFlagged?.contains(id) ?: false

    override fun markItemRead(id: Long, isRead: Boolean) = doAsync {
        userData?.let {
            if (isRead)
                it.articlesRead.add(id)
            else
                it.articlesRead.remove(id)
            it.articlesRead.uniqueBy { it }
            userDataDao.insertUserData(it)
            updateUnreadSavedStoriesList()
        }
    }

    override fun markItemRated(id: Long, isRated: Boolean) {
        doAsync {
            userData?.let {
                if (isRated)
                    it.articlesRated.add(id)
                else
                    it.articlesRated.remove(id)
                it.articlesRated.uniqueBy { it }
                userDataDao.insertUserData(it)
            }
        }
    }

    override fun markItemBookmarked(id: Long, isBookmarked: Boolean) = doAsync {
        userData?.let {
            if (isBookmarked)
                it.articlesSaved.add(id)
            else
                it.articlesSaved.remove(id)
            it.articlesSaved.uniqueBy { it }
            userDataDao.insertUserData(it)

            if (!isBookmarked)
                UserDataManager.unreadSavedStoriesIds.remove(id)

            updateUnreadSavedStoriesList()
        }
    }

    override fun markCommentLiked(id: Long, isLiked: Boolean) = doAsync {
        userData?.let {
            if (isLiked)
                it.commentsLiked.add(id)
            else
                it.commentsLiked.remove(id)
            it.commentsLiked.uniqueBy { it }
            userDataDao.insertUserData(it)
        }
    }

    override fun markCommentFlagged(id: Long, isFlagged: Boolean) = doAsync {
        userData?.let {
            if (isFlagged)
                it.commentsFlagged.add(id)
            else
                it.commentsFlagged.remove(id)
            it.commentsFlagged.uniqueBy { it }
            userDataDao.insertUserData(it)
        }
    }

    override fun savedArticleIds(): List<Long> = userData?.articlesSaved.orEmpty()
}