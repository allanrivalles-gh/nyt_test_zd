package com.theathletic.links

import com.theathletic.feed.FeedType
import timber.log.Timber

class NavigationLinkParser {
    fun toFeedType(deeplink: String): FeedType? {
        val parts = deeplink.split("://")
        if (parts.size != 2) {
            return null
        }
        return parseFeedTypeFromPath(parts[1].split("/"))
    }

    private fun parseFeedTypeFromPath(path: List<String>): FeedType? {
        val prunedPath = path.filterNot { it == "feed" }
        if (prunedPath.isEmpty()) return null
        return try {
            when (prunedPath[0]) {
                "user" -> FeedType.User
                "team" -> FeedType.Team(prunedPath[1].toLong())
                "league" -> FeedType.League(prunedPath[1].toLong())
                "author" -> FeedType.Author(prunedPath[1].toLong())
                "category" -> FeedType.Category(prunedPath[1].toLong(), "")
                "ink" -> FeedType.Ink(prunedPath.getOrNull(1) ?: "")
                else -> null
            }
        } catch (e: NumberFormatException) {
            Timber.e(e)
            null
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
            null
        }
    }

    fun toScoresFeedType(deeplink: String): FeedType? {
        val parts = deeplink.split("://")
        if (parts.size != 2) {
            return null
        }
        return parseScoresFeedTypeFromPath(parts[1].split("/"))
    }

    private fun parseScoresFeedTypeFromPath(path: List<String>): FeedType? {
        val prunedPath = path.filterNot { it == "scores" }
        if (prunedPath.isEmpty()) return null
        return try {
            when (prunedPath[0]) {
                "user" -> FeedType.ScoresToday(0)
                "team" -> FeedType.ScoresTeam(-1, prunedPath[1])
                "league" -> FeedType.ScoresLeague(prunedPath[1].toLong())
                else -> null
            }
        } catch (e: NumberFormatException) {
            Timber.e(e)
            null
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
            null
        }
    }
}