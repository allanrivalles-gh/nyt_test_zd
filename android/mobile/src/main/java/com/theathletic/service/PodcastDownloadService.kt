package com.theathletic.service

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment.DIRECTORY_PODCASTS
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.theathletic.R
import com.theathletic.entity.main.PodcastDownloadEntity
import com.theathletic.extension.doAsync
import com.theathletic.extension.extLogError
import com.theathletic.extension.runOnUiThread
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.podcast.download.PodcastDownloadStateStore
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import timber.log.Timber

/**
 * This service should take care of downloading multiple podcast episodes at the time.
 *
 * It should be started with the first download request, and destroyed once all items are downloaded.
 * We are going to use [BehaviorSubject] to notify ViewModels about current state. This way we can listen to updates,
 * and also grab it value once the ViewModel is created.
 */
class PodcastDownloadService : Service(), KoinComponent {
    companion object {
        private const val PODCAST_EPISODE_ID = "podcast_episode_id"
        private const val PODCAST_EPISODE_NAME = "podcast_episode_name"
        private const val DOWNLOAD_PATH = "download_path"

        /**
         * This method will call [startService] method, which will enqueue new download request in the service.
         */
        fun downloadFile(
            context: Context,
            podcastEpisodeId: Long,
            podcastEpisodeName: String,
            downloadUrl: String
        ) {
            Timber.v("[PodcastDownloadService] downloadFile($context, $podcastEpisodeId, $podcastEpisodeName, $downloadUrl)")
            checkAndDisplayBatterySaverToast(context)
            context.startService(
                Intent(context, PodcastDownloadService::class.java)
                    .putExtra(PODCAST_EPISODE_ID, podcastEpisodeId)
                    .putExtra(PODCAST_EPISODE_NAME, podcastEpisodeName)
                    .putExtra(DOWNLOAD_PATH, downloadUrl)
            )
        }

        /**
         * We will let service know that the episode download should be canceled.
         */
        fun cancelDownload(context: Context, podcastEpisodeId: Long) {
            Timber.v("[PodcastDownloadService] cancelDownload($context, $podcastEpisodeId)")
            context.startService(
                Intent(context, PodcastDownloadService::class.java)
                    .putExtra(PODCAST_EPISODE_ID, podcastEpisodeId)
            )
        }

        private fun checkAndDisplayBatterySaverToast(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (powerManager.isPowerSaveMode) {
                runOnUiThread {
                    Toast.makeText(
                        context,
                        context.getString(R.string.podcast_download_power_saving_warning),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val context: Context by inject(named("application-context"))
    private val podcastDownloadStore: PodcastDownloadStateStore by inject()
    private var compositeDisposable = CompositeDisposable()
    private var isDownloadCompleteBroadcastReceiverRegistered = false
    private val podcastDownloadCompleteBroadcastReceiver = object : BroadcastReceiver() {
        private fun getLastRedirectUrl(url: String): String {
            var responseCode = 302
            var location = url
            var redirectCount = 0

            try {
                while (responseCode == 302 && redirectCount < 15) {
                    val con = URL(location).openConnection() as HttpURLConnection
                    con.instanceFollowRedirects = false
                    con.connect()
                    responseCode = con.responseCode

                    if (responseCode == 200 || !con.headerFields.containsKey("Location")) {
                        con.disconnect()
                        Timber.v("[PodcastDownloadService] getLastRedirectUrl() -> Final location: $location")
                        break
                    }
                    Timber.v("[PodcastDownloadService] getLastRedirectUrl() -> Next location: $location")

                    location = con.getHeaderField("Location")
                    con.disconnect()
                    redirectCount++
                }
            } catch (e: IOException) {
                e.extLogError()
            }

            return location
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.v("[PodcastDownloadService] onReceive($context, $intent)")
            val downloadId = intent?.extras?.getLong(DownloadManager.EXTRA_DOWNLOAD_ID) ?: return
            val downloadItem = podcastDownloadStore.getEntityByDownloadId(downloadId) ?: return
            val downloadManager = (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId)) ?: return

            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Timber.v("[PodcastDownloadService] onReceive -> STATUS_SUCCESSFUL")
                        downloadItem.downloadId = -1L
                        downloadItem.markAsDownloaded()
                        podcastDownloadStore.updateEntity(downloadItem)
                        LegacyPodcastRepository.setPodcastEpisodeDownloaded(downloadItem.podcastEpisodeId, true)
                        disposeDownloadCompleteBroadcastReceiverIfNeeded()
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val errorCode = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                        if (errorCode == DownloadManager.ERROR_TOO_MANY_REDIRECTS) {
                            Timber.w("[PodcastDownloadService] onReceive -> STATUS_FAILED -> ERROR_TOO_MANY_REDIRECTS")
                            val requestUrl = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI))
                            doAsync {
                                val finalURL = getLastRedirectUrl(requestUrl)
                                startDownload(downloadItem.podcastEpisodeId, downloadItem.podcastEpisodeName, finalURL)
                            }
                        } else {
                            Timber.w("[PodcastDownloadService] onReceive -> STATUS_FAILED -> Error Code: $errorCode")
                            stopDownload(downloadItem.podcastEpisodeId)
                            LegacyPodcastRepository.setPodcastEpisodeDownloaded(downloadItem.podcastEpisodeId, false)
                            disposeDownloadCompleteBroadcastReceiverIfNeeded()
                        }
                    }
                }
            } else {
                stopDownload(downloadItem.podcastEpisodeId)
                LegacyPodcastRepository.setPodcastEpisodeDownloaded(downloadItem.podcastEpisodeId, false)
            }

            cursor.close()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Service entry method. We will decide by the extras content if the user wanted to start or stop the download.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val podcastEpisodeId = intent?.getLongExtra(PODCAST_EPISODE_ID, -1L) ?: return START_STICKY
        val podcastEpisodeName = intent.getStringExtra(PODCAST_EPISODE_NAME) ?: ""
        val downloadPath = intent.getStringExtra(DOWNLOAD_PATH) ?: ""
        if (downloadPath.isNotBlank())
            startDownload(podcastEpisodeId, podcastEpisodeName, downloadPath)
        else
            stopDownload(podcastEpisodeId)

        return START_STICKY
    }

