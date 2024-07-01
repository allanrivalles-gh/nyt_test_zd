package com.theathletic.fragment.main

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.app.AlarmManagerCompat
import androidx.databinding.Observable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theathletic.BR
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.adapter.main.PodcastBigPlayerAdapter
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.data.ContentType
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.databinding.FragmentPodcastBigPlayerBinding
import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.getBestSource
import com.theathletic.event.CancelSleepTimerEvent
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.SetSleepTimerEvent
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extGetColor
import com.theathletic.extension.extGetDimensionPixelSize
import com.theathletic.extension.extLogError
import com.theathletic.manager.PodcastManager
import com.theathletic.receiver.SleepTimerAlarmReceiver
import com.theathletic.rxbus.RxBus
import com.theathletic.service.PodcastService
import com.theathletic.service.PodcastServicePlaybackAction
import com.theathletic.ui.main.PodcastBigPlayerView
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.ElevationAnimator
import com.theathletic.utility.NetworkManager
import com.theathletic.viewmodel.main.PodcastBigPlayerViewModel
import io.reactivex.disposables.CompositeDisposable
import java.util.Date
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val SLEEP_TIMER_ALARM_REQUEST_CODE = 2277

class PodcastBigPlayerSheetDialogFragment : BottomSheetDialogFragment(), PodcastBigPlayerView {
    private val viewModel by viewModel<PodcastBigPlayerViewModel>()
    private val analytics by inject<Analytics>()
    private lateinit var binding: FragmentPodcastBigPlayerBinding
    private lateinit var recyclerAdapter: PodcastBigPlayerAdapter
    private var activeTrackChangeCallback: Observable.OnPropertyChangedCallback? = null
    private var progressChangeCallback: Observable.OnPropertyChangedCallback? = null
    private var maxDurationChangeCallback: Observable.OnPropertyChangedCallback? = null
    private val compositeDisposable = CompositeDisposable()
    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent? = null

    companion object {
        fun newInstance() = PodcastBigPlayerSheetDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        bottomSheetDialog.setOnShowListener { dialog ->
            (dialog as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet)?.let {
                val behavior = BottomSheetBehavior.from(it)
                val toolbarHeight = R.dimen.podcast_big_player_toolbar_height.extGetDimensionPixelSize()
                behavior.peekHeight = binding.bottomBarContent.height + toolbarHeight
            }
        }

        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        viewModel.observeEvent(this, DataChangeEvent::class.java, Observer { onDataChangeEvent() })
        viewModel.observeEvent(this, SetSleepTimerEvent::class.java, Observer { onSetSleepTimerEvent(it.sleepDelay) })
        viewModel.observeEvent(this, CancelSleepTimerEvent::class.java, Observer { onCancelSleepTimerEvent() })
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Athletic_BigPlayerSheetDialog)

        compositeDisposable.add(
            RxBus.instance.register(RxBus.SwitchToPodcastDetailEvent::class.java).subscribe(
                {
                    dismiss()
                },
                Throwable::extLogError
            )
        )

