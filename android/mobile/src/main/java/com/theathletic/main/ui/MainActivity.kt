package com.theathletic.main.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.SparseArray
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.snackbar.Snackbar
import com.iterable.iterableapi.IterableApi
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.activity.DeepLinkDispatcherActivity
import com.theathletic.activity.article.ReferredArticleIdManager
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.attributionsurvey.SurveyRouter
import com.theathletic.featureswitch.Features
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.followable.analyticsId
import com.theathletic.followable.analyticsType
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.gifts.ui.GiftSheetDialogFragment
import com.theathletic.links.deep.DeeplinkEventConsumer
import com.theathletic.links.deep.DeeplinkEventProducer
import com.theathletic.main.DeeplinkThrottle
import com.theathletic.main.MainEvent
import com.theathletic.main.MainEventProducer
import com.theathletic.main.ui.listen.MainListenScreen
import com.theathletic.main.ui.misc.ActionTextSnackbar
import com.theathletic.main.ui.navigation.TabState
import com.theathletic.notifications.AthleticNotificationHandler
import com.theathletic.notifications.AthleticNotificationPayload
import com.theathletic.notifications.fromBundle
import com.theathletic.rooms.ui.LiveAudioEvent
import com.theathletic.rooms.ui.LiveAudioEventProducer
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerInteractor
import com.theathletic.rooms.ui.LiveRoomMiniPlayer
import com.theathletic.scores.navigation.scoresNavGraph
import com.theathletic.scores.ui.DateChangeEventProducer
import com.theathletic.scores.ui.DateChangeEvents
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.DisplayPreferences
import com.theathletic.ui.widgets.BottomSheetExpansionType
import com.theathletic.ui.widgets.ModalBottomSheetLayout
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.user.IUserManager
import com.theathletic.utility.DateChangeListener
import com.theathletic.utility.DateChangeReceiver
import com.theathletic.utility.FeatureIntroductionPreferences
import com.theathletic.utility.IActivityUtility
import com.theathletic.utility.InAppUpdateHelper
import com.theathletic.utility.device.DeviceInfo
import com.theathletic.utility.getSerializableCompat
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.get
import org.koin.core.parameter.parametersOf
import timber.log.Timber

private const val SELECTED_TAB_STATE_KEY = "selected_tab"

class MainActivity : BaseActivity(), LiveAudioRoomMiniPlayerInteractor {

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
        var activityExists = false

