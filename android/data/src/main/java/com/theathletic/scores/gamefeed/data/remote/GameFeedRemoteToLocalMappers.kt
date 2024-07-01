package com.theathletic.scores.gamefeed.data.remote

import com.theathletic.datetime.Datetime
import com.theathletic.fragment.LiveBlogLiteFragment
import com.theathletic.fragment.LiveBlogPostLiteFragment
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.liveblog.data.local.LiveBlogPostEntity

fun LiveBlogPostLiteFragment.toEntity(): LiveBlogPostEntity {
    return LiveBlogPostEntity(
        id = id,
        title = title,
        body = body,
        authorName = user.name,
        relatedArticleId = articles?.firstOrNull()?.id,
        relatedArticleTitle = articles?.firstOrNull()?.title,
        occurredAt = Datetime(occurred_at),
        imageUrl = imgs?.firstOrNull()?.image_uri
    )
}

fun LiveBlogLiteFragment.toEntity(): LiveBlogEntity {
    return LiveBlogEntity(
        id = id,
        title = title,
        description = description.orEmpty(),
        isLive = status == "live",
        permalink = permalink,
        contentUrl = permalinkForEmbed,
        lastActivityAt = Datetime(lastActivityAt),
        imageUrl = imgs?.firstOrNull()?.image_uri
    )
}