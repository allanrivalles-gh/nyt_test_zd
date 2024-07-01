package com.theathletic.media.androidauto

import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extLogError
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.repository.resource.Resource
import com.theathletic.user.IUserManager
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * TODO: Convert to using coroutines and repositories
 */
class AndroidAutoMediaLoader @AutoKoin constructor(
    private val userManager: IUserManager
) {

    fun loadSection(
        parentMediaId: String,
        result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>,
        compositeDisposable: CompositeDisposable
    ) {
        when {
            // TT Browsing not allowed
            parentMediaId == AndroidAuto.Section.EMPTY_ROOT -> {
                // Google's docs say to send null here but Pre-N devices crash when null is sent
                // so sending an empty list to Pre-N devices instead.
                // See: https://stackoverflow.com/questions/47791337/onloadchildren-sent-null-list-for-id-root-on-mediabrowserservicecompat-service
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    result.sendResult(emptyList())
                } else {
                    result.sendResult(null)
                }
                return
            }
            // TT Root Item
            parentMediaId == AndroidAuto.Section.ROOT -> {
                result.sendResult(listOf(ANDROID_AUTO_FOLLOWING_ROOT, ANDROID_AUTO_DOWNLOADED_ROOT))
            }
            // TT Following Root
            parentMediaId == AndroidAuto.Section.FOLLOWING_ROOT -> loadFollowedPodcasts(
                compositeDisposable,
                result
            )
            // TT Downloaded Root
            parentMediaId == AndroidAuto.Section.DOWNLOADED_ROOT -> loadDownloadedEpisodes(
                compositeDisposable,
                result
            )
            // TT Followed Item
            parentMediaId.contains(AndroidAuto.Section.FOLLOWING_ITEM_EPISODES) -> loadPodcastEpisodes(
                compositeDisposable,
                parentMediaId,
                result
            )
            // Tt Unknown
            else -> result.sendResult(null)
        }
    }

    private fun loadDownloadedEpisodes(
        compositeDisposable: CompositeDisposable,
        result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        compositeDisposable.add(
            LegacyPodcastRepository.getDownloadedPodcastsList().applySchedulers().subscribe(
                { episodeList ->
                    val mediaItems = episodeList.filter { userManager.isUserSubscribed() || it.isTeaser }
                        .sortedByDescending { it.dateGmt }
                        .map { it.toMediaBrowserItem(AndroidAuto.Items.DOWNLOADED_EPISODE) }

                    result.sendResult(mediaItems)
                },
                {
                    it.extLogError()
                    result.sendResult(null)
                }
            )
        )

        result.detach()
    }

    private fun loadFollowedPodcasts(
        compositeDisposable: CompositeDisposable,
        result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        val data = LegacyPodcastRepository.getPodcastUserFeedData()
        compositeDisposable.add(
            data.getDataObservable().applySchedulers().subscribe(
                { resource ->
                    if (resource.status != Resource.Status.LOADING) {
                        val mediaItems = resource.data?.map {
                            it.toMediaBrowserSection(AndroidAuto.Section.FOLLOWING_ITEM_EPISODES)
                        } ?: emptyList()

                        result.sendResult(mediaItems)
                    }
                },
                {
                    it.extLogError()
                    result.sendResult(null)
                }
            )
        )

        data.load()
        result.detach()
    }

    private fun loadPodcastEpisodes(
        compositeDisposable: CompositeDisposable,
        parentMediaId: String,
        result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        val podcastId = parsePodcastIdFromMediaKey(parentMediaId) ?: return result.sendResult(null)
        val data = LegacyPodcastRepository.getPodcastDetailData(podcastId)
        compositeDisposable.add(
            data.getDataObservable()
                .map { resource ->
                    if (resource.status != Resource.Status.LOADING) {
                        resource.data?.episodes?.forEach {
                            Timber.d("[PodcastService] Saving podcast episode: ${it.id} / ${it.title}")
                            LegacyPodcastRepository.insertPodcastEpisodeStandalone(it)
                        }
                    }
                    resource
                }
                .applySchedulers()
                .subscribe(
                    { resource ->
                        if (resource.status == Resource.Status.LOADING)
                            return@subscribe

                        val mediaItems = resource.data?.episodes
                            ?.filter { userManager.isUserSubscribed() || it.isTeaser }
                            ?.sortedByDescending { it.dateGmt }
                            ?.map { it.toMediaBrowserItem(AndroidAuto.Items.FOLLOWING_EPISODE) } ?: emptyList()

                        result.sendResult(mediaItems)
                    },
                    {
                        it.extLogError()
                        result.sendResult(null)
                    }
                )
        )

        data.load()
        result.detach()
    }

    private fun parsePodcastIdFromMediaKey(mediaId: String) = mediaId.split("_").lastOrNull()?.toLongOrNull()
}