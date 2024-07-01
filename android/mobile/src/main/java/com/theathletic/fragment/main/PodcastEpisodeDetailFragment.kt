package com.theathletic.fragment.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.theathletic.R
import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.adapter.main.PodcastEpisodeDetailAdapter
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.data.ContentType
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.ContentDescriptor
import com.theathletic.databinding.FragmentPodcastEpisodeDetailBinding
import com.theathletic.entity.main.PodcastEpisodeDetailStoryItem
import com.theathletic.entity.main.PodcastEpisodeDetailTrackItem
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.ToolbarCollapseEvent
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extGetDimensionPixelSize
import com.theathletic.fragment.BaseBindingFragment
import com.theathletic.manager.PodcastManager
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.service.PodcastDownloadService
import com.theathletic.ui.main.PodcastEpisodeDetailView
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.NetworkManager
import com.theathletic.viewmodel.main.PodcastEpisodeDetailViewModel
import com.theathletic.widget.StatefulLayout
import cz.helu.helubottombuttonsheet.HeluBottomButtonSheet
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PodcastEpisodeDetailFragment :
    BaseBindingFragment<PodcastEpisodeDetailViewModel, FragmentPodcastEpisodeDetailBinding>(),
    PodcastEpisodeDetailView {

    private lateinit var podcastAdapter: PodcastEpisodeDetailAdapter
    private var actionBarHeight = 0
    private val compositeDisposable = CompositeDisposable()
    private var activeTrackChangeCallback: Observable.OnPropertyChangedCallback? = null
    private var progressChangeCallback: Observable.OnPropertyChangedCallback? = null

    private val podcastDownloadClickAdapter = PodcastDownloadButtonAdapter(this)

    private val analytics by inject<Analytics>()
    private val podcastAnalyticsContext by inject<PodcastAnalyticsContext>()
    private val podcastPlayClickController by inject<PodcastPlayButtonController>()

    private val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }

    private class ItemAnimatorWithoutChanges : DefaultItemAnimator() {
        init {
            supportsChangeAnimations = false
        }
    }

    companion object {
        const val EXTRA_PODCAST_EPISODE_ID = "podcast_episode_id"

        fun newInstance(podcastId: Long) = PodcastEpisodeDetailFragment().apply {
            arguments = Bundle()
            arguments?.putLong(EXTRA_PODCAST_EPISODE_ID, podcastId)
        }
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentPodcastEpisodeDetailBinding = FragmentPodcastEpisodeDetailBinding.inflate(inflater)

    override fun setupViewModel(): PodcastEpisodeDetailViewModel {
        val viewModel = getViewModel<PodcastEpisodeDetailViewModel> { parametersOf(arguments) }
        lifecycle.addObserver(viewModel)
        viewModel.observeEvent(this, DataChangeEvent::class.java, Observer { onDataChangeEvent() })
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupAdapter()
        setupRefreshLayout()
        setupListeners()
        binding.executePendingBindings()
        binding.episodeRecycler.itemAnimator = ItemAnimatorWithoutChanges()

        viewModel.observeEvent(viewLifecycleOwner, ToolbarCollapseEvent::class.java, Observer { toolbarCollapseEvent() })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        activeTrackChangeCallback?.let { PodcastManager.activeTrack.removeOnPropertyChangedCallback(it) }
        removeTrackProgressListener()
        super.onDestroy()
    }

    override fun onPodcastDownloadClick(item: PodcastEpisodeItem) {
        analytics.track(
            Event.Podcast.DownloadSelected(item.id.toString())
        )
        podcastDownloadClickAdapter.onPodcastDownloadClick(item)
    }

    override fun onCommentsOpenClick(item: PodcastEpisodeItem) {
        analytics.track(
            Event.Comments.CommentsClick(
                view = "podcast",
                object_type = "podcast_episode_id",
                object_id = item.id.toString()
            )
        )

        navigator.startCommentsV2Activity(
            contentDescriptor = ContentDescriptor(item.id, item.title),
            type = CommentsSourceType.PODCAST_EPISODE,
            clickSource = ClickSource.PODCAST_STORY
        )
    }

    override fun showDeleteBottomButtonSheet(item: PodcastEpisodeItem) {
        val sheet = HeluBottomButtonSheet.Builder(context).build()
        sheet.addButton(
            R.drawable.ic_trash, resources.getString(R.string.podcast_downloaded_delete_button),
            View.OnClickListener { viewModel.onDeletePodcastClick(item); sheet.dismiss() }
        )
        sheet.show(activity?.supportFragmentManager)
    }

    override fun downloadPodcastStart(item: PodcastEpisodeItem) {
        analytics.track(
            Event.Podcast.Download(
                view = "podcast_episode",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_id = item.id.toString()
            )
        )
        activity?.let { PodcastDownloadService.downloadFile(it, item.id, item.title, item.mp3Url) }
    }

    override fun downloadPodcastCancel(item: PodcastEpisodeItem) {
        activity?.let { PodcastDownloadService.cancelDownload(it, item.id) }
        item.downloadProgress.set(-1)
    }

    override fun onPodcastPlayClick(item: PodcastEpisodeItem) {
        lifecycleScope.launch {
            podcastPlayClickController.onPodcastPlayClick(
                episodeId = item.id,
                callback = this@PodcastEpisodeDetailFragment
            )
        }
    }

    override fun onShareClick() {
        ActivityUtility.startShareTextActivity(
            context, resources.getString(R.string.podcast_share_title),
            viewModel.episode.get()?.permalinkUrl ?: ""
        )
        analytics.track(
            Event.Global.GenericShare(
                "Link",
                viewModel.episode.get()?.title ?: "",
                ContentType.PODCAST.value,
                viewModel.episode.get()?.id.toString()
            )
        )
    }

    override fun showNetworkOfflineError() {
        showSnackbar(R.string.global_network_offline)
    }

    override fun showPayWall() {
        ActivityUtility.startPlansActivity(context, ClickSource.PODCAST_PAYWALL)
    }

    override fun onTrackItemClick(track: PodcastEpisodeDetailTrackItem) {
        viewModel.episode.get()?.let { episode ->
            lifecycleScope.launch {
                podcastPlayClickController.onPodcastPlayClick(
                    episodeId = episode.id,
                    track = track,
                    callback = this@PodcastEpisodeDetailFragment
                )
            }
        }
    }

    override fun onStoryItemClick(item: PodcastEpisodeDetailStoryItem) {
        ActivityUtility.startArticleActivity(context, item.id, ClickSource.PODCAST_STORY)
    }

    private fun onDataChangeEvent() {
        podcastAdapter.notifyDataSetChanged()
        setTrackProgressListener(PodcastManager.activeTrack.get()?.id)
    }

    private fun setupAdapter() {
        podcastAdapter = PodcastEpisodeDetailAdapter(this, viewModel.recyclerList)
        binding.episodeRecycler.adapter = podcastAdapter
    }

    private fun setupToolbar() {
        TypedValue().let { tv ->
            context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
            actionBarHeight = tv.resourceId.extGetDimensionPixelSize()
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.run {
            setDisplayUseLogoEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            binding.toolbar.setNavigationOnClickListener({ activity?.onBackPressed() })
        }
    }

    private fun setupRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            when {
                NetworkManager.getInstance().isOffline() -> {
                    showSnackbar(R.string.global_network_offline)
                    binding.refreshLayout.isRefreshing = false
                }
                viewModel.state.get() != StatefulLayout.PROGRESS -> viewModel.reloadData()
                else -> binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun setupListeners() {
        activeTrackChangeCallback = PodcastManager.activeTrack.extAddOnPropertyChangedCallback { value ->
            setTrackProgressListener(value?.id)
        }
    }

    private fun toolbarCollapseEvent() {
        binding.appBar.setExpanded(false, false)
    }

    private fun setTrackProgressListener(activeTrackId: Long?) {
        removeTrackProgressListener()
        if (viewModel.episode.get()?.id != activeTrackId) {
            return
        }

        var lastPosition = -1
        progressChangeCallback = PodcastManager.currentProgress.extAddOnPropertyChangedCallback { _, _, _ ->
            // Let's do check for lastPosition so we handle less of those events.
            val position = PodcastManager.currentProgress.get() / 1_000
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

    override fun firePlayAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Play(
                view = "podcast_episode",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_id = podcastEpisodeId.toString()
            )
        )
    }

    override fun firePauseAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Pause(
                view = "podcast_episode",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_id = podcastEpisodeId.toString()
            )
        )
    }
}