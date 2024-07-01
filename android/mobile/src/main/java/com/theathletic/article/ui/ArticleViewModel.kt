package com.theathletic.article.ui

import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.ads.AdAnalytics
import com.theathletic.ads.AdConfig
import com.theathletic.ads.bridge.data.local.AdEvent
import com.theathletic.ads.data.local.ContentType
import com.theathletic.analytics.data.ClickSource
import com.theathletic.annotation.autokoin.Assisted
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.article.ArticleHasPaywallUseCase
import com.theathletic.article.data.ArticleRepository
import com.theathletic.comments.FlagReason
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.comments.data.CommentsRepository
import com.theathletic.comments.ui.LikeActionUiState
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.compass.getAdExperiments
import com.theathletic.data.ContentDescriptor
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.article.ArticleRating
import com.theathletic.entity.article.isHeadlinePost
import com.theathletic.entity.authentication.UserData
import com.theathletic.entity.authentication.UserPrivilegeLevel
import com.theathletic.event.SnackbarEventRes
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.feed.FeedType
import com.theathletic.feed.compose.MarkArticleAsReadUseCase
import com.theathletic.feed.compose.MarkArticleAsSavedUseCase
import com.theathletic.links.LinkHelper
import com.theathletic.links.LinkHelper.Companion.BASE_IMAGE_SCHEME
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.location.data.LocationRepository
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.remoteconfig.RemoteConfigRepository
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.rooms.LiveAudioRoomStateManager
import com.theathletic.rooms.ui.LiveAudioEvent
import com.theathletic.rooms.ui.LiveAudioEventProducer
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerUiModel
import com.theathletic.share.ShareBroadcastReceiver
import com.theathletic.share.ShareEventConsumer
import com.theathletic.share.ShareTitle
import com.theathletic.ui.AthleticViewModel
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.DataState
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.Transformer
import com.theathletic.user.IUserManager
import com.theathletic.utility.AppRatingEngine
import com.theathletic.utility.IPreferences
import com.theathletic.utility.PaywallUtility
import com.theathletic.utility.coroutines.collectIn
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
class ArticleViewModel @AutoKoin constructor(
    transformer: ArticleTransformer,
    @Assisted private val params: Params,
    @Assisted private val screenNavigator: ScreenNavigator,
    private val articleRepository: ArticleRepository,
    private val linkHelper: LinkHelper,
    private val deeplinkEventProducer: DeeplinkEventProducer,
    private val userManager: IUserManager,
    private val appRatingEngine: AppRatingEngine,
    private val preferences: IPreferences,
    private val liveAudioRoomStateManager: LiveAudioRoomStateManager,
    private val liveAudioEventProducer: LiveAudioEventProducer,
    private val userDataRepository: IUserDataRepository,
    private val commentsRepository: CommentsRepository,
    private val paywallUtility: PaywallUtility,
    private val articleHasPaywall: ArticleHasPaywallUseCase,
    private val displayPreferences: DisplayPreferences,
    private val shareEventConsumer: ShareEventConsumer,
    private val featureSwitches: FeatureSwitches,
    private val articleAnalytics: ArticleAnalytics,
    private val adAnalytics: AdAnalytics,
    private val locationUtility: LocationRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val authorFeedExistsUseCase: AuthorFeedExistsUseCase,
    private val adConfigBuilder: AdConfig.Builder,
    private val markItemAsRead: MarkArticleAsReadUseCase,
    private val markArticleAsSaved: MarkArticleAsSavedUseCase
) : AthleticViewModel<ArticleDataState, ArticleContract.ViewState>(),
    ArticleContract.Presenter,
    DefaultLifecycleObserver,
    Transformer<ArticleDataState, ArticleContract.ViewState> by transformer {

    data class Params(
        val articleId: Long,
        val source: String,
        val screenWidth: Int,
        val screenHeight: Int,
        val appVersion: String
    )

    override val initialState = ArticleDataState(
        contentTextSize = displayPreferences.contentTextSize,
        isRatedAtLaunch = userDataRepository.isItemRated(params.articleId)
    )

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        listenForRenderUpdates()
        initialize()
    }

    fun initialize() {
        viewModelScope.launch {
            listenForConfigUpdates()
            initializeAdConfig()
            listenForArticleUpdates()
        }
        adAnalytics.trackAdPageView(pageViewId, "article")

        preferences.lastGoogleSubArticleId = params.articleId
        preferences.lastGoogleSubPodcastId = null

        waitToConfirmShareIntent()
    }

    private fun listenForConfigUpdates() {
        remoteConfigRepository.gdprSupportedCountries.collectIn(viewModelScope) {
            adConfigBuilder.setGDPRCountries(it)
        }
        remoteConfigRepository.ccpaSupportedStates.collectIn(viewModelScope) {
            adConfigBuilder.setCCPAStates(it)
        }
    }

    private fun listenForArticleUpdates() {
        articleRepository.getArticleFlow(params.articleId, networkFetch = true).onEach { article ->
            val showPaywall = articleHasPaywall(params.articleId, article)
            articleAnalytics.trackArticleView(article, showPaywall, params.source)
            articleAnalytics.trackAdOnLoad(article, pageViewId)
            val authorExists = authorFeedExistsUseCase(article?.authorId)
            updateState {
                copy(
                    articleEntity = article,
                    showPaywall = showPaywall,
                    isAuthorClickable = authorExists,
                    adConfig = adConfigBuilder
                        .setAdTargeting(article?.adTargetingParams)
                        .setAdUnitPath(article?.adUnitPath)
                        .viewport(params.screenWidth, params.screenHeight)
                        .build(pageViewId, true)
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun listenForRenderUpdates() {
        liveAudioRoomStateManager.currentRoomViewState.collectIn(viewModelScope) {
            updateState { copy(liveRoomData = it) }
        }
        displayPreferences.contentTextSizeState.collectIn(viewModelScope) {
            updateState {
                copy(
                    contentTextSize = it
                )
            }
        }
        userDataRepository.userDataFlow.collectIn(viewModelScope) { userData ->
            updateState {
                copy(
                    isBookmarked = userData.isArticleBookmarked(params.articleId)
                )
            }
        }
    }

    private suspend fun initializeAdConfig() {
        adConfigBuilder.subscriber(userManager.isUserSubscribed())
            .contentType(ContentType.ARTICLES.type)
            .appVersion(params.appVersion)
            .setCompassExperiments(CompassExperiment.getAdExperiments())
            .setGeo(locationUtility.getCountryCode(), locationUtility.getState())
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (state.articleEntity != null && state.htmlIsLoaded) {
            updateState { copy(showSpinner = false) }
            onTapToReloadComments()
        }
        viewModelScope.launch {
            articleHasPaywall(params.articleId, state.articleEntity).let { newPaywallState ->
                if (state.showPaywall != newPaywallState) {
                    updateState { copy(showPaywall = newPaywallState) }
                }
            }
        }
    }

    fun trackArticleRead(percentRead: Int) {
        articleAnalytics.trackArticleRead(state.articleEntity, state.showPaywall, percentRead, params.source)
    }

    fun saveArticleLastScrollPos(articleScrollPos: Int) {
        articleRepository.saveArticleLastScrollPercentage(params.articleId, articleScrollPos)
    }

    fun trackAdEvent(event: AdEvent) {
        adAnalytics.trackAdEvent(event = event, view = "article", pageViewId = pageViewId)
    }

    override fun onUrlClick(url: String) {
        if (linkHelper.isAthleticLink(url)) {
            updateState { copy(showSpinner = true) }
            viewModelScope.launch {
                deeplinkEventProducer.emit(url)
            }
        } else {
            handleNonAthleticUrl(url)
        }
    }

    override fun onPageLoaded() {
        updateState {
            copy(
                showSpinner = false,
                htmlIsLoaded = true
            )
        }
    }

    override fun onFullscreenToggled(isFullscreen: Boolean) {
        sendEvent(ArticleContract.Event.ToggleFullscreen(isFullscreen))
    }

    override fun onAuthorClicked(authorId: Long) {
        screenNavigator.startStandaloneFeedActivity(FeedType.Author(authorId))
    }

    override fun onPaywallContinueClick() {
        screenNavigator.startPlansActivity(
            ClickSource.ARTICLE,
            state.articleEntity?.articleId ?: 0
        )
    }

    private fun handleNonAthleticUrl(url: String) {
        if (url.contains(BASE_IMAGE_SCHEME)) {
            screenNavigator.startFullscreenPhotoActivity(url.replace(BASE_IMAGE_SCHEME, ""))
            return
        }

        val uri = Uri.parse(url)
        when (uri.scheme) {
            "mailto" -> screenNavigator.startSendEmailActivity(uri)
            else -> {
                handleAnalytics(url)
                screenNavigator.startOpenExternalLink(uri)
            }
        }
    }

    private fun handleAnalytics(url: String) {
        articleAnalytics.trackExternalUrl(params.articleId.toString(), params.source, url)
    }

    override fun onArticleRead() {
        if (state.showPaywall) return
        if (state.articleEntity?.isTeaser == false) {
            appRatingEngine.onRatingTrigger()
        }
        viewModelScope.launch { markItemAsRead(params.articleId, true) }
        articleAnalytics.trackFreeArticleRead(state.articleEntity)
    }

    override fun onArticleCompleted() {
        // resetting the article last scroll value to zero once article is completely read
        saveArticleLastScrollPos(0)
    }

    override fun onRoomMiniPlayerClicked(id: String) {
        articleAnalytics.trackRoomMiniPlayerClicked(state.articleEntity)
        screenNavigator.startLiveAudioRoomActivity(id)
    }

    override fun onRoomCloseClicked(id: String) {
        articleAnalytics.trackOnRoomCloseClick(state.articleEntity)
        viewModelScope.launch {
            liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
        }
    }

    override fun onCommentsClick() {
        val sourceType = getCommentSourceType(state.articleEntity)
        val clickSource = getClickSourceType(sourceType)

        articleAnalytics.trackCommentsClick(state.articleEntity)
        screenNavigator.startCommentsV2Activity(
            contentDescriptor = ContentDescriptor(
                params.articleId.toString(),
                state.articleEntity?.articleTitle ?: ""
            ),
            type = sourceType,
            clickSource = clickSource
        )
    }

    private fun getCommentSourceType(article: ArticleEntity?): CommentsSourceType {
        return if (article != null && article.isHeadlinePost) {
            CommentsSourceType.HEADLINE
        } else {
            CommentsSourceType.ARTICLE
        }
    }

    private fun getClickSourceType(sourceType: CommentsSourceType): ClickSource {
        return if (sourceType == CommentsSourceType.HEADLINE) {
            ClickSource.HEADLINE
        } else {
            ClickSource.ARTICLE
        }
    }

    override fun onBookmarkClick(isBookmarked: Boolean) {
        updateState { copy(isBookmarked = !isBookmarked) }
        markArticleAsSaved(params.articleId, !isBookmarked)
    }

    override fun onTextStyleClick() {
        articleAnalytics.trackTextStyleClick(state.articleEntity)
        sendEvent(ArticleContract.Event.ShowTextStyleBottomSheet)
    }

    override fun onWebViewUpgradeClick() {
        sendEvent(ArticleContract.Event.ShowWebViewUpgradeDialog)
    }

    fun onLoadedWithOldWebView() {
        if (featureSwitches.isFeatureEnabled(FeatureSwitch.WEBVIEW_VERSION_VALIDATOR_ENABLED)) {
            updateState { copy(showWebviewUpgradeInToolbar = true) }
            if (preferences.hasSeenWebViewVersionNotice != true) {
                sendEvent(ArticleContract.Event.ShowWebViewUpgradeDialog)
                preferences.hasSeenWebViewVersionNotice = true
            }
        }
    }

    override fun onShareClick() {
        var linkToShare = state.articleEntity?.permalink ?: ""
        val userIsAtLeastAuthor = userManager.getCurrentUser()?.getUserLevel()
            ?.isAtLeastAtLevel(UserPrivilegeLevel.AUTHOR) == true
        val userOrEmpString = if (userIsAtLeastAuthor) "emp" else "user"

        if (!linkToShare.contains("source=$userOrEmpString-shared-article")) {
            if (linkToShare.endsWith("/")) {
                linkToShare = linkToShare.removeSuffix("/")
            }
            linkToShare += "?source=$userOrEmpString-shared-article"
        }
        articleAnalytics.trackShareClick(state.articleEntity)

        screenNavigator.startShareTextActivity(
            textToSend = linkToShare,
            title = ShareTitle.ARTICLE,
            shareKey = ShareBroadcastReceiver.ShareKey.ARTICLE.value
        )
    }

    private fun waitToConfirmShareIntent() {
        shareEventConsumer.filter { it.hasExtra(ShareBroadcastReceiver.ShareKey.ARTICLE.value) }
            .collectIn(viewModelScope) {
                articleAnalytics.trackShareComplete(state.articleEntity)
            }
    }

    override fun onMehRating() {
        rateArticle(ArticleRating.MEH.value)
        updateState { copy(userRating = ArticleRating.MEH.value) }
    }

    override fun onSolidRating() {
        rateArticle(ArticleRating.SOLID.value)
        updateState { copy(userRating = ArticleRating.SOLID.value) }
    }

    override fun onAwesomeRating() {
        appRatingEngine.onRatingTrigger()
        rateArticle(ArticleRating.AWESOME.value)
        updateState { copy(userRating = ArticleRating.AWESOME.value) }
    }

    private fun rateArticle(rating: Long) {
        articleRepository.rateArticle(params.articleId, rating)
    }

    override fun onViewPlanClick() {
        screenNavigator.startPlansActivity(
            source = ClickSource.ARTICLE,
            articleId = params.articleId
        )
    }

    override fun onCommentClick(commentId: String) {
        screenNavigator.startCommentsV2Activity(
            contentDescriptor = ContentDescriptor(params.articleId, state.articleEntity?.articleTitle),
            type = CommentsSourceType.ARTICLE,
            clickSource = ClickSource.ARTICLE,
            launchAction = CommentsLaunchAction.View(commentId)
        )
    }

    override fun onCommentLiked(commentId: String, isCurrentlyLiked: Boolean, index: Int) {
        updateState { copy(likeActionState = likeActionState.disable(commentId)) }
        if (isCommentInteractionAllowed()) {
            viewModelScope.launch {
                articleAnalytics.trackLikeCommentAction(
                    sourceId = params.articleId.toString(),
                    sourceType = CommentsSourceType.ARTICLE,
                    commentId = commentId,
                    filterType = userManager.getCommentsSortType(CommentsSourceType.ARTICLE),
                    index = index,
                    isLike = isCurrentlyLiked.not()
                )
                if (isCurrentlyLiked) {
                    unlikeComment(commentId)
                } else {
                    likeComment(commentId)
                }
                updateState { copy(likeActionState = likeActionState.enable(commentId)) }
            }
        } else {
            updateState { copy(likeActionState = likeActionState.enable(commentId)) }
            showInteractionRequirements()
        }
    }

    private fun showInteractionRequirements() {
        when {
            state.articleEntity?.commentsLocked == true -> sendEvent(SnackbarEventRes(R.string.comments_locked_message))
            paywallUtility.shouldUserSeePaywall() -> screenNavigator.startPlansActivity(ClickSource.PAYWALL)
            userManager.isCodeOfConductAccepted().not() -> screenNavigator.showCodeOfConduct()
        }
    }

    private fun isCommentInteractionAllowed(): Boolean {
        return state.articleEntity?.commentsLocked == false &&
            paywallUtility.shouldUserSeePaywall().not() &&
            userManager.isCodeOfConductAccepted()
    }

    private suspend fun likeComment(commentId: String) {
        commentId.toLongOrNull()?.also { commentsRepository.likeArticleComment(params.articleId, it) }
    }

    private suspend fun unlikeComment(commentId: String) {
        commentId.toLongOrNull()?.also { commentsRepository.unlikeArticleComment(params.articleId, it) }
    }

    override fun onCommentReply(parentId: String, commentId: String) {
        state.articleEntity?.let { article ->
            if (article.commentsLocked) {
                sendEvent(SnackbarEventRes(R.string.comments_locked_message))
            } else {
                screenNavigator.startCommentsV2Activity(
                    contentDescriptor = ContentDescriptor(article.articleId, article.articleTitle),
                    type = CommentsSourceType.ARTICLE,
                    clickSource = ClickSource.ARTICLE,
                    launchAction = CommentsLaunchAction.Reply(parentId, commentId)
                )
            }
        }
    }

    override fun onCommentOptionsClicked(commentId: String, index: Int) {
        val id = commentId.toLongOrNull() ?: return
        when (userManager.getCurrentUser()) {
            null -> screenNavigator.startPlansActivity(ClickSource.ARTICLE)
            else -> {
                val indexedComment = state.articleEntity?.comments?.withIndex()?.firstOrNull { indexedComment ->
                    indexedComment.value.commentId == id
                } ?: return
                val event = ArticleContract.Event.ShowCommentOptionsSheet(
                    commentId = id,
                    isUserAuthor = indexedComment.value.authorId == userManager.getCurrentUserId(),
                    isCommentLocked = indexedComment.value.commentLocked,
                    commentIndex = indexedComment.index
                )
                if (!event.isUserAuthor && event.isCommentLocked) {
                    sendEvent(SnackbarEventRes(R.string.comments_locked_message))
                } else {
                    sendEvent(event)
                }
            }
        }
    }

    override fun onCommentShare(permalink: String) {
        if (permalink.isNotEmpty()) screenNavigator.startShareTextActivity(permalink)
    }

    override fun onTapToReloadComments() {
        updateState { copy(isReloadingComments = true) }
        viewModelScope.launch {
            articleRepository.fetchArticleComments(params.articleId)
            updateState { copy(isReloadingComments = false) }
        }
    }

    override fun onViewMoreComments() {
        articleAnalytics.trackViewMoreComments(state.articleEntity)
        screenNavigator.startCommentsV2Activity(
            contentDescriptor = ContentDescriptor(params.articleId, state.articleEntity?.articleTitle),
            type = CommentsSourceType.ARTICLE,
            clickSource = ClickSource.ARTICLE
        )
    }

    override fun onFlagCommentClick(commentId: Long, index: Int) {
        when {
            userManager.isCodeOfConductAccepted().not() -> screenNavigator.showCodeOfConduct()
            userDataRepository.isCommentFlagged(commentId) -> sendEvent(SnackbarEventRes(R.string.global_comment_already_flagged))
            else -> {
                sendEvent(ArticleContract.Event.ShowReportCommentDialog(commentId))
                articleAnalytics.trackFlagComment(
                    commentId = commentId.toString(),
                    filterType = userManager.getCommentsSortType(CommentsSourceType.ARTICLE),
                    index = index
                )
            }
        }
    }

    override fun flagComment(commentId: Long, flagType: FlagReason) {
        viewModelScope.launch {
            commentsRepository.flagCommentArticle(params.articleId, commentId, flagType)
        }
    }

    override fun onEditCommentClicked(commentId: Long) {
        when {
            paywallUtility.shouldUserSeePaywall() ->
                screenNavigator.startPlansActivity(ClickSource.ARTICLE, params.articleId)
            userManager.isAnonymous -> screenNavigator.startCreateAccountWallActivity()
            !userManager.isCodeOfConductAccepted() -> screenNavigator.startCodeOfConductSheetActivityForResult()
            else -> {
                screenNavigator.startCommentsV2Activity(
                    contentDescriptor = ContentDescriptor(params.articleId, state.articleEntity?.articleTitle),
                    type = CommentsSourceType.ARTICLE,
                    clickSource = ClickSource.ARTICLE,
                    launchAction = CommentsLaunchAction.Edit(commentId.toString())
                )
            }
        }
    }

    override fun onDeleteCommentClicked(commentId: Long) {
        viewModelScope.launch {
            commentsRepository.deleteCommentArticle(params.articleId, commentId)
        }
    }

    override fun onCodeOfConductClick() {
        screenNavigator.showCodeOfConduct()
    }

    override fun onMessageModerator() {
        screenNavigator.startSendEmailActivity(Uri.parse(AthleticConfig.EMAIL_EDITOR_URI))
    }

    private val RelatedContentItemId.isArticleType: Boolean
        get() = this.type in setOf(
            RelatedContentItemId.ContentType.ARTICLE,
            RelatedContentItemId.ContentType.QANDA,
            RelatedContentItemId.ContentType.DISCUSSION,
            RelatedContentItemId.ContentType.HEADLINE
        )

    override fun onRelatedContentClicked(contentId: RelatedContentItemId) {
        when {
            contentId.isArticleType -> onRelatedArticleClicked(contentId)
            contentId.type == RelatedContentItemId.ContentType.LIVEBLOG -> {
                screenNavigator.startLiveBlogActivity(contentId.id)
            }
        }
    }

    private fun onRelatedArticleClicked(contentId: RelatedContentItemId) {
        val articleId = contentId.id.toLongOrNull() ?: return
        viewModelScope.launch {
            val clickSource = ClickSource.ARTICLE
            when {
                articleHasPaywall(articleId) ->
                    screenNavigator.startArticlePaywallActivity(articleId, clickSource)
                contentId.type == RelatedContentItemId.ContentType.DISCUSSION ->
                    screenNavigator.startCommentsV2Activity(
                        ContentDescriptor(articleId, state.articleEntity?.articleTitle),
                        CommentsSourceType.DISCUSSION,
                        clickSource
                    )
                contentId.type == RelatedContentItemId.ContentType.QANDA ->
                    screenNavigator.startCommentsV2Activity(
                        ContentDescriptor(articleId, state.articleEntity?.articleTitle),
                        CommentsSourceType.QANDA,
                        clickSource
                    )
                else -> screenNavigator.startArticleActivity(articleId, clickSource)
            }
        }
    }

    override fun onRelatedArticleImpression(percentInView: Float) {
        if (!state.showSpinner) {
            articleAnalytics.trackRelatedArticleImpression(state.articleEntity, percentInView)
        }
    }

    private fun UserData?.isArticleBookmarked(articleId: Long) = this?.articlesSaved?.contains(articleId)
        ?: false
}

data class ArticleDataState(
    val showSpinner: Boolean = true,
    val htmlIsLoaded: Boolean = false,
    val userRating: Long? = null,
    val articleEntity: ArticleEntity? = null,
    val liveRoomData: LiveAudioRoomMiniPlayerUiModel? = null,
    val contentTextSize: ContentTextSize = ContentTextSize.DEFAULT,
    val showPaywall: Boolean = false,
    val isRatedAtLaunch: Boolean = false,
    val isBookmarked: Boolean = false,
    val showWebviewUpgradeInToolbar: Boolean = false,
    val adConfig: AdConfig? = null,
    val likeActionState: LikeActionUiState = LikeActionUiState(),
    val isAuthorClickable: Boolean = false,
    val isReloadingComments: Boolean = false,
) : DataState