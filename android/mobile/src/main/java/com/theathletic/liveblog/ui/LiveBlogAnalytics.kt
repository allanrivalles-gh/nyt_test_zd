package com.theathletic.liveblog.ui

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes

interface LiveBlogAnalytics {
    @Suppress("LongParameterList")
    fun trackView(
        blogId: String,
        element: String = "",
        view: String = "blog",
        objectType: String = "blog_id",
        objectId: String = blogId,
        boxScoreState: String
    )

    @Suppress("LongParameterList")
    fun trackClick(
        element: String,
        view: String = "blog",
        objectType: String = "blog_id",
        blogId: String = "",
        objectId: String = blogId,
        articleId: String = "",
        authorId: String = "",
        pageOrder: String = ""
    )

    fun trackSlide(
        blogId: String,
        element: String,
        view: String = "settings_drawer",
        objectType: String = "slider_value",
        objectId: String,
    )

    fun trackOnAdLoad(view: String, pageViewId: String)
}

@Exposes(LiveBlogAnalytics::class)
class LiveBlogAnalyticsHandler @AutoKoin constructor(
    val analytics: Analytics
) : LiveBlogAnalytics {
    override fun trackView(
        blogId: String,
        element: String,
        view: String,
        objectType: String,
        objectId: String,
        boxScoreState: String
    ) {
        analytics.track(
            Event.LiveBlog.View(
                blog_id = blogId,
                element = element,
                view = view,
                object_type = objectType,
                object_id = objectId,
                box_score_state = boxScoreState
            )
        )
    }

    override fun trackClick(
        element: String,
        view: String,
        objectType: String,
        blogId: String,
        objectId: String,
        articleId: String,
        authorId: String,
        pageOrder: String
    ) {
        analytics.track(
            Event.LiveBlog.Click(
                view = view,
                element = element,
                object_type = objectType,
                object_id = objectId,
                blog_id = blogId,
                article_id = articleId,
                author_id = authorId,
                page_order = pageOrder
            )
        )
    }

    override fun trackSlide(
        blogId: String,
        element: String,
        view: String,
        objectType: String,
        objectId: String
    ) {
        analytics.track(
            Event.LiveBlog.Slide(
                blog_id = blogId,
                element = element,
                view = view,
                object_type = objectType,
                object_id = objectId
            )
        )
    }

    override fun trackOnAdLoad(view: String, pageViewId: String) {
        analytics.track(
            Event.Global.AdOnLoad(
                view = view,
                ad_view_id = pageViewId
            )
        )
    }
}