    override fun onDestroy() {
        if (isDownloadCompleteBroadcastReceiverRegistered) {
            context.unregisterReceiver(podcastDownloadCompleteBroadcastReceiver)
            compositeDisposable.dispose()
            super.onDestroy()
        }
    }

    /**
     * This will setup download complete receiver, create download request and enqueue it in [DownloadManager].
     * We will also update the [PodcastDownloadEntity], and setup download Observable to observer the download progress
     */
    private fun startDownload(
        podcastEpisodeId: Long,
        podcastEpisodeName: String,
        downloadPath: String
    ) {
        Timber.v("[PodcastDownloadService] startDownload($podcastEpisodeId, $podcastEpisodeName, $downloadPath)")
        setupDownloadCompleteBroadcastReceiverIfNeeded()
        val downloadManager = (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
        val request = DownloadManager.Request(Uri.parse(downloadPath))

        // Tt Tell on which network you want to download file.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)

        // Tt This will show notification on top when downloading the file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        // Tt Title for notification.
        request.setTitle(podcastEpisodeName)
        @Suppress("DEPRECATION")
        request.setVisibleInDownloadsUi(true) // From API 29 value ignored
        request.setDestinationInExternalFilesDir(
            context, DIRECTORY_PODCASTS,
            LegacyPodcastRepository.getPodcastLocalFileSubPath(podcastEpisodeId)
        )

        // Tt Check and delete the file before downloading. Just to be sure.
        val file = File(LegacyPodcastRepository.getPodcastLocalFilePath(podcastEpisodeId))
        if (file.exists())
            file.delete()

        // Tt Enqueue the request
        val enqueuedId = downloadManager.enqueue(request) // This will start downloading

        // Tt Init downloadItem for storing information
        val downloadItem = podcastDownloadStore.getEntity(podcastEpisodeId)
        downloadItem.downloadId = enqueuedId
        downloadItem.podcastEpisodeName = podcastEpisodeName
        downloadItem.setProgress(0L)
        podcastDownloadStore.updateEntity(downloadItem)

        setupDownloadObservable(downloadManager, downloadItem, enqueuedId)
    }

    /**
     * We will check [DownloadManager] periodically and update the progress inside [PodcastDownloadEntity] periodically.
     */
    private fun setupDownloadObservable(
        downloadManager: DownloadManager,
        downloadItem: PodcastDownloadEntity,
        enqueuedId: Long
    ) {
        Timber.v("[PodcastDownloadService] setupDownloadObservable()")
        val query = DownloadManager.Query().apply { setFilterById(enqueuedId) }
        val progressUpdateInterval = 200L
        var oldProgress = 0L

        compositeDisposable.add(
            Observable
                .fromCallable {
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val size = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val downloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        if (size > 0)
                            downloadItem.setProgress((downloaded * 100.0 / size).toLong())
                    }
                    cursor.close()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .delay(progressUpdateInterval, TimeUnit.MILLISECONDS)
                .repeatUntil { !downloadItem.isDownloading() }
                .distinctUntilChanged { _, _ -> oldProgress == downloadItem.progress }
                .subscribe(
                    {
                        oldProgress = downloadItem.progress
                        podcastDownloadStore.updateEntity(downloadItem)
                    },
                    Throwable::extLogError,
                    {
                        downloadItem.downloadId = -1L
                    }
                )
        )
    }

    /**
     * This will will remove the download from [DownloadManager] and mark item as not downloaded in [episodesSubject]
     */
    private fun stopDownload(podcastEpisodeId: Long) {
        val downloadItem = podcastDownloadStore.getEntity(podcastEpisodeId)
        val downloadManager = (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)

        downloadManager.remove(downloadItem.downloadId)
        downloadItem.downloadId = -1L
        downloadItem.markAsNotDownloaded()
        podcastDownloadStore.updateEntity(downloadItem)
    }

    private fun setupDownloadCompleteBroadcastReceiverIfNeeded() {
        if (!isDownloadCompleteBroadcastReceiverRegistered) {
            ContextCompat.registerReceiver(
                context,
                podcastDownloadCompleteBroadcastReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_EXPORTED
            )
            isDownloadCompleteBroadcastReceiverRegistered = true
        }
    }

    private fun disposeDownloadCompleteBroadcastReceiverIfNeeded() {
        if (isDownloadCompleteBroadcastReceiverRegistered &&
            !podcastDownloadStore.hasDownloadsInProgress()
        ) {
            context.unregisterReceiver(podcastDownloadCompleteBroadcastReceiver)
            isDownloadCompleteBroadcastReceiverRegistered = false

            // Tt stop the service
            stopSelf()
        }
    }
}