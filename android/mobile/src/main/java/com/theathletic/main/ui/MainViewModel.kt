package com.theathletic.main.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.audio.data.ListenFeedRepository
import com.theathletic.billing.BillingStartupHelper
import com.theathletic.extension.extGetMd5
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.data.domain.UserFollowing
import com.theathletic.network.NetworkStateManager
import com.theathletic.profile.ShouldShowInitialConsentUseCase
import com.theathletic.repository.safeApiRequest
import com.theathletic.rooms.LiveAudioRoomStateManager
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerUiModel
import com.theathletic.ui.LegacyAthleticViewModel
import com.theathletic.ui.updateState
import com.theathletic.ui.widgets.ModalBottomSheetType
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import com.theathletic.utility.coroutines.DispatcherProvider
import com.theathletic.utility.coroutines.collectIn
import com.theathletic.utility.logging.ICrashLogHandler
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber

class MainViewModel @AutoKoin constructor(
    val feedPrimaryNavigationItem: FeedPrimaryNavigationItem,
    val discoverPrimaryNavigationItem: DiscoverPrimaryNavigationItem,
    val accountPrimaryNavigationItem: AccountPrimaryNavigationItem,
    private val listenFeedRepository: ListenFeedRepository,
    private val liveAudioRoomStateManager: LiveAudioRoomStateManager,
    private val networkStateManager: NetworkStateManager,
    private val billingStartupHelper: BillingStartupHelper,
    private val userManager: IUserManager,
    private val crashLogHandler: ICrashLogHandler,
    private val userRepository: UserRepository,
    private val observeUserFollowing: ObserveUserFollowingUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val showInitialConsentUseCase: ShouldShowInitialConsentUseCase
) : LegacyAthleticViewModel(),
    KoinComponent,
    DefaultLifecycleObserver {

    // TODO: Convert MainViewModel to AthleticViewModel
    val state by lazy { MutableStateFlow(MainViewModelState()) }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        setupListeners()
        checkForUserAlerts()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        billingStartupHelper.updateBillingInfo {
            state.updateState { copy(currentAlertType = MainContract.AlertType.SuccessfulPurchaseAlert) }
        }
    }

    fun onOfflineAlertClicked() {
        state.updateState { copy(showOfflineAlert = false) }
    }

    fun onAlertClicked(alertType: MainContract.AlertType) {
        val event = when (alertType) {
            MainContract.AlertType.GracePeriodAlert -> {
                val user = userManager.getCurrentUser() ?: return
                val email = user.email ?: ""
                val userIdHash = userManager.getCurrentUserId().toString().extGetMd5()
                MainContract.Event.NavigateToUpdateCreditCard(email, userIdHash)
            }
            MainContract.AlertType.InvalidEmailAlert -> MainContract.Event.NavigateToManageAccount
            MainContract.AlertType.SuccessfulPurchaseAlert -> MainContract.Event.NavigateToGiftSheet
        }
        state.updateState { copy(currentAlertType = null) }
        sendEvent(event)
    }

    fun updateUserAfterPaymentMethodUpdate() = viewModelScope.launch {
        // TODO this user validation should most likely live in a dedicated class somewhere
        safeApiRequest(dispatcherProvider.io) {
            userRepository.fetchUser()
        }.onSuccess { user ->
            when {
                user.id != userManager.getCurrentUserId() -> {
                    crashLogHandler.trackException(
                        ICrashLogHandler.UserException("Error: User login"),
                        "Local user ID:  ${userManager.getCurrentUserId()} doesn't match with server response ID: ${user.id}"
                    )
                    userManager.logOut()
                    sendEvent(MainContract.Event.FinishAndRouteToAuth)
                }

                user.shouldLogUserOut -> {
                    crashLogHandler.trackException(
                        ICrashLogHandler.UserException("Error: User login"),
                        "should_log_user_out is set to true!"
                    )
                    userManager.logOut()
                    sendEvent(MainContract.Event.FinishAndRouteToAuth)
                }

                else -> {
                    if (user.endDate?.before(Date()) == true) {
                        crashLogHandler.trackException(
                            ICrashLogHandler.SubscriptionException("Warning: end_date expired"),
                            "Server end_date is: ${user.endDate} / Current local time is: ${Date()}"
                        )
                    }

                    userManager.saveCurrentUser(user, false)
                    checkForUserAlerts()
                }
            }
        }.onError {
            Timber.e(it)
            crashLogHandler.trackException(
                ICrashLogHandler.UserException("Warning: User login error"),
                "Error updating user at onResume method. Reason: ${it.message}",
            )
        }
    }

    suspend fun shouldAskForConsent(): Boolean = showInitialConsentUseCase()

    private fun checkForUserAlerts() {
        val user = userManager.getCurrentUser() ?: return
        val currentAlertType = state.value.currentAlertType
        val alertType = when {
            user.isInGracePeriod -> MainContract.AlertType.GracePeriodAlert
            user.hasInvalidEmail -> MainContract.AlertType.InvalidEmailAlert
            currentAlertType == MainContract.AlertType.SuccessfulPurchaseAlert -> currentAlertType
            else -> null
        }
        state.updateState { copy(currentAlertType = alertType) }
    }

    private fun setupListeners() {
        listenFeedRepository.getCurrentLiveRooms(fetch = true)
            .collectIn(viewModelScope) { currentRooms ->
                state.updateState { copy(hasLiveRooms = currentRooms?.hasLiveRooms == true) }
            }
        observeUserFollowing().collectIn(viewModelScope) { newFollowables ->
            state.updateState {
                copy(
                    navigationItems = navigationItems.copy(
                        showPlaceholder = false,
                        navItems = newFollowables.map { it.toNavItem() }
                    )
                )
            }
        }

        liveAudioRoomStateManager.currentRoomViewState.collectIn(viewModelScope) { liveRoom ->
            state.updateState { copy(currentLiveRoom = liveRoom) }
        }
        networkStateManager.isNetworkConnected.collectIn(viewModelScope) { isConnected ->
            state.updateState { copy(showOfflineAlert = !isConnected) }
        }
    }

    fun showBottomSheetModal(show: Boolean) = viewModelScope.launch {
        state.updateState {
            if (show) {
                copy(currentBottomSheetModal = ModalSheetType.ManageFollowingModalSheet)
            } else {
                copy(currentBottomSheetModal = null)
            }
        }
    }

    fun dismissBottomSheet() {
        showBottomSheetModal(false)
    }

    override fun onCleared() {
        super.onCleared()
        dismissBottomSheet()
        feedPrimaryNavigationItem.onDestroy()
        discoverPrimaryNavigationItem.onDestroy()
        accountPrimaryNavigationItem.onDestroy()
    }

    fun backAction() {
        if (state.value.currentBottomSheetModal != null) {
            showBottomSheetModal(false)
        } else {
            sendEvent(MainContract.Event.CloseApplication)
        }
    }

    fun navigateToRoute(route: String?, innerTabIndex: Int? = null) {
        if (state.value.routeForNavigation != route) {
            state.updateState { copy(routeForNavigation = route, initialTabIndex = innerTabIndex) }
        }
    }

    fun onNavigatedToRoute() {
        state.updateState { copy(routeForNavigation = null) }
    }

    fun cleanInitialTab() {
        state.updateState { copy(initialTabIndex = null) }
    }
}