        private const val USER_UPDATE_REQUEST_CODE = 0x1
        private const val IN_APP_UPDATE_REQUEST_CODE = 3232
    }

    private val viewModel by inject<MainViewModel>()
    private val displayPreferences by inject<DisplayPreferences>()
    private val analytics by inject<Analytics>()
    private val notificationHandler by inject<AthleticNotificationHandler>()
    private val userFollowingRepository by inject<UserFollowingRepository>()
    private val userManager by inject<IUserManager>()
    private val inAppUpdateHelper by inject<InAppUpdateHelper>()
    private val features by inject<Features>()

    // Deeplink Handling
    private val deeplinkEventConsumer by inject<DeeplinkEventConsumer>()
    private val deeplinkEventProducer by inject<DeeplinkEventProducer>()
    private val deeplinkDelegate by inject<MainActivityDeeplinkDelegate> {
        parametersOf(navigator)
    }
    private val deeplinkThrottle by inject<DeeplinkThrottle>()

    private val mainEventProducer by inject<MainEventProducer>()

    // Attribution Survey dependencies
    private val surveyRouter by inject<SurveyRouter>()
    private val referredArticleIdManager by inject<ReferredArticleIdManager>()
    private val activityUtility by inject<IActivityUtility>()

    private var currentlySelectedTab = MutableStateFlow(BottomTabItem.FEED)

    private val dateChangeEventProducer by inject<DateChangeEventProducer>()
    private val mainNavigationEventConsumer by inject<MainNavigationEventConsumer>()
    private val liveAudioEventProducer by inject<LiveAudioEventProducer>()

    private val featureIntroPreferences by inject<FeatureIntroductionPreferences>()
    private var showFeatureIntroModal: Boolean = true

    private lateinit var dateChangeReceiver: DateChangeReceiver
    private val mainNavigationEventProducer by inject<MainNavigationEventProducer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)

        activityExists = true

        restoreCurrentlySelectedTab(savedInstanceState)

        if (savedInstanceState == null) {
            handleIntentExtras(intent.extras)
        }
        deeplinkEventConsumer.observe(lifecycleScope, ::handleDeeplink)

        userFollowingRepository.fetchUserFollowingItems()
        displayAttributionSurveyIfQualified()
        showConsentPromptIfNeeded()

        inAppUpdateHelper.checkForAppStoreUpdate(this, IN_APP_UPDATE_REQUEST_CODE) {
            showCompleteUpdateSnackbar()
        }

        referredArticleIdManager.checkAndRouteToArticle(this)
        IterableApi.getInstance().inAppManager.setAutoDisplayPaused(false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observe<MainContract.Event> { handleEvent(it) }
            }
        }

        setContent {
            AthleticTheme(lightMode = displayPreferences.shouldDisplayDayMode(this@MainActivity)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AthTheme.colors.dark100)
                ) {
                    MainActivityScreen()
                }
            }
        }

        /* Uncomment below to enable the Feature Intro screens */
        /* if (featureIntroPreferences.hasSeenFeatureIntro.not()) {
            featureIntroPreferences.hasSeenFeatureIntro = true
            navigator.startFeatureIntroActivity()
        } */

        launchTopSportsNewsIntroIfRequired()
        initDateChangeReceiver()
    }

    private fun showConsentPromptIfNeeded() {
        if (features.isTcfConsentEnabled) {
            lifecycleScope.launch {
                if (viewModel.shouldAskForConsent()) {
                    navigator.startConsentWebView(false)
                }
            }
        }
    }

    private fun initDateChangeReceiver() {
        dateChangeReceiver = DateChangeReceiver(object : DateChangeListener {
            override fun onDateChanged() {
                lifecycleScope.launch {
                    dateChangeEventProducer.emit(DateChangeEvents.OnDateChanged)
                }
            }
        })

        // register the BR for date change
        val filter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        registerReceiver(dateChangeReceiver, filter)
    }

    private fun launchTopSportsNewsIntroIfRequired() {
        val areFeatureConditionsMet = featureIntroPreferences.hasSeenTopSportsNewsIntro.not() &&
            features.isTopSportsNewsFeatureIntroEnabled
        val areUserConditionsMet = NotificationManagerCompat.from(this).areNotificationsEnabled() &&
            userManager.isTopSportsNewsOptIn().not()

        if (areFeatureConditionsMet && areUserConditionsMet) {
            featureIntroPreferences.hasSeenTopSportsNewsIntro = true
            navigator.startFeatureIntroActivity()
        }
    }

    @Composable
    private fun MainActivityScreen() {
        val viewModelState by viewModel.state.collectAsState(initial = MainViewModelState())
        val coroutineScope = rememberCoroutineScope()

        Column(Modifier.fillMaxSize()) {
            Box(modifier = Modifier.animateContentSize()) {
                if (viewModelState.showOfflineAlert) {
                    OfflineAlert { viewModel.onOfflineAlertClicked() }
                }
            }
            Box(modifier = Modifier.animateContentSize()) {
                InAppAlert(viewModelState.currentAlertType)
            }
            Box(modifier = Modifier.weight(1f)) {
                ModalBottomSheetLayout(
                    currentModal = viewModelState.currentBottomSheetModal,
                    onDismissed = viewModel::dismissBottomSheet,
                    expansionType = BottomSheetExpansionType.HALF_EXPANSION_SUPPORT,
                    modalSheetContent = {
                        ModalFollowableBottomSheet(navigator = navigator)
                    }
                ) {
                    MainContent(
                        state = viewModelState
                    )
                }
            }
            viewModelState.currentLiveRoom?.let { model ->
                LiveRoomMiniPlayer(
                    liveRoomId = model.id,
                    title = model.title,
                    subtitle = model.subtitle,
                    onMiniPlayerClick = ::onRoomMiniPlayerClicked,
                    onCloseClick = ::onRoomCloseClicked,
                )
            }
        }

        MainActionTextSnackbar(coroutineScope)
        BackHandler { viewModel.backAction() }
    }

    @Composable
    private fun MainActionTextSnackbar(coroutineScope: CoroutineScope) {
        val snackBarHostState = remember { SnackbarHostState() }
        var actionSnackbarData: ActionSnackbarData? by remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            mainNavigationEventConsumer.collect { event ->
                when (event) {
                    is MainNavigationEvent.ShowActionTextSnackbar -> {
                        actionSnackbarData = event.actionSnackbarData
                        val job = launch {
                            snackBarHostState.showSnackbar("", duration = SnackbarDuration.Indefinite)
                        }
                        delay(event.actionSnackbarData.duration)
                        actionSnackbarData = null
                        job.cancel()
                    }
                    else -> {}
                }
            }
        }

        SnackbarHost(hostState = snackBarHostState) {
            actionSnackbarData?.let { data ->
                Box(modifier = Modifier.fillMaxSize()) {
                    ActionTextSnackbar(
                        text = data.text,
                        icon = data.icon,
                        actionTag = data.tag,
                        isSuccess = data.isSuccess,
                        onActionStringClick = { tagAction ->
                            coroutineScope.launch {
                                deeplinkEventProducer.emit(tagAction.item)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun InAppAlert(alertType: MainContract.AlertType?) {
        when (alertType) {
            MainContract.AlertType.SuccessfulPurchaseAlert -> {
                SimpleAlert(text = getString(R.string.gifts_pending_payment_ready)) {
                    viewModel.onAlertClicked(alertType)
                }
            }
            MainContract.AlertType.GracePeriodAlert -> {
                SimpleAlert(text = getString(R.string.global_stripe_fail)) {
                    viewModel.onAlertClicked(alertType)
                }
            }
            MainContract.AlertType.InvalidEmailAlert -> {
                SimpleAlert(text = getString(R.string.global_email_fail)) {
                    viewModel.onAlertClicked(alertType)
                }
            }
            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdateHelper.checkForFinishedUpdate { showCompleteUpdateSnackbar() }
    }

    override fun onPause() {
        super.onPause()
        deeplinkThrottle.releaseThrottle()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Suppress("LongMethod")
    @Composable
    private fun MainContent(state: MainViewModelState) {
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val showBottomNavBar = MainNavigation.routes.contains(navController.currentRouteState.value)

        LaunchedEffect(state.routeForNavigation) {
            state.routeForNavigation?.let { route ->
                navController.navigateMain(route)
                viewModel.onNavigatedToRoute()
            }
        }

        Scaffold(
            modifier = Modifier.semantics { testTagsAsResourceId = true },
            bottomBar = {
                MainBottomNavigation(
                    currentRoute = navController.currentRouteState.value,
                    onRouteSelected = { route -> viewModel.navigateToRoute(route) },
                    isVisible = showBottomNavBar,
                    onScrollToTopRequested = { route ->
                        coroutineScope.launch() {
                            mainEventProducer.emit(MainEvent.ScrollToTop(route))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(color = AthTheme.colors.dark100)
            ) {
                NavigationGraph(navController = navController, state = state)
            }
        }
    }

    private fun NavHostController.navigateMain(route: String) {
        navigate(route) {
            popUpTo(graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    @Composable
    fun NavigationGraph(
        navController: NavHostController,
        state: MainViewModelState,
    ) {
        val tabState = retrieveTabState()

        NavHost(navController, startDestination = MainNavigation.Feed.route) {
            composable(MainNavigation.Feed.route) {
                val isComposeFeedEnabled = features.isFollowingFeedComposeEnabled && get<DeviceInfo>().isTablet.not()
                MainFeedScreen(
                    navItems = state.navigationItems,
                    onEditClick = { viewModel.showBottomSheetModal(true) },
                    onNavigationItemClick = onFeedNavigationItemClick,
                    fragmentManager = { supportFragmentManager },
                    feedPrimaryNavigationItem = viewModel.feedPrimaryNavigationItem,
                    mainNavEventConsumer = { mainNavigationEventConsumer },
                    tabState = tabState,
                    isComposeFeedEnabled = isComposeFeedEnabled,
                    route = MainNavigation.Feed.route
                )
            }

            scoresNavGraph(navController = navController)

            composable(MainNavigation.Discover.route) {
                val isComposeFeedEnabled = features.isComposeFeedEnabled && get<DeviceInfo>().isTablet.not()

                MainDiscoverScreen(
                    discoverPrimaryNavigationItem = viewModel.discoverPrimaryNavigationItem,
                    fragmentManager = { supportFragmentManager },
                    onSearchClick = onDiscoverSearchClick,
                    route = MainNavigation.Discover.route,
                    mainNavEventConsumer = { mainNavigationEventConsumer },
                    tabState = tabState,
                    isComposeFeedEnabled = isComposeFeedEnabled
                )
            }

            composable(MainNavigation.Listen.route) {
                MainListenScreen(
                    activity = { this@MainActivity },
                    navigator = { navigator },
                    mainNavEventConsumer = { mainNavigationEventConsumer },
                    initialSelectedTabIndex = state.initialTabIndex,
                    onInitialTabSelected = viewModel::cleanInitialTab
                )
            }

            composable(MainNavigation.Account.route) {
                AccountScreen(
                    accountPrimaryNavigationItem = viewModel.accountPrimaryNavigationItem,
                    fragmentManager = { supportFragmentManager },
                    mainNavEventConsumer = { mainNavigationEventConsumer },
                    tabState = tabState
                )
            }
        }
    }

    @Composable
    private fun retrieveTabState(): TabState {
        val selectedTabId = remember { mutableStateOf(abs(BottomTabItem.FEED.titleId)) }
        val savedStates by rememberSaveable { mutableStateOf(SparseArray<Fragment.SavedState>()) }
        val tabState by remember { derivedStateOf { TabState(savedStates, selectedTabId) } }
        Timber.v("[SELECTED] ${tabState.selectedTabId} | [STATES] ${tabState.savedStates}")
        return tabState
    }

    private val onFeedNavigationItemClick: (Followable.Id, Int) -> Unit = { id, index ->
        navigator.startStandaloneFeedActivity(FeedType.fromFollowable(id))
        analytics.track(
            Event.Home.NavigationClick(
                object_type = id.analyticsType,
                object_id = id.analyticsId,
                h_index = index.toString()
            )
        )
    }

    private val onDiscoverSearchClick: () -> Unit = {
        navigator.startSearchActivity()
        analytics.track(Event.Frontpage.SearchClick())
    }

    private fun restoreCurrentlySelectedTab(bundle: Bundle?) {
        bundle?.getSerializableCompat<BottomTabItem>(SELECTED_TAB_STATE_KEY)?.let {
            currentlySelectedTab.value = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dateChangeReceiver)
        activityExists = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putSerializable(SELECTED_TAB_STATE_KEY, currentlySelectedTab.value)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO(Todd): refactor our BaseActivity to use the newer Result pattern
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            USER_UPDATE_REQUEST_CODE -> viewModel.updateUserAfterPaymentMethodUpdate()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntentExtras(intent?.extras)
    }

    private fun handleIntentExtras(extras: Bundle?) = lifecycleScope.launch {
        if (extras == null) return@launch

        showFeatureIntroModal = false
        AthleticNotificationPayload.fromBundle(extras)?.let { payload ->
            notificationHandler.onNotificationOpen(this@MainActivity, payload)
            return@launch
        }

        extras.getString(DeepLinkDispatcherActivity.EXTRAS_DEEPLINK_URL)?.let { deeplink ->
            handleDeeplink(deeplink)
        }
    }

    private fun handleEvent(event: com.theathletic.utility.Event) {
        when (event) {
            is MainContract.Event.FinishAndRouteToAuth -> {
                navigator.startAuthenticationActivity()
                finish()
            }
            is MainContract.Event.NavigateToGiftSheet -> {
                GiftSheetDialogFragment.newInstance().show(
                    supportFragmentManager,
                    "gift_bottom_bar_sheet"
                )
            }
            is MainContract.Event.NavigateToUpdateCreditCard -> {
                navigator.startUpdateCreditCardActivity(
                    event.email,
                    event.idHash,
                    USER_UPDATE_REQUEST_CODE
                )
            }
            is MainContract.Event.NavigateToManageAccount -> {
                navigator.startManageAccountActivity(USER_UPDATE_REQUEST_CODE)
            }
            is MainContract.Event.CloseApplication -> finish()
            else -> {
                // ignore
            }
        }
    }

    private suspend fun handleDeeplink(deeplink: String) {
        deeplinkDelegate.parseDeeplink(
            activity = this@MainActivity,
            uri = deeplink,
            selectPrimaryTab = ::onPrimaryTabSelected,
        )
    }

    private fun onPrimaryTabSelected(
        tab: BottomTabItem,
        selectedInnerTabIndex: Int? = null,
        skipAnalyticsEvent: Boolean = false
    ) {
        if (!skipAnalyticsEvent) {
            analytics.track(
                Event.Navigation.SwitchPrimaryTab(
                    view = currentlySelectedTab.value.analyticsView,
                    object_type = tab.analyticsView
                )
            )
        }

        currentlySelectedTab.value = tab
        val route = when (tab) {
            BottomTabItem.FEED -> MainNavigation.Feed.route
            BottomTabItem.SCORES -> MainNavigation.Scores.route
            BottomTabItem.DISCOVER -> MainNavigation.Discover.route
            BottomTabItem.LISTEN -> MainNavigation.Listen.route
            BottomTabItem.ACCOUNT -> MainNavigation.Account.route
            else -> MainNavigation.Feed.route
        }
        viewModel.navigateToRoute(route, selectedInnerTabIndex)
    }

    private fun onPrimaryTabReselected() {
        lifecycleScope.launch {
            mainNavigationEventProducer.emit(MainNavigationEvent.ScrollToTopOfFeed)
        }
    }

    private fun showCompleteUpdateSnackbar() {
        Snackbar.make(
            findViewById(android.R.id.content),
            R.string.flexible_update_app_installed,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.flexible_update_reload) { inAppUpdateHelper.completeUpdate() }
            show()
        }
    }

    override fun onRoomMiniPlayerClicked(id: String) {
        analytics.track(
            Event.LiveRoom.Click(
                view = "liveroom_miniplayer",
                element = "open",
                object_type = "room_id",
                object_id = id
            )
        )
        navigator.startLiveAudioRoomActivity(id = id)
    }

    override fun onRoomCloseClicked(id: String) {
        analytics.track(
            Event.LiveRoom.Click(
                view = "liveroom_miniplayer",
                element = "close",
                object_type = "room_id",
                object_id = id
            )
        )
        lifecycleScope.launch {
            liveAudioEventProducer.emit(LiveAudioEvent.LeaveRoom())
        }
    }

    private fun displayAttributionSurveyIfQualified() {
        if (surveyRouter.shouldPresentSurvey()) {
            activityUtility.startAttributionSurveyActivityForResult(
                this,
                "onboarding",
                "article",
                referredArticleIdManager.getArticleId() ?: -1L
            )
        }
    }

    private val BottomTabItem.analyticsView
        get() = when (this) {
            BottomTabItem.FEED -> "feed"
            BottomTabItem.SCORES -> "scores"
            BottomTabItem.FRONTPAGE -> "front_page"
            BottomTabItem.LISTEN -> "listen"
            BottomTabItem.ACCOUNT -> "account"
            BottomTabItem.DISCOVER -> "discover"
        }
}

@Composable
fun MainBottomNavigation(
    currentRoute: String,
    onScrollToTopRequested: (route: String) -> Unit,
    onRouteSelected: (route: String) -> Unit = {},
    isVisible: Boolean = true
) {
    if (isVisible.not()) return

    val selectedRoute = remember { mutableStateOf("") }

    BottomNavigation {
        MainNavigation.destinations.forEach { destination ->
            val isSelected = destination.route == currentRoute
            val tintColor = if (isSelected) AthTheme.colors.dark800 else AthTheme.colors.dark400

            BottomNavigationItem(
                modifier = Modifier.testTag(stringResource(id = destination.title)),
                selected = isSelected,
                onClick = {
                    if (selectedRoute.value != destination.route) {
                        selectedRoute.value = destination.route
                        onRouteSelected(destination.route)
                    } else {
                        onScrollToTopRequested(destination.route)
                    }
                },
                icon = {
                    ResourceIcon(
                        resourceId = destination.icon,
                        modifier = Modifier.size(22.dp),
                        tint = tintColor
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.title),
                        color = tintColor,
                        style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            )
        }
    }
}