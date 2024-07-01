package com.theathletic.entity.local.merge

import com.theathletic.entity.local.AthleticEntity

/**
 * This class is responsible for merging two different versions of the same entity, resulting in
 * a new entity with the most up-to-date information.
 *
 * This is needed as different GraphQL queries can result in the same local model
 * (e.g. [ArticleEntity]) but have different levels of details.
 *
 * So feed might only load the title and author names, but the article detail screen needs the
 * article content. If we already have the content of an article and refresh the feed, we don't
 * want the lesser-detail version model to override the content that we already have in the DB, so
 * we can merge model we already have (with more detail) with the model just fetched from the server
 * (lesser detail) to gain the updates from the new fetch but not lose the fields we previously had.
 */
abstract class EntityMerger<T : AthleticEntity> {

    /**
     * Returns a model which is the best guess of up-to-date information between an older model
     * and a newer model of potentially differing detail levels.
     */
    abstract fun merge(old: T, new: T): T

    fun T.newerString(old: T, block: T.() -> String?): String? {
        val newString = this.block()
        if (!newString.isNullOrEmpty()) {
            return newString
        }
        val oldString = old.block()
        if (!oldString.isNullOrEmpty()) {
            return oldString
        }
        return null
    }

    fun T.newerLong(old: T, block: T.() -> Long?): Long? {
        val newValue = this.block()
        if (newValue != null) {
            return newValue
        }
        val oldValue = old.block()
        if (oldValue != null) {
            return oldValue
        }
        return null
    }

    fun <O : Any?> T.newerObject(old: T, block: T.() -> O): O? {
        val newValue = this.block()
        if (newValue != null) {
            return newValue
        }

        return old.block()
    }

    fun T.newerInt(old: T, block: T.() -> Int?): Int? {
        val newValue = this.block()
        if (newValue != null) {
            return newValue
        }
        val oldValue = old.block()
        if (oldValue != null) {
            return oldValue
        }
        return null
    }
}