        compositeDisposable.add(
            RxBus.instance.register(RxBus.SwitchToPodcastEpisodeDetailEvent::class.java).subscribe(
                {
                    dismiss()
                },
                Throwable::extLogError
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPodcastBigPlayerBinding.inflate(layoutInflater)
        binding.setVariable(BR.view, this)
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupListeners()

        // Setup ElevationAnimator to animate shadow under the toolbar
        ElevationAnimator(binding.scrollview, binding.toolbar, R.dimen.global_elevation_8.extGetDimensionPixelSize().toFloat())
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        activeTrackChangeCallback?.let { PodcastManager.activeTrack.removeOnPropertyChangedCallback(it) }
        maxDurationChangeCallback?.let { PodcastManager.maxDuration.removeOnPropertyChangedCallback(it) }
        removeTrackProgressListener()
        super.onDestroy()
    }

    override fun onOpenDetailOverlayClick() {
        activity?.let { ActivityUtility.startMainActivityNewTask(it) }
        PodcastManager.activeTrack.get()?.let { RxBus.instance.post(RxBus.SwitchToPodcastEpisodeDetailEvent(it.id, it.podcastId)) }
    }

    override fun onPlayPauseClick() {
        val activePodcastId = PodcastManager.activeTrack.get()?.id
        compositeDisposable.add(
            when (PodcastManager.playbackState.get()) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    firePauseEvent(activePodcastId)
                    PodcastManager.getTransportControlsSingle().subscribe({ it.pause() }, Throwable::extLogError)
                }
                PlaybackStateCompat.STATE_PAUSED,
                PlaybackStateCompat.STATE_STOPPED -> {
                    firePlayEvent(activePodcastId)
                    PodcastManager.getTransportControlsSingle().subscribe({ it.play() }, Throwable::extLogError)
                }
                PlaybackStateCompat.STATE_NONE,
                PlaybackStateCompat.STATE_ERROR -> PodcastManager.getTransportControlsSingle().subscribe(
                    {
                        val activeTrack = PodcastManager.activeTrack.get()
                        if (NetworkManager.getInstance().isOffline()) {
                            showSnackbar(R.string.global_network_offline)
                        } else if (activeTrack != null) {
                            firePlayEvent(activeTrack.id)
                            val bundle = Bundle()
                            bundle.putInt(PodcastService.EXTRAS_START_PROGRESS_SECONDS, PodcastManager.currentProgress.get() / 1000)
                            it.playFromUri(Uri.parse(activeTrack.getBestSource()), bundle)
                        }
                    },
                    Throwable::extLogError
                )
                else -> PodcastManager.getTransportControlsSingle().subscribe({ it.pause() }, Throwable::extLogError)
            }
        )
    }

    override fun onBackwardClick() {
        compositeDisposable.add(PodcastManager.getTransportControlsSingle().subscribe({ it.sendCustomAction(PodcastServicePlaybackAction.BACKWARD_10_SEC.value, null) }, Throwable::extLogError))
    }

    override fun onForwardClick() {
        compositeDisposable.add(PodcastManager.getTransportControlsSingle().subscribe({ it.sendCustomAction(PodcastServicePlaybackAction.FORWARD_10_SEC.value, null) }, Throwable::extLogError))
    }

    override fun onChangeSpeedClick() {
        compositeDisposable.add(PodcastManager.getTransportControlsSingle().subscribe({ it.fastForward() }, Throwable::extLogError))
    }

    override fun onOpenQueueClick() {
        showToast("not implemented")
    }

    override fun onSleepTimerClick() {
        PodcastSleepTimerSheetDialogFragment.newInstance(viewModel.sleepTimerRunning.get())
            .show(requireActivity().supportFragmentManager, "podcast_sleep_timer_bottom_bar_sheet")
    }

    override fun onShareClick() {
        val permalinkURL = PodcastManager.activeTrack.get()?.permalinkUrl ?: ""
        val title = PodcastManager.activeTrack.get()?.title ?: ""
        val episodeId = PodcastManager.activeTrack.get()?.episodeId?.toString() ?: ""

        activity?.let { ActivityUtility.startShareTextActivity(it, resources.getString(R.string.podcast_share_title), permalinkURL) }
        analytics.track(
            Event.Global.GenericShare(
                "Link",
                title,
                ContentType.PODCAST_EPISODE.value,
                episodeId
            )
        )
    }

    override fun onTrackItemClick(track: PodcastEpisodeDetailTrackItem) {
        // Seek to correct position
        compositeDisposable.add(PodcastManager.getTransportControlsSingle().subscribe({ it.seekTo(track.startPosition * 1_000) }, Throwable::extLogError))

        // Play if not playing
        if (PodcastManager.playbackState.get() != PlaybackStateCompat.STATE_PLAYING)
            onPlayPauseClick()
    }

    override fun onStoryItemClick(item: PodcastEpisodeDetailStoryItem) {
        context?.let { ActivityUtility.startArticleActivity(it, item.id, ClickSource.PODCAST_STORY) }
    }

    override fun onCloseClick() {
        dismiss()
    }

    override fun showToast(stringRes: Int) {
        (activity as? BaseActivity)?.showToast(stringRes)
    }

    override fun showToast(message: String) {
        (activity as? BaseActivity)?.showToast(message)
    }

    override fun showSnackbar(stringRes: Int) {
        (activity as? BaseActivity)?.showSnackbar(stringRes)
    }

    override fun showSnackbar(message: String) {
        (activity as? BaseActivity)?.showSnackbar(message)
    }

    override fun viewLifecycleOwnerProducer() = viewLifecycleOwner

    private fun onSetSleepTimerEvent(sleepTimerAlarmTimeMillis: Long) {
        onCancelSleepTimerEvent()
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarmIntent = Intent(context, SleepTimerAlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                SLEEP_TIMER_ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Tt alarm delay needs to be calculated based on device uptime.
        // Tt We use that and add difference between intended alarm time and now
        alarmManager?.also { manager ->
            val intent = alarmIntent ?: return
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                manager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (sleepTimerAlarmTimeMillis - Date().time),
                intent
            )
        }
    }

    private fun onCancelSleepTimerEvent() {
        alarmIntent?.also { intent -> alarmManager?.cancel(intent) }
    }

    private fun onDataChangeEvent() {
        recyclerAdapter.notifyDataSetChanged()
        setTrackProgressListener()
    }

    private fun setupAdapter() {
        recyclerAdapter = PodcastBigPlayerAdapter(this, viewModel.recyclerList)
        binding.recycler.adapter = recyclerAdapter
    }

    private fun setupListeners() {
        activeTrackChangeCallback = PodcastManager.activeTrack.extAddOnPropertyChangedCallback { value ->
            setTrackProgressListener()
            if (value == null && !isStateSaved) {
                dismiss()
            }
        }
        maxDurationChangeCallback = PodcastManager.maxDuration.extAddOnPropertyChangedCallback { value ->
            binding.seekBar.max = value
        }

        binding.seekBar.progress = viewModel.currentProgress.get()
        binding.seekBar.max = PodcastManager.maxDuration.get()
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    viewModel.onSeekTrackingChangeFromUser(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewModel.onSeekTrackingStart()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    seekBar?.progressDrawable?.colorFilter = BlendModeColorFilter(R.color.red.extGetColor(), BlendMode.SRC_IN)
                } else {
                    @Suppress("DEPRECATION")
                    seekBar?.progressDrawable?.setColorFilter(R.color.red.extGetColor(), PorterDuff.Mode.SRC_IN)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.onSeekTrackingStop()
                compositeDisposable.add(PodcastManager.getTransportControlsSingle().subscribe({ it.seekTo(PodcastManager.currentProgress.get().toLong()) }, Throwable::extLogError))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    seekBar?.progressDrawable?.colorFilter = BlendModeColorFilter(R.color.white.extGetColor(), BlendMode.SRC_IN)
                } else {
                    @Suppress("DEPRECATION")
                    seekBar?.progressDrawable?.setColorFilter(R.color.white.extGetColor(), PorterDuff.Mode.SRC_IN)
                }
            }
        })
    }

    private fun setTrackProgressListener() {
        var lastPosition = -1
        removeTrackProgressListener()
        progressChangeCallback = PodcastManager.currentProgress.extAddOnPropertyChangedCallback { progress ->
            // Let's do check for lastPosition so we handle less of those events.
            val position = progress / 1_000
            if (lastPosition != position) {
                lastPosition = position
                viewModel.recyclerList.filterIsInstance<PodcastEpisodeDetailTrackItem>().forEach {
                    it.isCurrentlyPlayingTrack.set(it.startPosition <= position && it.endPosition > position)
                }
            }
        }
    }

    private fun removeTrackProgressListener() {
        progressChangeCallback?.let { PodcastManager.currentProgress.removeOnPropertyChangedCallback(it) }
        viewModel.recyclerList.filterIsInstance<PodcastEpisodeDetailTrackItem>().forEach {
            it.isCurrentlyPlayingTrack.set(false)
        }
    }

    private fun firePlayEvent(activeTrackId: Long?) {
        activeTrackId?.let {
            analytics.track(
                Event.Podcast.Play(
                    view = "podcast_player",
                    element = "player",
                    object_id = it.toString()
                )
            )
        }
    }

    private fun firePauseEvent(activeTrackId: Long?) {
        activeTrackId?.let {
            analytics.track(
                Event.Podcast.Pause(
                    view = "podcast_player",
                    element = "player",
                    object_id = it.toString()
                )
            )
        }
    }
}