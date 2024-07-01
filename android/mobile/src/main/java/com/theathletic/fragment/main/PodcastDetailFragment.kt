package com.theathletic.fragment.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.theathletic.R
import com.theathletic.adapter.ViewGroupPagerAdapter
import com.theathletic.adapter.main.PodcastDetailAdapter
import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.adapter.main.PodcastPlayButtonController
import com.theathletic.analytics.AnalyticsPayload
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.data.ContentType
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.databinding.FragmentPodcastDetailBinding
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.event.DataChangeEvent
import com.theathletic.event.ToolbarCollapseEvent
import com.theathletic.extension.extGetDimensionPixelSize
import com.theathletic.fragment.BaseBindingFragment
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.service.PodcastDownloadService
import com.theathletic.ui.UiModel
import com.theathletic.ui.main.PodcastDetailView
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.NetworkManager
import com.theathletic.viewmodel.main.PodcastDetailViewModel
import com.theathletic.widget.StatefulLayout
import cz.helu.helubottombuttonsheet.HeluBottomButtonSheet
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class PodcastDetailFragment :
    BaseBindingFragment<PodcastDetailViewModel, FragmentPodcastDetailBinding>(),
    PodcastDetailView,
    KoinComponent {
    private lateinit var viewPagerAdapter: ViewGroupPagerAdapter
    private lateinit var podcastAdapter: PodcastDetailAdapter
    private var actionBarHeight = 0
    private val compositeDisposable = CompositeDisposable()

    private val podcastDownloadClickAdapter = PodcastDownloadButtonAdapter(this)
    private val podcastPlayClickController by inject<PodcastPlayButtonController>()

    private val analytics by inject<Analytics>()
    private val podcastAnalyticsContext by inject<PodcastAnalyticsContext>()

    private val appBarOffsetListener = object : AppBarLayout.OnOffsetChangedListener {
        var alpha = 1f

        /**
         * We will threat every element little bit different as we want to have
         * different time and speed of it alpha value.
         * In this case, first constant is the opacity delay, second constant is the opacity speed.
         */
        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            val toolbarHeight = binding.toolbar.height

            alpha = (verticalOffset * -1.0f) / (appBarLayout.height - actionBarHeight - toolbarHeight)
            binding.toolbarTitle.alpha = -4.2f + alpha * 4.5f
            binding.titleImage.alpha = 1.5f - alpha * 1.5f
            binding.titleText.alpha = 2f - alpha * 2.3f
            binding.description.alpha = 2f - alpha * 2.3f
            binding.followButton.alpha = 1.5f - alpha * 2.7f
            binding.tabLayout.alpha = 1.05f - alpha * 5.5f
        }
    }

    companion object {
        const val EXTRA_PODCAST_ID = "podcast_id"

        fun newInstance(podcastId: Long) = PodcastDetailFragment().apply {
            arguments = Bundle()
            arguments?.putLong(EXTRA_PODCAST_ID, podcastId)
        }
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentPodcastDetailBinding = FragmentPodcastDetailBinding.inflate(inflater)

    override fun setupViewModel(): PodcastDetailViewModel {
        val viewModel = getViewModel<PodcastDetailViewModel> { parametersOf(arguments) }
        lifecycle.addObserver(viewModel)
        viewModel.observeEvent(this, DataChangeEvent::class.java) { onDataChangeEvent() }
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TypedValue().let { tv ->
            context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
            actionBarHeight = tv.resourceId.extGetDimensionPixelSize()
        }

        viewModel.observeEvent(viewLifecycleOwner, ToolbarCollapseEvent::class.java, Observer { toolbarCollapseEvent() })

        setupToolbar()
        setupAdapter()
        setupViewPagerAdapter()
        setupRefreshLayout()
        binding.executePendingBindings()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onPodcastEpisodeDownloadClick(item: PodcastEpisodeItem) {
        analytics.track(Event.Podcast.DownloadSelected(item.id.toString()))
        podcastDownloadClickAdapter.onPodcastDownloadClick(item)
    }

    override fun onPodcastEpisodeShareClick(item: PodcastEpisodeItem) {
        ActivityUtility.startShareTextActivity(context, resources.getString(R.string.podcast_episode_share_title), item.permalinkUrl ?: "")
        analytics.track(
            Event.Global.GenericShare(
                "Link",
                item.title,
                ContentType.PODCAST_EPISODE.value,
                item.id.toString()
            )
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
                callback = this@PodcastDetailFragment
            )
        }
    }

    override fun onShareClick() {
        ActivityUtility.startShareTextActivity(
            context, resources.getString(R.string.podcast_share_title),
            viewModel.podcast.get()?.permalinkUrl
                ?: ""
        )
        analytics.track(
            Event.Global.GenericShare(
                "Link",
                viewModel.podcast.get()?.title ?: "",
                ContentType.PODCAST.value,
                viewModel.podcast.get()?.id.toString()
            )
        )
    }

    override fun onFollowClick() {
        viewModel.switchFollowStatus()
    }

    override fun showNetworkOfflineError() {
        showSnackbar(R.string.global_network_offline)
    }

    override fun showPayWall() {
        ActivityUtility.startPlansActivity(context, ClickSource.PODCAST_PAYWALL)
    }

    override fun onPodcastEpisodeItemClick(episodeItem: PodcastEpisodeItem) {
        ActivityUtility.startPodcastEpisodeDetailActivity(
            requireContext(),
            episodeItem.id,
            podcastAnalyticsContext.source // Forward the source that was passed to get here
        )
    }

    private fun onDataChangeEvent() {
        podcastAdapter.submitList(viewModel.podcastEpisodeList.toMutableList() as List<UiModel>)
    }

    private fun setupAdapter() {
        podcastAdapter = PodcastDetailAdapter(this, this)
        binding.episodeRecycler.adapter = podcastAdapter
    }

    private fun setupViewPagerAdapter() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPagerAdapter = ViewGroupPagerAdapter(viewPager)
        viewPager.clipToPadding = false
        viewPager.adapter = viewPagerAdapter

        // Bind the title indicator to the viewPager
        tabLayout.setupWithViewPager(viewPager, true)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.run {
            setDisplayUseLogoEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            binding.toolbar.setNavigationOnClickListener({ activity?.onBackPressed() })
            binding.toolbar.navigationIcon?.setTint(resources.getColor(R.color.ath_grey_10, null))
        }

        binding.appBar.removeOnOffsetChangedListener(appBarOffsetListener)
        binding.appBar.addOnOffsetChangedListener(appBarOffsetListener)
    }

    private fun setupRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            if (NetworkManager.getInstance().isOnline()) {
                if (viewModel.state.get() != StatefulLayout.PROGRESS)
                    viewModel.reloadData()
                else
                    binding.refreshLayout.isRefreshing = false
            } else {
                showSnackbar(R.string.global_network_offline)
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private fun toolbarCollapseEvent() {
        binding.appBar.setExpanded(false, false)
    }

    override fun firePlayAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Play(
                view = "podcast_page",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_id = podcastEpisodeId.toString()
            )
        )
    }

    override fun firePauseAnalyticsEvent(podcastEpisodeId: Long, payload: AnalyticsPayload?) {
        analytics.track(
            Event.Podcast.Pause(
                view = "podcast_page",
                element = podcastAnalyticsContext.source.analyticsElement,
                object_id = podcastEpisodeId.toString()
            )
        )
    }
}