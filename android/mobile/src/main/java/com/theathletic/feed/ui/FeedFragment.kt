package com.theathletic.feed.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.adapter.main.PodcastDownloadButtonAdapter
import com.theathletic.analytics.data.ClickSource
import com.theathletic.analytics.impressions.ViewVisibilityTracker
import com.theathletic.databinding.FragmentFeedBinding
import com.theathletic.entity.main.PodcastEpisodeItem
import com.theathletic.extension.betterSmoothScrollToPosition
import com.theathletic.extension.toDp
import com.theathletic.feed.FeedType
import com.theathletic.feed.ui.models.FeedHeadlineListItem
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.ui.PodcastDeleteDialog
import com.theathletic.service.PodcastDownloadService
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.list.AthleticFeedAdapter
import com.theathletic.ui.observe
import com.theathletic.ui.widgets.dialog.menuSheet
import com.theathletic.user.ui.PrivacyPolicyDialogFragment
import com.theathletic.utility.PrivacyRegion
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FeedFragment :
    AthleticMvpBindingFragment<
        FeedViewModel,
        FragmentFeedBinding,
        FeedContract.ViewState,
        >(),
    PrivacyPolicyDialogFragment.PrivacyPolicyDialogListener,
    PodcastDownloadButtonAdapter.Callback {

    companion object {
        private const val ARG_FEED_TYPE = "feed_type"
        private const val ARG_FEED_TITLE = "feed_title"
        private const val ARG_STANDALONE_FEED = "standalone_feed"

        fun newInstance(
            feedType: FeedType,
            title: String = "",
            isStandaloneFeed: Boolean = false
        ) = FeedFragment().apply {
            arguments = bundleOf(
                ARG_FEED_TYPE to feedType,
                ARG_FEED_TITLE to title,
                ARG_STANDALONE_FEED to isStandaloneFeed
            )
        }
    }

    lateinit var adapter: AthleticFeedAdapter
    lateinit var recyclerView: RecyclerView
    private lateinit var reviewManager: ReviewManager
    private var privacyDialogFragment: PrivacyPolicyDialogFragment? = null
    private val podcastDownloadButtonAdapter = PodcastDownloadButtonAdapter(this)

    private val displayPreferences by inject<DisplayPreferences>()

    private val viewVisibilityTracker by lazy {
        ViewVisibilityTracker { requireActivity() }
    }

    override fun setupViewModel() = getViewModel<FeedViewModel> {
        // TODO show error if null

        val feedType = (arguments?.getSerializable(ARG_FEED_TYPE) as? FeedType) ?: FeedType.User
        val padding = context.resources.getDimension(R.dimen.global_list_gutter_padding).toInt().toDp
        val width = resources.displayMetrics.widthPixels.toDp
        parametersOf(
            FeedViewModel.Params(
                isStandaloneFeed = arguments?.getBoolean(ARG_STANDALONE_FEED) ?: false,
                feedTitle = arguments?.getString(ARG_FEED_TITLE).orEmpty(),
                screenWidth = width - (padding * 2),
                screenHeight = resources.displayMetrics.heightPixels.toDp
            ),
            feedType,
            navigator
        )
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentFeedBinding {
        val binding = FragmentFeedBinding.inflate(inflater)
        adapter = AthleticFeedAdapter(
            lifecycleOwner = viewLifecycleOwner,
            interactor = presenter,
            displayPreferences = displayPreferences,
            onPostBindAtPositionListener = presenter,
            impressionTracker = viewVisibilityTracker,
            impressionListener = presenter
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        }
        recyclerView = binding.feedRecyclerView
        recyclerView.adapter = adapter
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewManager = ReviewManagerFactory.create(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).setupActionBar("", view.findViewById(R.id.toolbar))

        presenter.observe<FeedContract.Event>(this) { event ->
            when (event) {
                is FeedContract.Event.ShowArticleLongClickSheet -> showArticleOptionsSheet(
                    event.articleId,
                    event.isBookmarked,
                    event.isRead
                )
                is FeedContract.Event.ScrollToTopOfFeed ->
                    recyclerView.betterSmoothScrollToPosition(0)
                is FeedContract.Event.ScrollToTopHeadlines -> {
                    val index = adapter.currentList.indexOfLast { it is FeedHeadlineListItem }
                    recyclerView.betterSmoothScrollToPosition(index)
                }
                is FeedContract.Event.SolicitAppRating -> {
                    Timber.d("[rating]: received rating event")
                    solicitAppRating()
                }
                is FeedContract.Event.ShowDiscussionLongClickSheet ->
                    showDiscussionOptionsSheet(
                        event.discussionId
                    )
                is FeedContract.Event.SolicitPrivacyUpdate -> showPrivacyDialog(event.privacyRegion)
                is FeedContract.Event.ShowPodcastEpisodeOptionSheet ->
                    showPodcastOptionsSheet(event.episodeId, event.isFinished, event.isDownloaded)
                is FeedContract.Event.TrackFeedView -> if (view.isShown) { event.trackFeedView() }
            }
        }
    }

    private fun showPrivacyDialog(privacyRegion: PrivacyRegion) {
        if (privacyDialogFragment?.isVisible == true) return
        privacyDialogFragment = PrivacyPolicyDialogFragment.newInstance(privacyRegion).also {
            it.show(childFragmentManager, "PrivacyDialogFragment")
        }
    }

    override fun onResume() {
        viewVisibilityTracker.startTracking()
        super.onResume()
    }

    override fun onPause() {
        viewVisibilityTracker.stopTracking()
        super.onPause()
    }

    override fun renderState(viewState: FeedContract.ViewState) {
        adapter.submitList(viewState.uiModels)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        presenter.onPrivacyDialogAccepted()
    }

    private fun showArticleOptionsSheet(
        articleId: Long,
        isArticleBookmarked: Boolean,
        isArticleRead: Boolean
    ) {
        menuSheet {
            if (isArticleBookmarked) {
                addUnbookmark { presenter.changeArticleBookmarkStatus(articleId, false) }
            } else {
                addBookmark { presenter.changeArticleBookmarkStatus(articleId, true) }
            }
            if (isArticleRead) {
                addEntry(iconRes = R.drawable.ic_x, textRes = R.string.feed_mark_unread) {
                    presenter.onMarkArticleRead(
                        articleId = articleId,
                        isRead = false
                    )
                }
            } else {
                addEntry(iconRes = R.drawable.ic_check, textRes = R.string.feed_mark_read) {
                    presenter.onMarkArticleRead(
                        articleId = articleId,
                        isRead = true
                    )
                }
            }
            addShare { presenter.shareArticle(articleId) }
        }.show(requireActivity().supportFragmentManager, null)
    }

    private fun solicitAppRating() {
        viewLifecycleOwner.lifecycleScope.launch {
            Timber.i("Attempting to solicit app rating")
            reviewManager.launchReview(requireActivity(), reviewManager.requestReview())
        }
    }

    private fun showDiscussionOptionsSheet(discussionId: Long) {
        menuSheet {
            addShare { presenter.shareArticle(discussionId) }
        }.show(requireActivity().supportFragmentManager, null)
    }

    private fun showPodcastOptionsSheet(
        episodeId: Long,
        isFinished: Boolean,
        isDownloaded: Boolean
    ) {
        menuSheet {
            addShare { presenter.onPodcastShareClicked(episodeId) }

            if (!isFinished) {
                addEntry(
                    iconRes = R.drawable.ic_check,
                    textRes = R.string.podcast_mark_as_played
                ) {
                    presenter.onMarkPodcastAsPlayedClicked(episodeId)
                }
            }

            if (isDownloaded) {
                addEntry(
                    iconRes = R.drawable.ic_delete,
                    textRes = R.string.podcast_general_remove_download
                ) {
                    presenter.onDeletePodcastClick(episodeId)
                }
            } else {
                addEntry(
                    iconRes = R.drawable.ic_podcast_download_v2,
                    textRes = R.string.podcast_download_episode
                ) {
                    lifecycleScope.launch {
                        podcastDownloadButtonAdapter.onPodcastDownloadClick(episodeId)
                    }
                }
            }
        }.show(requireActivity().supportFragmentManager, null)
    }

    override fun showPayWall() {
        navigator.startPlansActivity(ClickSource.PODCAST_PAYWALL)
    }

    override fun showNetworkOfflineError() {
        showSnackbar(R.string.global_network_offline)
    }

    override fun showDeleteBottomButtonSheet(item: PodcastEpisodeItem) {
        PodcastDeleteDialog.show(requireActivity()) { presenter.onDeletePodcastClick(item.id) }
    }

    override fun downloadPodcastStart(item: PodcastEpisodeItem) {
        PodcastDownloadService.downloadFile(requireActivity(), item.id, item.title, item.mp3Url)
    }

    override fun downloadPodcastCancel(item: PodcastEpisodeItem) {
        PodcastDownloadService.cancelDownload(requireActivity(), item.id)
        item.downloadProgress.set(PodcastDownloadStateStore.NOT_DOWNLOADED)
    }
}