package com.theathletic.repository

import com.theathletic.extension.doAsync
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.navigation.data.NavigationRepository
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.repository.savedstories.SavedStoriesRepository
import com.theathletic.repository.user.UserDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

object AthleticRepository : KoinComponent {
    private val navigationRepository by inject<NavigationRepository>()
    private val userFollowingRepository by inject<UserFollowingRepository>()

    // TT Clear
    fun clearAllCachedData() {
        Timber.d("[AthleticRepository] Clearing all cached data!")
        SavedStoriesRepository.clearAllCachedData()
        LegacyPodcastRepository.clearAllCachedData()
        UserDataRepository.clearAllCachedData()
        navigationRepository.clearAllCachedData()
        userFollowingRepository.clearFollowing()
    }

    // TT Read item
    fun markItemRead(id: Long, isRead: Boolean) = doAsync {
        Timber.d("[AthleticRepository] Marking article ID: $id as read: $isRead")
        SavedStoriesRepository.markItemRead(id, isRead).get()
        UserDataRepository.markItemRead(id, isRead).get()
    }

    // TT Bookmark item
    fun markItemBookmarked(id: Long, isBookmarked: Boolean, articleVersionNumber: Long = -1L) = doAsync {
        Timber.d("[AthleticRepository] Bookmarking item ID: $id to: $isBookmarked")
        SavedStoriesRepository.markItemBookmarked(id, isBookmarked).get()
        UserDataRepository.markItemBookmarked(id, isBookmarked).get()
    }

    // TT Podcast comment count change
    fun podcastCommentsCountChange(podcastEpisodeId: Long, commentsCount: Int) = doAsync {
        Timber.d("[AthleticRepository] Changing comment count for ID: $podcastEpisodeId to: $commentsCount")
        LegacyPodcastRepository.setPodcastCommentsCount(podcastEpisodeId, commentsCount).get()
    }
}