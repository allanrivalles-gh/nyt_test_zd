package com.theathletic.article.ui

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.platform.LocalContext
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.theathletic.ApplicationProcessListener
import com.theathletic.R
import com.theathletic.ads.articles.AdsScrollBehavior
import com.theathletic.ads.getAdKvpsAsJson
import com.theathletic.analytics.impressions.ViewVisibilityTracker
import com.theathletic.article.ArticleAuthorModel
import com.theathletic.article.ArticleComment
import com.theathletic.article.ArticleCommentsNotLoaded
import com.theathletic.article.ArticleCommentsTitle
import com.theathletic.article.ArticleContentModel
import com.theathletic.article.ArticleDisabledComments
import com.theathletic.article.ArticleFreeUserUpsell
import com.theathletic.article.ArticleImageModel
import com.theathletic.article.ArticlePaywallCTAModel
import com.theathletic.article.ArticlePaywallContentModel
import com.theathletic.article.ArticleRatedImage
import com.theathletic.article.ArticleRatingButtons
import com.theathletic.article.ArticleRatingPadding
import com.theathletic.article.ArticleRatingTitle
import com.theathletic.article.ArticleTitleModel
import com.theathletic.article.ArticleViewMoreComments
import com.theathletic.article.WebViewRestorationManager
import com.theathletic.comments.ui.components.Comment
import com.theathletic.comments.ui.components.CommentItemInteractor
import com.theathletic.comments.ui.components.CommentsUi
import com.theathletic.databinding.ListItemArticleCommentComposeBinding
import com.theathletic.databinding.ListItemArticleCommentDisabledBinding
import com.theathletic.databinding.ListItemArticleCommentsNotLoadedComposeBinding
import com.theathletic.databinding.ListItemArticleRelatedContentBinding
import com.theathletic.databinding.ListItemArticleWebViewBinding
import com.theathletic.databinding.ListItemArticleWebViewPaywallBinding
import com.theathletic.extension.extSetClickableSpanBold
import com.theathletic.extension.extSetClickableSpanUnderlineBold
import com.theathletic.featureswitch.Features
import com.theathletic.profile.SetConsentUseCase
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.UiModel
import com.theathletic.ui.asResourceString
import com.theathletic.ui.list.BindingDiffAdapter
import com.theathletic.ui.list.DataBindingViewHolder
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.widget.webview.VideoEnabledWebChromeClient
import com.theathletic.widget.webview.VideoEnabledWebView
import java.lang.ref.WeakReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class ArticleAdapter(
    lifecycleOwner: LifecycleOwner,
    private val interactor: ArticleContract.Presenter,
    private val articleReadCalculator: ArticleReadCalculator,
    private val fullscreenView: FrameLayout,
    private val contentView: View,
    private val webViewHtmlBinder: WebViewHtmlBinder,
    private val viewVisibilityTracker: ViewVisibilityTracker,
    private val adScrollBehavior: AdsScrollBehavior,
    private val displayPreferences: DisplayPreferences,
    private val processListener: ApplicationProcessListener,
    private val setConsentUseCase: SetConsentUseCase,
    private val features: Features,
) : BindingDiffAdapter(lifecycleOwner, interactor) {

    @Suppress("ComplexMethod")
    override fun getLayoutForModel(model: UiModel): Int {
        return when (model) {
            // Article Content
            is ArticleImageModel -> R.layout.list_item_article_header_image
            is ArticleTitleModel -> R.layout.list_item_article_title
            is ArticleAuthorModel -> R.layout.list_item_article_author
            is ArticleContentModel -> R.layout.list_item_article_web_view
            is ArticlePaywallContentModel -> R.layout.list_item_article_web_view_paywall
            is ArticlePaywallCTAModel -> R.layout.list_item_article_paywall

            // Ratings
            is ArticleRatingTitle -> R.layout.list_item_article_rating_title
            is ArticleRatingButtons -> R.layout.list_item_article_rating_buttons
            is ArticleRatedImage -> R.layout.list_item_article_rated_image
            is ArticleFreeUserUpsell -> R.layout.list_item_article_free_user_upsell
            is ArticleRatingPadding -> R.layout.list_item_article_rating_padding

            // Comments
            is ArticleCommentsNotLoaded -> R.layout.list_item_article_comments_not_loaded_compose
            is ArticleCommentsTitle -> R.layout.list_item_article_comments_title
            is ArticleDisabledComments -> R.layout.list_item_article_comment_disabled
            is ArticleComment -> R.layout.list_item_article_comment_compose
            is ArticleViewMoreComments -> R.layout.list_item_article_comments_load_more

            // Related Articles
            is RelatedContentSectionTitle -> R.layout.list_item_article_related_content_header
            is RelatedContentItem -> R.layout.list_item_article_related_content

            else -> throw IllegalArgumentException("Cannot create layout from $model")
        }
    }

    var articlePaywallModel: ArticlePaywallContentModel? = null
    var commentIndex = -1

    override fun onPostBind(uiModel: UiModel, holder: DataBindingViewHolder<ViewDataBinding>) {
        super.onPostBind(uiModel, holder)
        when (uiModel) {
            is ArticleContentModel -> {
                bindArticleContent(uiModel, holder.binding as ListItemArticleWebViewBinding)
            }
            is ArticlePaywallContentModel -> {
                bindPaywallContent(uiModel, holder.binding as ListItemArticleWebViewPaywallBinding)
            }
            is ArticleImageModel,
            is ArticleTitleModel,
            is ArticleAuthorModel -> holder.binding.root.viewTreeObserver.addOnGlobalLayoutListener {
                articleReadCalculator.updateTopperSize(uiModel.stableId, holder.binding.root.height)
            }
            is ArticleCommentsNotLoaded -> setupCommentsNotLoadedView(
                holder.binding as ListItemArticleCommentsNotLoadedComposeBinding,
                uiModel,
            )
            is ArticleDisabledComments ->
                setupDisabledCommentsSpans(uiModel, holder.binding as ListItemArticleCommentDisabledBinding)
            is ArticleComment -> setupCommentView(holder.binding, uiModel)
            is RelatedContentItem -> {
                viewVisibilityTracker.registerView(holder.binding.root) {
                    interactor.onRelatedArticleImpression(it)
                }
                (holder.binding as ListItemArticleRelatedContentBinding).composeView.setContent {
                    AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(LocalContext.current)) {
                        RelatedContentRow(uiModel, interactor)
                    }
                }
            }
        }
    }

    private fun setupCommentsNotLoadedView(
        binding: ListItemArticleCommentsNotLoadedComposeBinding,
        state: ArticleCommentsNotLoaded,
    ) {
        binding.commentsNotLoadedComposeView.setContent {
            AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(LocalContext.current)) {
                ArticleCommentsNotLoadedNotice(
                    isLoading = state.isLoading,
                    onTapToReloadComments = interactor::onTapToReloadComments
                )
            }
        }
    }

    private fun setupCommentView(binding: ViewDataBinding, uiModel: ArticleComment) {
        commentIndex.inc()
        (binding as ListItemArticleCommentComposeBinding).commentComposeView.setContent {
            val comment = uiModel.toUserComment()
            AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(LocalContext.current)) {
                Comment(
                    comment = comment,
                    interactor = object : CommentItemInteractor {
                        override fun onLikeClick(commentId: String, index: Int) {
                            interactor.onCommentLiked(commentId, comment.hasUserLiked, index)
                        }

                        override fun onReplyClick(parentId: String, commentId: String) {
                            interactor.onCommentReply(parentId, commentId)
                        }

                        override fun onShareClick(permalink: String) {
                            interactor.onCommentShare(permalink)
                        }

                        override fun onFlagClick(commentId: String, index: Int) {
                            interactor.onFlagCommentClick(commentId.toLong(), index)
                        }

                        override fun onCommentClick(commentId: String, index: Int) {
                            interactor.onCommentClick(commentId)
                        }
                    },
                    index = commentIndex,
                    isLikeEnabled = uiModel.isLikeEnabled
                )
            }
        }
    }

    private fun ArticleComment.toUserComment(): CommentsUi.Comments {
        val comment = CommentsUi.Comments.UserComment(
            commentId = id.toString(),
            parentId = stableId,
            commentText = comment,
            likesCount = likes.toInt(),
            commentLink = permalink,
            commentedAt = date.asResourceString(),
            authorId = "",
            authorName = userName,
            isPinned = false,
            isHighlighted = false,
            hasUserLiked = isLiked,
            replyCount = replies.toInt(),
            isAuthor = false,
            isDeletable = false,
            commentMetadata = null,
            authorFlairs = null
        )

        return if (isStaff) CommentsUi.Comments.StaffComment(comment) else comment
    }

    private lateinit var restorationManager: WebViewRestorationManager
    private lateinit var boundArticleContentWebView: VideoEnabledWebView
    private var boundArticleContentModel: ArticleContentModel? = null

    private fun bindArticleContent(
        uiModel: ArticleContentModel,
        binding: ListItemArticleWebViewBinding
    ) {
        fun setHTMLContentToBoundWebView(refresh: Boolean) {
            webViewHtmlBinder.setHtmlContent(
                webView = boundArticleContentWebView,
                forceRefresh = refresh,
                content = uiModel.contentString,
                url = uiModel.contentUrl,
                adConfig = uiModel.adConfig?.getAdKvpsAsJson()
            )
        }

        // we might get here a second time, in this case we don't want to recreate and setup the webview
        if (boundArticleContentModel != null) {
            if (uiModel !== boundArticleContentModel) {
                boundArticleContentModel = uiModel
                setHTMLContentToBoundWebView(refresh = true)
            }
            return
        }

        boundArticleContentModel = uiModel
        val createAndSetupWebView = {
            val context = binding.root.context
            val container = binding.webviewContainer
            val webView = VideoEnabledWebView(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false

                adScrollBehavior.webView = WeakReference(this)
                container.addView(this)
            }

            setupWebView(webView) {
                container.removeView(webView)
                webView.destroy()

                restorationManager.onWebViewCrashed()
            }
            boundArticleContentWebView = webView
            setHTMLContentToBoundWebView(refresh = false)
        }
        restorationManager = WebViewRestorationManager(restore = createAndSetupWebView)
        createAndSetupWebView()

        restorationManager.listenForAppLifecycle(processListener, lifecycleOwner.lifecycleScope)
    }

    private fun bindPaywallContent(
        uiModel: ArticlePaywallContentModel,
        binding: ListItemArticleWebViewPaywallBinding
    ) {
        adScrollBehavior.webView = WeakReference(binding.webviewPaywall)
        when {
            articlePaywallModel == null -> {
                setupWebView(binding.webviewPaywall)
                adjustWebViewForPaywall(binding.webviewPaywall)
                webViewHtmlBinder.setHtmlContent(
                    webView = binding.webviewPaywall,
                    content = uiModel.contentString,
                    url = uiModel.contentUrl,
                    forceRefresh = false,
                    adConfig = uiModel.adConfig?.getAdKvpsAsJson()
                )
                articlePaywallModel = uiModel
            }
            articlePaywallModel != uiModel -> {
                webViewHtmlBinder.setHtmlContent(
                    webView = binding.webviewPaywall,
                    content = uiModel.contentString,
                    url = uiModel.contentUrl,
                    forceRefresh = true,
                    adConfig = uiModel.adConfig?.getAdKvpsAsJson()
                )
                articlePaywallModel = uiModel
            }
            else -> {
            }
        }
    }

    private fun adjustWebViewForPaywall(webView: WebView) {
        webView.apply {
            // Disable touch events
            setOnLongClickListener { true }
            isLongClickable = false
            setOnTouchListener { _, _ -> true }

            // Adjust height to preview more content
            val displayMetrics = context.resources.displayMetrics
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            val heightParams = layoutParams
            val isPortrait = height >= width
            if (isPortrait) {
                heightParams.height = height
            } else {
                heightParams.height = (height * 1.2).toInt()
            }
            layoutParams = heightParams
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(webView: WebView, handleRenderProcessGone: (() -> Unit)? = null) {
        val webViewLoadedState = WebViewLoadedState()
        if (features.isTcfConsentEnabled) {
            lifecycleOwner.lifecycleScope.launch {
                setConsentUseCase(webView)
                webViewLoadedState.hasConsentSetFinished = true
            }
        } else {
            webViewLoadedState.hasConsentSetFinished = true
        }
        webView.apply {
            webChromeClient = VideoEnabledWebChromeClient(
                contentView,
                fullscreenView
            ).apply {
                setOnToggledFullscreen { interactor.onFullscreenToggled(it) }
            }

            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            // This needs to be set to false. This is fix for the bug that caused some video could not be played for some users.
            // https://trello.com/c/LZsJu1gy/1596-android-videos-not-playing
            settings.mediaPlaybackRequiresUserGesture = false

            isFocusable = false
            isFocusableInTouchMode = false

            webViewClient = object : WebViewClient() {
                override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                    // if don't handle it, we let the application crash
                    if (handleRenderProcessGone == null) return false

                    handleRenderProcessGone()
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    interactor.onUrlClick(request.url.toString())
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    webViewLoadedState.hasPageFinished = true
                    adScrollBehavior.requestInitialAd()
                }
            }

            viewTreeObserver.addOnGlobalLayoutListener {
                articleReadCalculator.articleSize = height
                if (height > 0) {
                    webViewLoadedState.hasValidHeight = true
                }
            }
        }
    }

    private inner class WebViewLoadedState(
        private var hasInvokedInteractor: Boolean = false
    ) {
        var hasValidHeight: Boolean = false
            set(value) {
                field = value
                callInteractorIfConditionsMet()
            }

        var hasPageFinished: Boolean = false
            set(value) {
                field = value
                callInteractorIfConditionsMet()
            }

        var hasConsentSetFinished: Boolean = false
            set(value) {
                field = value
                callInteractorIfConditionsMet()
            }

        @Suppress("ComplexCondition")
        private fun callInteractorIfConditionsMet() {
            if (hasPageFinished && hasValidHeight && hasConsentSetFinished && !hasInvokedInteractor) {
                hasInvokedInteractor = true
                interactor.onPageLoaded()
            }
        }
    }

    private fun setupDisabledCommentsSpans(
        uiModel: ArticleDisabledComments,
        binding: ListItemArticleCommentDisabledBinding
    ) {
        val context = binding.root.context
        val spannableString = SpannableString(context.getString(uiModel.textRes))

        val codeOfConductSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                interactor.onCodeOfConductClick()
            }
        }
        val codeOfConductString = context.getString(R.string.article_comments_moderation_code_of_conduct_span)
        spannableString.extSetClickableSpanBold(codeOfConductString, codeOfConductSpan)

        val emailEditorString = context.getString(R.string.article_comments_moderation_email_editor_span)
        val emailEditorSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                interactor.onMessageModerator()
            }
        }
        spannableString.extSetClickableSpanUnderlineBold(emailEditorString, emailEditorSpan)

        binding.disableCommentsText.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.disableCommentsText.movementMethod = LinkMovementMethod.getInstance()
    }
}

// this will listen for the app lifecycle while the view is not destroyed
private fun WebViewRestorationManager.listenForAppLifecycle(
    processListener: ApplicationProcessListener,
    scope: CoroutineScope
) {
    processListener.isInForegroundFlow.collectIn(scope) { isInForeground ->
        if (isInForeground) {
            onAppInForeground()
        } else {
            onAppInBackground()
        }
    }
}