package com.theathletic.activity.article

import android.content.Context
import com.theathletic.analytics.data.ClickSource
import com.theathletic.auth.data.AuthenticationRepository
import com.theathletic.datetime.TimeProvider
import com.theathletic.extension.mapRestRequest
import com.theathletic.utility.IActivityUtility
import com.theathletic.utility.IPreferences
import io.reactivex.Single
import java.util.Calendar
import java.util.Date

class ReferredArticleIdManager(
    private val activityUtility: IActivityUtility,
    private val preferences: IPreferences,
    private val timeProvider: TimeProvider,
    private val authenticationRepository: AuthenticationRepository,
    private val articleExpirationDays: Int = 2
) {

    fun fetchAndUpdateArticleIdFromServer(): Single<Long> {
        return authenticationRepository.getReferredArticle()
            .mapRestRequest()
            .toSingle()
            .map { article ->
                if (article.articleId > 0) {
                    setArticleId(article.articleId)
                }
                article.articleId
            }
    }

    fun checkAndRouteToArticle(context: Context) {
        pruneStaleArticle()
        preferences.kochavaArticleId?.toLongOrNull()?.let {
            if (it > 0) {
                activityUtility.startArticleActivity(context, it, ClickSource.REFERRED)
                clearArticleId()
            }
        }
    }

    fun getArticleId(): Long? {
        pruneStaleArticle()
        return preferences.kochavaArticleId?.toLongOrNull()
    }

    fun setArticleId(articleId: Long?) {
        if (articleId != null && preferences.kochavaArticleId?.toLongOrNull() != articleId) {
            preferences.kochavaArticleId = articleId.toString()
            preferences.kochavaArticleDate = timeProvider.currentDate
        }
    }

    fun clearArticleId() {
        preferences.kochavaArticleId = null
        preferences.kochavaArticleDate = null
    }

    private fun pruneStaleArticle() {
        preferences.kochavaArticleId?.toLongOrNull()?.let {
            val date = preferences.kochavaArticleDate
            if (date == null || isStale(date)) {
                clearArticleId()
            }
        }
    }

    private fun isStale(articleDate: Date): Boolean {
        val adjustedArticleDate = Calendar.getInstance().apply {
            time = articleDate
            this.add(Calendar.DAY_OF_MONTH, articleExpirationDays)
        }
        val currentDate = Calendar.getInstance().apply { time = timeProvider.currentDate }
        return adjustedArticleDate.before(currentDate)
    }
}