data class MainViewModelState(
    val hasLiveRooms: Boolean = false,
    val currentLiveRoom: LiveAudioRoomMiniPlayerUiModel? = null,
    val navigationItems: NavigationItems = NavigationItems(),
    val showOfflineAlert: Boolean = false,
    val currentAlertType: MainContract.AlertType? = null,
    val currentBottomSheetModal: ModalSheetType? = null,
    val isMainFeedLoaded: Boolean = false,
    val routeForNavigation: String? = null,
    val initialTabIndex: Int? = null,
    val showInitialConsent: Boolean = false,
)

data class NavigationItems(
    val showPlaceholder: Boolean = true,
    val navItems: List<NavigationItem> = emptyList()
) {
    val items = if (showPlaceholder) placeholderNavigationItems else navItems

    companion object {
        val placeholderNavigationItems = List<NavigationItem>(5) {
            SimpleNavItem(
                id = FollowableId(
                    it.toString(),
                    type = FollowableType.TEAM
                ),
                title = " ".repeat(10)
            )
        }
    }
}

sealed class ModalSheetType : ModalBottomSheetType {
    object ManageFollowingModalSheet : ModalSheetType()
}

private fun UserFollowing.toNavItem(): NavigationItem {
    val navItem = SimpleNavItem(
        id = id,
        title = shortName,
        imageUrl = imageUrl,
        color = color
    )
    return when (id.type) {
        FollowableType.TEAM -> TeamNavItem(navItem)
        FollowableType.LEAGUE -> LeagueNavItem(navItem)
        FollowableType.AUTHOR -> AuthorNavItem(navItem)
    }
}