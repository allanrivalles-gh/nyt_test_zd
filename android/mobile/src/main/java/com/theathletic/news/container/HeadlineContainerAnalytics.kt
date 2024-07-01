package com.theathletic.news.container

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin

class HeadlineContainerAnalytics @AutoKoin constructor(
    private val analytics: Analytics
) {

    fun trackView(headlineId: String?, source: String?) {
        val safeId = headlineId ?: return
        analytics.track(
            Event.Headline.View(
                element = "", // Intentional meant to be empty string
                object_type = "headline_id",
                object_id = safeId,
                source = source.orEmpty()
            )
        )
        analytics.track(Event.Headline.HeadlineViewKochava)
    }

    fun trackCommentsOpen(newsId: String) {
        analytics.track(
            Event.Comments.CommentsClick(
                view = "headline",
                object_type = "headline_id",
                object_id = newsId
            )
        )
    }

    fun trackTextStyleClick(newsId: String) {
        analytics.track(
            Event.Headline.TextStyleClick(object_id = newsId)
        )
    }
}