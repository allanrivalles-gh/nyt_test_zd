package com.theathletic.viewmodel.main

import android.text.format.DateUtils
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.entity.main.PodcastEpisodeDetailBaseItem
import com.theathletic.entity.main.PodcastEpisodeDetailStoryDividerItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.entity.main.getSortableDate
import com.theathletic.event.CancelSleepTimerEvent
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.SetSleepTimerEvent
import com.theathletic.extension.ObservableString
import com.theathletic.extension.ObservableStringNonNull
import com.theathletic.extension.applySchedulers
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extGetString
import com.theathletic.extension.extLogError
import com.theathletic.extension.runOnUiThread
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.manager.IPodcastManager
import com.theathletic.manager.PodcastManager
import com.theathletic.podcast.data.LegacyPodcastRepository
import com.theathletic.rxbus.RxBus
import com.theathletic.service.PodcastService
import com.theathletic.utility.Preferences
import com.theathletic.utility.formatters.StringFormatUtility
import com.theathletic.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import timber.log.Timber

const val SLEEP_TIMER_EPISODE_END = Long.MAX_VALUE

class PodcastBigPlayerViewModel @AutoKoin constructor(
    private val podcastManager: IPodcastManager,
    featureSwitches: FeatureSwitches,
    private val analytics: Analytics
) : BaseViewModel(), LifecycleObserver {

    val recyclerList = ObservableArrayList<PodcastEpisodeDetailBaseItem>()
    val podcastName: ObservableString = ObservableString("")
    var activeTrack = podcastManager.activeTrack
    var currentBufferProgress = podcastManager.currentBufferProgress
    var currentProgress = ObservableInt(podcastManager.currentProgress.get())
    var currentProgressFormattedString = ObservableStringNonNull(getFormattedCurrentProgressString(currentProgress.get()))
    var timeRemainingFormattedString = ObservableStringNonNull(getFormattedTimeRemainingString(currentProgress.get()))
    var playbackState = podcastManager.playbackState
    var currentPlayBackSpeed = podcastManager.currentPlayBackSpeed
    var playBackSpeedEnabled = podcastManager.playBackSpeedEnabled
    val sleepTimerRunning = ObservableBoolean(false)
    val sleepTimerRemaining = ObservableString("")
    private val timer = Timer()
    private var updateSleepTimerTask: TimerTask = UpdateSleepTimerTask()
    private val compositeDisposable = CompositeDisposable()
    private var currentProgressChangeCallback: Observable.OnPropertyChangedCallback? = null
    private var episodeDetailDataDisposable: Disposable? = null
    private var podcastDataDisposable: Disposable? = null

    internal inner class UpdateSleepTimerTask : TimerTask() {
        override fun run() {
            sleepTimerRemaining.set(getFormattedSleepTimerString())
        }
    }

    enum class SleepTimerOptions(val value: Int) {
        SLEEP_OPTION_5_MINUTES(5),
        SLEEP_OPTION_10_MINUTES(10),
        SLEEP_OPTION_15_MINUTES(15),
        SLEEP_OPTION_30_MINUTES(30),
        SLEEP_OPTION_45_MINUTES(45),
        SLEEP_OPTION_1_HOUR(60),
    }

    init {
        podcastManager.activeTrack.extAddOnPropertyChangedCallback { _, _, _ ->
            checkActivePodcastTrack()
        }.disposeOnCleared()

        checkActivePodcastTrack()
        setupCurrentProgressChangeListener()
        registerRxBusListeners()

        analytics.track(
            Event.Podcast.View(
                view = "podcast_player",
                element = "player",
                object_type = "podcast_episode_id",
                object_id = podcastManager.activeTrack.get()?.episodeId?.toString() ?: ""
            )
        )
    }

    override fun onCleared() {
        removeCurrentProgressChangeListener()
        episodeDetailDataDisposable?.dispose()
        podcastDataDisposable?.dispose()
        super.onCleared()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (sleepTimerRunning.get() ||
            shouldRecoverSleepTimer()
        ) {
            sleepTimerRunning.set(true)
            startSleepFixedRateTimer()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (sleepTimerRunning.get()) {
            stopSleepFixedRateTimer()
        }
    }

    fun onSeekTrackingChangeFromUser(progress: Int) {
        currentProgress.set(progress)
        currentProgressFormattedString.set(getFormattedCurrentProgressString(progress))
        timeRemainingFormattedString.set(getFormattedTimeRemainingString(progress))
    }

    fun onSeekTrackingStart() {
        removeCurrentProgressChangeListener()
    }

    fun onSeekTrackingStop() {
        podcastManager.currentProgress.set(currentProgress.get())
        setupCurrentProgressChangeListener()
    }

    private fun shouldRecoverSleepTimer(): Boolean {
        return (Preferences.podcastSleepTimestampMillis > Date().time) ||
            (
                Preferences.podcastSleepTimestampMillis == SLEEP_TIMER_EPISODE_END &&
                    PodcastService().isInForegroundServiceState()
                )
    }

    private fun sleepDelayEvent(delayInMinutes: Int) {
        sleepTimerRunning.set(true)
        // Tt future time in millis when alarm should fire and stop the playback
        val sleepTimerAlarmTimeMillis = Date().time + (delayInMinutes * DateUtils.MINUTE_IN_MILLIS)
        Preferences.podcastSleepTimestampMillis = sleepTimerAlarmTimeMillis
        sendEvent(SetSleepTimerEvent(sleepTimerAlarmTimeMillis))
        startSleepFixedRateTimer()

        trackSleepTimerClick(delayInMinutes)
    }

    private fun sleepCancelEvent() {
        sleepTimerRunning.set(false)
        Preferences.clearPodcastSleepTimestamp()
        sleepTimerRemaining.set("")
        stopSleepFixedRateTimer()
        sendEvent(CancelSleepTimerEvent())

        trackSleepTimerClick(0)
    }

    private fun sleepAfterEpisodeEvent() {
        sleepTimerRunning.set(true)
        // Tt we are checking this flag in PodcastService.mediaSessionCallback.onSkipToNext()
        // Tt or clearing it on app start if nothing is playing at that moment
        Preferences.podcastSleepTimestampMillis = SLEEP_TIMER_EPISODE_END
        startSleepFixedRateTimer()

        trackSleepTimerClick(100)
    }

    private fun startSleepFixedRateTimer() {
        updateSleepTimerTask = UpdateSleepTimerTask()
        timer.scheduleAtFixedRate(updateSleepTimerTask, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(1))
    }

    private fun stopSleepFixedRateTimer() {
        updateSleepTimerTask.cancel()
    }

    private fun registerRxBusListeners() {
        compositeDisposable.add(
            RxBus.instance.register(PodcastSleepTimerViewModel.SleepDelayEvent::class.java)
                .subscribe({ sleepDelayEvent(it.delayInMinutes) }, { it.extLogError() })
        )
        compositeDisposable.add(
            RxBus.instance.register(PodcastSleepTimerViewModel.SleepCancelEvent::class.java)
                .subscribe({ sleepCancelEvent() }, { it.extLogError() })
        )
        compositeDisposable.add(
            RxBus.instance.register(PodcastSleepTimerViewModel.SleepAfterEpisodeEvent::class.java)
                .subscribe({ sleepAfterEpisodeEvent() }, { it.extLogError() })
        )
        // Tt from alarm brodcast receiver - to get UI back to "sleep timer turned off" state
        compositeDisposable.add(
            RxBus.instance.register(RxBus.SleepTimerPauseEvent::class.java)
                .subscribe({ sleepCancelEvent() }, { it.extLogError() })
        )
    }

    private fun checkActivePodcastTrack() {
        val episodeId = podcastManager.activeTrack.get()?.episodeId
        val podcastId = podcastManager.activeTrack.get()?.podcastId

        if (episodeId != null && podcastId != null) {
            setupListenerForEpisodeId(episodeId, podcastId)
        } else {
            recyclerList.clear()
            sendEvent(DataChangeEvent())
        }
    }

    private fun setupListenerForEpisodeId(episodeId: Long, podcastId: Long) {
        val episodeDetailData = LegacyPodcastRepository.getPodcastEpisodeDetailData(episodeId = episodeId)
        episodeDetailDataDisposable?.dispose()
        episodeDetailDataDisposable = episodeDetailData.getDataObservable().applySchedulers().subscribe(
            { resource ->
                Timber.i("[PodcastBigPlayerViewModel] Episode Detail - Observer status: ${resource.status} Observer value: ${resource.data} track size: ${resource.data?.tracks?.size}")
                resource?.data?.let { processData(it) }
            },
            Throwable::extLogError
        )
        episodeDetailData.load()

        val podcastData = LegacyPodcastRepository.getPodcastDetailData(podcastId = podcastId)
        podcastDataDisposable?.dispose()
        podcastDataDisposable = podcastData.getDataObservable().applySchedulers().subscribe(
            { resource ->
                Timber.i("[PodcastBigPlayerViewModel] Podcast - Observer status: ${resource.status} Observer value: ${resource.data}")
                resource?.data?.let {
                    podcastName.set(it.title)
                }
            },
            Throwable::extLogError
        )
        podcastData.loadOnlyCache()
    }

    private fun processData(data: PodcastEpisodeItem) {
        recyclerList.clear()
        recyclerList.addAll(data.tracks.sortedBy { it.trackNumber })

        val sortedStoriesList = data.stories.sortedBy { it.getSortableDate() }
        sortedStoriesList.forEach {
            recyclerList.add(it)
            if (it != sortedStoriesList.last())
                recyclerList.add(PodcastEpisodeDetailStoryDividerItem())
        }

        runOnUiThread { sendEvent(DataChangeEvent()) }
    }

    private fun getFormattedCurrentProgressString(progress: Int): String {
        val currentSec = progress / 1000
        val hours = currentSec / 3600
        val minutes = (currentSec % 3600) / 60
        val seconds = currentSec % 60

        return StringFormatUtility.formatHoursMinutesSecondsTime(hours, minutes, seconds)
    }

    private fun getFormattedTimeRemainingString(progress: Int): String {
        var remainingSec = (PodcastManager.maxDuration.get() / 1000) - (progress / 1000)

        if (remainingSec < 0)
            remainingSec = 0

        val remainingHours = remainingSec / 3600
        val remainingMinutes = (remainingSec % 3600) / 60
        val remainingSeconds = remainingSec % 60

        return StringFormatUtility.formatHoursMinutesSecondsTimeRemaining(remainingHours, remainingMinutes, remainingSeconds)
    }

    private fun getFormattedSleepTimerString(): String {
        if (!sleepTimerRunning.get()) {
            return ""
        }

        if (Preferences.podcastSleepTimestampMillis == SLEEP_TIMER_EPISODE_END) {
            return R.string.podcast_sleep_timer_button_episode_end.extGetString()
        }

        var remainingSec = (Preferences.podcastSleepTimestampMillis - Date().time) / DateUtils.SECOND_IN_MILLIS

        if (remainingSec < 0)
            remainingSec = 0

        val hours = remainingSec / 3600
        val minutes = (remainingSec % 3600) / 60
        val seconds = remainingSec % 60

        return StringFormatUtility.formatHoursMinutesSecondsTime(hours.toInt(), minutes.toInt(), seconds.toInt())
    }

    private fun removeCurrentProgressChangeListener() {
        currentProgressChangeCallback?.let { podcastManager.currentProgress.removeOnPropertyChangedCallback(it) }
    }

    private fun setupCurrentProgressChangeListener() {
        removeCurrentProgressChangeListener()
        currentProgressChangeCallback = podcastManager.currentProgress.extAddOnPropertyChangedCallback { _, _, _ ->
            currentProgress.set(podcastManager.currentProgress.get())
            currentProgressFormattedString.set(getFormattedCurrentProgressString(currentProgress.get()))
            timeRemainingFormattedString.set(getFormattedTimeRemainingString(currentProgress.get()))
        }
    }

    private fun trackSleepTimerClick(duration: Int) {
        analytics.track(
            Event.Podcast.Click(
                view = "podcast_player",
                element = "sleep_timer",
                object_type = "timer_length",
                object_id = duration.toString()
            )
        )
    }
}