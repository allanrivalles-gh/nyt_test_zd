package com.theathletic.entity.local.merge

import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.main.FeedItemEntryType

object ArticleEntityMerger : EntityMerger<ArticleEntity>() {
    override fun merge(old: ArticleEntity, new: ArticleEntity): ArticleEntity {
        return new.apply {
            // Author info
            authorId = newerLong(old) { authorId }
            authorName = newerString(old) { authorName }
            authorDescription = newerString(old) { authorDescription }
            authorImg = newerString(old) { authorImg }
            authorStatus = newerString(old) { authorStatus }

            // Article data
            articleTitle = newerString(old) { articleTitle }
            articleHeaderImg = newerString(old) { articleHeaderImg }
            articleBody = newerString(old) { articleBody }
            excerpt = newerString(old) { excerpt }

            // Metadata
            commentsCount = newerLong(old) { commentsCount } ?: 0
            permalink = newerString(old) { permalink }
            entryType = when (new.entryType) {
                FeedItemEntryType.UNKNOWN -> old.entryType
                else -> new.entryType
            }

            relatedContent = newerObject(old) { relatedContent }

            lastScrollPercentage = newerInt(old) { lastScrollPercentage } ?: 0
        }.run {
            // TODO: Move all fields into here when converted from var -> val
            copy(
                primaryTag = newerString(old) { primaryTag }
            )
        }
    }
}