package com.theathletic.article.data.remote

import com.theathletic.datetime.asGMTString
import com.theathletic.entity.SavedStoriesEntity
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.fragment.SavedArticle
import java.util.Date

fun SavedArticle.toArticleEntity(): ArticleEntity {
    return ArticleEntity(
        articleId = id.toLong(),
        articleTitle = title,
        authorName = author.name,
        articleHeaderImg = image_uri,
        articlePublishDate = Date(published_at).asGMTString(),
        commentsCount = comment_count.toLong(),
    )
}

fun SavedArticle.toEntity(): SavedStoriesEntity {
    return SavedStoriesEntity().also {
        it.id = id
        it.postTitle = title
        it.authorName = author.name
        it.postDateGmt = Date(published_at).asGMTString()
        it.postImgUrl = image_uri
        it.commentsCount = comment_count.toLong()
    }
}