package com.theathletic.article.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.theathletic.ApplicationProcessListener
import com.theathletic.R
import com.theathletic.ads.articles.AdScrollBehaviorImpl
import com.theathletic.ads.articles.AdsDisabledScrollBehaviorImpl
import com.theathletic.ads.bridge.AdBridge
import com.theathletic.analytics.impressions.ViewVisibilityTracker
import com.theathletic.comments.FlagReason
import com.theathletic.databinding.FragmentArticleBinding
import com.theathletic.extension.toDp
import com.theathletic.featureswitch.Features
import com.theathletic.fragment.AthleticMvpBindingFragment
import com.theathletic.profile.SetConsentUseCase
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.observe
import com.theathletic.ui.widgets.dialog.menuSheet
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.logging.WebviewVersionValidator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ArticleFragment : AthleticMvpBindingFragment<
    ArticleViewModel,
    FragmentArticleBinding,
    ArticleContract.ViewState>() {

    companion object {
        const val EXTRA_ARTICLE_ID = "article_id"
        const val EXTRA_SOURCE = "source"

        fun newInstance(
            articleId: Long,
            source: String
        ) = ArticleFragment().apply {
            arguments = Bundle().apply {
                putLong(EXTRA_ARTICLE_ID, articleId)
                putString(EXTRA_SOURCE, source)
            }
        }
    }

    private var adapter: ArticleAdapter? = null
    private var articleReadCalculator: ArticleReadCalculator? = null
    private val features by inject<Features>()
    private val adBridge by inject<AdBridge>()
    private val webViewHtmlBinder = WebViewHtmlBinder(get(), adBridge, get(), get())
    private val webviewVersionValidator by inject<WebviewVersionValidator>()
    private val remoteConfigRepository by inject<RemoteConfigRepository>()
    private val viewVisibilityTracker by lazy {
        ViewVisibilityTracker { requireActivity() }
    }
    private val displayPreferences by inject<DisplayPreferences>()
    private val processListener by inject<ApplicationProcessListener>()
    private val setConsentUseCase by inject<SetConsentUseCase>()
    private var hasScrolledToLastKnownPosition = false

    override fun setupViewModel() = getViewModel<ArticleViewModel> {
        val padding = context.resources.getDimension(R.dimen.global_list_gutter_padding).toInt().toDp
        val width = resources.displayMetrics.widthPixels.toDp
        parametersOf(
            ArticleViewModel.Params(
                arguments?.getLong(EXTRA_ARTICLE_ID) ?: 0,
                arguments?.getString(EXTRA_SOURCE) ?: "Unknown",
                screenWidth = width - (padding * 2),
                screenHeight = resources.displayMetrics.heightPixels.toDp,
                appVersion = context
                    .packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName
            ),
            navigator
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        presenter.observe<ArticleContract.Event>(this) { event ->
            when (event) {
                is ArticleContract.Event.ToggleFullscreen -> onFullscreenToggled(event.isFullscreen)
                is ArticleContract.Event.ShowTextStyleBottomSheet -> launchTextStyleSheet()
                is ArticleContract.Event.ShowCommentOptionsSheet -> launchCommentOptionsSheet(event)
                is ArticleContract.Event.ShowReportCommentDialog -> launchReportCommentDialog(event.commentId)
                is ArticleContract.Event.ShowWebViewUpgradeDialog -> launchWebViewUpgradeDialog()
            }
        }

        adBridge.observeAdEvents().collectIn(lifecycleScope) { adEvent ->
            presenter.trackAdEvent(adEvent)
        }
    }

    override fun onResume() {
        viewVisibilityTracker.startTracking()
        if (webviewVersionValidator.isOnOldWebView(context)) {
            presenter.onLoadedWithOldWebView()
        }
        super.onResume()
    }

    override fun onPause() {
        viewVisibilityTracker.stopTracking()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.trackArticleRead(articleReadCalculator?.articleMaxReadPercent ?: 0)
        if (articleReadCalculator?.isMarkAsCompleted == false) {
            presenter.saveArticleLastScrollPos(articleReadCalculator?.articleCurrentScrollPercent ?: 0)
        }
        super.onDestroy()
    }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentArticleBinding {
        val binding = FragmentArticleBinding.inflate(inflater)
        setupAdapter(binding)
        return binding
    }

    override fun renderState(viewState: ArticleContract.ViewState) {
        adapter?.submitList(viewState.uiModels)
        // We attempt to scroll the article to last scroll percentage only when the article content is visible
        // and have last scroll position value
        viewState.lastScrollPercentage?.let { percent ->
            if (!viewState.showSpinner && percent >= 0 && !hasScrolledToLastKnownPosition) {
                val pos = articleReadCalculator?.convertLastScrollPercentageToPosition(percent) ?: 0
                binding.scrollView.scrollTo(0, pos)
                hasScrolledToLastKnownPosition = true
            }
        }
    }

    private fun setupAdapter(binding: FragmentArticleBinding) {
        lifecycleScope.launch {
            val adScrollBehavior = if (features.isArticleAdsEnabled) {
                AdScrollBehaviorImpl(dispatcherProvider = get())
            } else {
                AdsDisabledScrollBehaviorImpl()
            }

            val articleScrollPercentToConsiderRead = remoteConfigRepository.articleScrollPercentToConsiderRead.first()
            articleReadCalculator = ArticleReadCalculator(
                presenter,
                adScrollBehavior,
                articleScrollPercentToConsiderRead
            )

            adapter = ArticleAdapter(
                lifecycleOwner = viewLifecycleOwner,
                interactor = presenter,
                articleReadCalculator = articleReadCalculator!!,
                fullscreenView = binding.fullscreen,
                contentView = binding.recyclerView,
                webViewHtmlBinder = webViewHtmlBinder,
                viewVisibilityTracker = viewVisibilityTracker,
                adScrollBehavior = adScrollBehavior,
                displayPreferences = displayPreferences,
                processListener = processListener,
                setConsentUseCase = setConsentUseCase,
                features = features,
            )
            binding.recyclerView.adapter = adapter
            binding.scrollView.setOnScrollChangeListener(articleReadCalculator)
        }
    }

    private fun onFullscreenToggled(isFullscreen: Boolean) {
        val attrs = activity?.window?.attributes
        if (isFullscreen) {
            attrs?.flags = attrs?.flags
                ?.plus(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                ?.plus(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            activity?.window?.attributes = attrs
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        } else {
            attrs?.flags = attrs?.flags
                ?.minus(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                ?.minus(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            activity?.window?.attributes = attrs
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun launchWebViewUpgradeDialog() {
        webviewVersionValidator.launchWebviewVersionDialog(context)
    }

    private fun launchTextStyleSheet() {
        ArticleSettingsSheetFragment.newInstance().show(requireActivity().supportFragmentManager, null)
    }

    private fun launchCommentOptionsSheet(event: ArticleContract.Event.ShowCommentOptionsSheet) {
        menuSheet {
            if (event.isUserAuthor && !event.isCommentLocked) {
                addEntry(iconRes = R.drawable.ic_edit, textRes = R.string.comments_settings_edit) {
                    presenter.onEditCommentClicked(event.commentId)
                }
            }

            if (event.isUserAuthor) {
                addEntry(iconRes = R.drawable.ic_trash, textRes = R.string.comments_settings_delete) {
                    presenter.onDeleteCommentClicked(event.commentId)
                }
            }

            if (!event.isUserAuthor && !event.isCommentLocked) {
                addEntry(iconRes = R.drawable.ic_report, textRes = R.string.comments_settings_flag) {
                    presenter.onFlagCommentClick(event.commentId, event.commentIndex)
                }
            }
        }.show(parentFragmentManager, null)
    }

    private fun launchReportCommentDialog(commentId: Long) {
        lateinit var dialog: AlertDialog
        lateinit var flagType: FlagReason
        dialog = AlertDialog.Builder(context, R.style.ThemeOverlay_Ath_MaterialAlertDialog)
            .setTitle(R.string.comments_flag_dialog_title)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.news_comments_flag_confirm_options),
                -1
            ) { _, position ->
                flagType = when (position) {
                    0 -> FlagReason.ABUSIVE_OR_HARMFUL
                    1 -> FlagReason.TROLLING_OR_BAITING
                    2 -> FlagReason.SPAM
                    3 -> FlagReason.USER
                    else -> FlagReason.NONE
                }

                if (flagType != FlagReason.NONE) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).alpha = 1f
                }
            }
            .setPositiveButton(R.string.comments_flag_dialog_confirm) { _, _ ->
                presenter.flagComment(commentId, flagType)
            }
            .setNegativeButton(R.string.comments_flag_dialog_cancel, null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getColor(R.color.ath_red))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).alpha = 0.5f
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar.mvpToolbar)
        (activity as AppCompatActivity).supportActionBar?.run {
            setDisplayUseLogoEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            binding.toolbar.mvpToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            binding.toolbar.mvpToolbar.navigationIcon?.setTint(resources.getColor(R.color.ath_grey_10, null))
        }
    }
}