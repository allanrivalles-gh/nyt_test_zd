package com.theathletic.utility

import android.app.Activity
import android.content.Context
import com.theathletic.analytics.data.ClickSource

/**
 * Interface that wraps ActivityUtility static methods.
 */
interface IActivityUtility {
    fun startArticleActivity(
        context: Context,
        articleId: Long,
        source: ClickSource
    )

    fun startArticleActivity(
        context: Context,
        articleId: Long,
        source: String
    )

    fun startProfileV2Activity(context: Context)

    fun startSearchActivity(context: Context)

    fun startPodcastDownloadedActivity(context: Context)

    fun startAttributionSurveyActivityForResult(
        context: Activity?,
        analyticsSource: String,
        analyticsObjectType: String = "",
        analyticsObjectId: Long = -1L
    )
}