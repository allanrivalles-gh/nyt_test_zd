package com.theathletic.podcast.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.PodcastFeed
import com.theathletic.entity.main.PodcastItem
import com.theathletic.entity.main.PodcastLeagueFeed
import com.theathletic.extension.uniqueBy
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PodcastDao {
    // TT Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPodcastFeedRaw(podcastFeed: PodcastFeed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPodcastLeagueFeed(podcastLeagueFeed: PodcastLeagueFeed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPodcastRaw(podcastItem: PodcastItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPodcastEpisodeRaw(podcastEpisodeItem: PodcastEpisodeItem)

    @Transaction
    open fun insertPodcasts(podcasts: List<PodcastItem>) {
        podcasts.forEach(this::insertPodcast)
    }

    @Transaction
    open fun insertOrUpdatePodcastFollowing(podcast: PodcastItem) {
        val existing = getPodcastRaw(podcast.id)
        if (existing != null) {
            existing.isFollowing = podcast.isFollowing
            insertPodcastRaw(existing)
        } else {
            insertPodcastRaw(podcast)
        }
    }

    @Transaction
    open fun insertPodcast(podcast: PodcastItem) {
        getPodcastRaw(podcast.id)?.let { podcast.topicIds.addAll(it.topicIds) }
        podcast.topicIds.uniqueBy { it }
        insertPodcastRaw(podcast)
    }

    @Transaction
    open fun insertPodcastDetail(podcast: PodcastItem) {
        insertPodcast(podcast)
        val downloadedIds = getPodcastsDownloadedRaw().map { it.id }
        val isUserFeedTagIds = getPodcastUserFeedEpisodesRaw().map { it.id }

        // Clear all previously saved episode items for the current podcast
        clearPodcastEpisodesByPodcastId(podcast.id)

        podcast.episodes.forEach {
            if (downloadedIds.contains(it.id))
                it.isDownloaded = true

            if (isUserFeedTagIds.contains(it.id))
                it.isUserFeed = true

            if (it.imageUrl.isNullOrBlank())
                it.imageUrl = podcast.imageUrl ?: ""

            insertOrUpdatePodcastEpisode(it)
        }
    }

    /**
     * Because we are using one and same PodcastEpisode entity for all calls that should return
     * podcast episodes, we have to "copy" and fill those track, story info every-time. In some cases
     * API is returning the entity without these fields.
     */
    @Transaction
    open fun insertOrUpdatePodcastEpisode(podcastEpisodeItem: PodcastEpisodeItem) {
        val downloadedIds = getPodcastsDownloadedRaw().map { it.id }
        val isUserFeedIds = getPodcastUserFeedEpisodesRaw().map { it.id }

        if (downloadedIds.contains(podcastEpisodeItem.id))
            podcastEpisodeItem.isDownloaded = true

        if (isUserFeedIds.contains(podcastEpisodeItem.id)) {
            podcastEpisodeItem.isUserFeed = true
        }

        if (podcastEpisodeItem.tracks.isEmpty()) {
            val oldEpisode = getPodcastEpisodeRaw(podcastEpisodeItem.id)
            podcastEpisodeItem.tracks = oldEpisode?.tracks ?: podcastEpisodeItem.tracks
        }

        if (podcastEpisodeItem.stories.isEmpty()) {
            val oldEpisode = getPodcastEpisodeRaw(podcastEpisodeItem.id)
            podcastEpisodeItem.stories = oldEpisode?.stories ?: podcastEpisodeItem.stories
        }

        insertPodcastEpisodeRaw(podcastEpisodeItem)
    }

    @Transaction
    open fun insertPodcastFeed(feed: PodcastFeed) {
        insertPodcastFeedRaw(feed)
        setPodcastEpisodeAsNotUserFeed()
        val downloadedIds = getPodcastsDownloadedRaw().map { it.id }
        feed.userPodcastEpisodes.forEach {
            it.isUserFeed = true
            if (downloadedIds.contains(it.id))
                it.isDownloaded = true

            insertOrUpdatePodcastEpisode(it)
        }
    }

    @Transaction
    open fun insertPodcastEpisodeStandalone(podcast: PodcastEpisodeItem) {
        val downloadedIds = getPodcastsDownloadedRaw().map { it.id }
        val isUserFeedTagIds = getPodcastUserFeedEpisodesRaw().map { it.id }

        if (downloadedIds.contains(podcast.id))
            podcast.isDownloaded = true

        if (isUserFeedTagIds.contains(podcast.id))
            podcast.isUserFeed = true

        if (podcast.imageUrl.isNullOrBlank())
            podcast.imageUrl = podcast.imageUrl ?: ""

        insertOrUpdatePodcastEpisode(podcast)
    }

    @Transaction
    open fun insertPodcastEpisodesTransaction(items: List<PodcastEpisodeItem>) {
        items.forEach {
            insertOrUpdatePodcastEpisode(it)
        }
    }

    // TT Gets
    @Query("SELECT * FROM podcast_feed WHERE id == 0")
    abstract fun getPodcastFeed(): Maybe<PodcastFeed>

    @Query("SELECT * FROM podcast_feed WHERE id == 0")
    abstract fun getPodcastFeedFlow(): Flow<PodcastFeed>

    @Query("SELECT * FROM podcast_league_feed WHERE id == :leagueId")
    abstract fun getPodcastLeagueFeed(leagueId: Long): Maybe<PodcastLeagueFeed>

    @Query("SELECT * FROM podcast_item WHERE id == :podcastId")
    abstract fun getPodcast(podcastId: Long): Maybe<PodcastItem>

    @Query("SELECT * FROM podcast_item WHERE id == :podcastId")
    abstract fun getPodcastRaw(podcastId: Long): PodcastItem?

    @Query("SELECT * FROM podcast_episode WHERE id == :episodeId")
    abstract fun getPodcastEpisode(episodeId: Long): Maybe<PodcastEpisodeItem>

    @Query("SELECT * FROM podcast_episode WHERE podcastId == :podcastId AND episodeNumber == :episodeNumber")
    abstract fun getPodcastEpisodeByNumber(podcastId: String, episodeNumber: Int): Maybe<PodcastEpisodeItem>

    @Query("SELECT * FROM podcast_episode WHERE id == :episodeId")
    abstract suspend fun getPodcastEpisodeSuspend(episodeId: Long): PodcastEpisodeItem?

    @Query("SELECT * FROM podcast_episode WHERE id == :episodeId")
    abstract fun getPodcastEpisodeRaw(episodeId: Long): PodcastEpisodeItem?

    @Query("SELECT * FROM podcast_item WHERE topicIds LIKE '%' || :topicId || '%'")
    abstract fun getPodcastsByTopicId(topicId: String): Maybe<List<PodcastItem>>

    @Query("SELECT * FROM podcast_episode WHERE isDownloaded")
    abstract fun getPodcastsDownloaded(): Maybe<List<PodcastEpisodeItem>>

    @Query("SELECT * FROM podcast_episode WHERE isDownloaded")
    abstract fun getPodcastsDownloadedRaw(): List<PodcastEpisodeItem>

    @Query("SELECT * FROM podcast_episode WHERE isDownloaded")
    abstract fun getPodcastsDownloadedFlow(): Flow<List<PodcastEpisodeItem>>

    @Query("SELECT * FROM podcast_episode WHERE isDownloaded")
    abstract suspend fun getPodcastsDownloadedSuspend(): List<PodcastEpisodeItem>

    @Query("SELECT * FROM podcast_episode WHERE podcastId == :podcastId")
    abstract fun getPodcastEpisodes(podcastId: Long): Maybe<List<PodcastEpisodeItem>>

    @Query("SELECT * FROM podcast_episode WHERE podcastId == :podcastId")
    abstract suspend fun getPodcastEpisodesSuspend(podcastId: Long): List<PodcastEpisodeItem>

    @Query("SELECT podcastId FROM podcast_episode WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' LIMIT 1")
    abstract fun getPodcastIdByTitleSearch(query: String): Maybe<Long>

    @Query("SELECT * FROM podcast_item WHERE isFollowing == 1")
    abstract fun getPodcastFollowedList(): Maybe<List<PodcastItem>>

    @Query("SELECT * FROM podcast_item WHERE isFollowing == 1")
    abstract fun getPodcastFollowedFlow(): Flow<List<PodcastItem>>

    @Query("SELECT * FROM podcast_episode WHERE isUserFeed == 1")
    abstract fun getPodcastUserFeedEpisodes(): Maybe<List<PodcastEpisodeItem>>

    @Query("SELECT * FROM podcast_episode WHERE isUserFeed == 1")
    abstract fun getPodcastUserFeedEpisodesRaw(): List<PodcastEpisodeItem>

    @Query("SELECT * FROM podcast_episode WHERE isUserFeed == 1")
    abstract fun getPodcastUserFeedEpisodesFlow(): Flow<List<PodcastEpisodeItem>>

    @Query("SELECT isFollowing FROM podcast_item WHERE id == :id")
    abstract fun getPodcastFollowStatus(id: Long): Maybe<Boolean>

    // TT Sets
    @Query("UPDATE podcast_episode SET isDownloaded = :downloaded WHERE id = :id")
    abstract fun setPodcastEpisodeDownloaded(id: Long, downloaded: Boolean): Int

    @Query("UPDATE podcast_item SET isFollowing = :following WHERE id = :id")
    abstract fun setPodcastFollowStatus(id: Long, following: Boolean): Int

    @Query("UPDATE podcast_episode SET timeElapsed = :position WHERE id = :episodeId")
    abstract fun setPodcastEpisodeProgress(episodeId: Long, position: Int): Int

    @Query("UPDATE podcast_episode SET finished = finished || :finished WHERE id = :episodeId")
    abstract fun setPodcastEpisodeFinished(episodeId: Long, finished: Boolean): Int

    @Query("UPDATE podcast_episode SET isUserFeed = 0")
    abstract fun setPodcastEpisodeAsNotUserFeed(): Int

    @Query("UPDATE podcast_episode SET numberOfComments = :commentsCount WHERE id = :podcastEpisodeId")
    abstract fun setArticleCommentsCount(podcastEpisodeId: Long, commentsCount: Int): Int

    // TT Deletes
    @Query("DELETE FROM podcast_feed")
    abstract fun clearPodcastFeed()

    @Query("DELETE FROM podcast_league_feed")
    abstract fun clearPodcastLeagueFeed()

    @Query("DELETE FROM podcast_episode WHERE isDownloaded == 0")
    abstract fun clearNotDownloadedPodcastEpisodes()

    @Query("DELETE FROM podcast_episode WHERE id == :episodeId")
    abstract fun removePodcastEpisode(episodeId: Long)

    @Query("DELETE FROM podcast_episode WHERE podcastId == :podcastId")
    abstract fun clearPodcastEpisodesByPodcastId(podcastId: Long)

    @Transaction
    open fun clear() {
        clearPodcastFeed()
        clearPodcastLeagueFeed()
        clearNotDownloadedPodcastEpisodes()
    }
}