package com.theathletic.main.ui

import androidx.lifecycle.LifecycleOwner
import com.theathletic.audio.data.ListenFeedRepository
import com.theathletic.billing.BillingStartupHelper
import com.theathletic.entity.user.UserEntity
import com.theathletic.followables.ObserveUserFollowingUseCase
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository
import com.theathletic.network.NetworkStateManager
import com.theathletic.profile.ShouldShowInitialConsentUseCase
import com.theathletic.rooms.LiveAudioRoomStateManager
import com.theathletic.rooms.ui.LiveAudioRoomMiniPlayerUiModel
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.user.IUserManager
import com.theathletic.user.data.UserRepository
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    @Mock private lateinit var feedPrimaryNavigationItem: FeedPrimaryNavigationItem
    @Mock private lateinit var discoverPrimaryNavigationItem: DiscoverPrimaryNavigationItem
    @Mock private lateinit var accountPrimaryNavigationItem: AccountPrimaryNavigationItem
    @Mock private lateinit var listenFeedRepository: ListenFeedRepository
    @Mock private lateinit var followableRepository: FollowableRepository
    @Mock private lateinit var userFollowingRepository: UserFollowingRepository
    @Mock private lateinit var liveAudioRoomStateManager: LiveAudioRoomStateManager
    @Mock private lateinit var networkStateManager: NetworkStateManager
    @Mock private lateinit var billingStartupHelper: BillingStartupHelper
    @Mock private lateinit var userManager: IUserManager
    @Mock private lateinit var crashLogHandler: ICrashLogHandler
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var lifecycleOwner: LifecycleOwner
    @Mock private lateinit var observeUserFollowingUseCase: ObserveUserFollowingUseCase
    @Mock private lateinit var showInitialConsentUseCase: ShouldShowInitialConsentUseCase

    private var closeable: AutoCloseable? = null

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        whenever(observeUserFollowingUseCase.invoke()).thenReturn(flowOf())
        whenever(listenFeedRepository.getCurrentLiveRooms(any())).thenReturn(flowOf(null))
        whenever(userFollowingRepository.userFollowingStream).thenReturn(flowOf(emptyList()))
        whenever(liveAudioRoomStateManager.currentRoomViewState).thenReturn(flowOf(null))
        whenever(networkStateManager.isNetworkConnected).thenReturn(MutableStateFlow(true))
        mainViewModel = MainViewModel(
            feedPrimaryNavigationItem,
            discoverPrimaryNavigationItem,
            accountPrimaryNavigationItem,
            listenFeedRepository,
            liveAudioRoomStateManager,
            networkStateManager,
            billingStartupHelper,
            userManager,
            crashLogHandler,
            userRepository,
            observeUserFollowingUseCase,
            coroutineTestRule.dispatcherProvider,
            showInitialConsentUseCase
        )
    }

    @After
    fun tearDown() {
        closeable?.close()
    }

    @Test
    fun `onCreate listens for network`() {
        whenever(networkStateManager.isNetworkConnected).thenReturn(MutableStateFlow(true))
        runBlocking {
            whenever(followableRepository.getFilteredLeagues(any())).thenReturn(emptyList())
        }
        mainViewModel.onCreate(lifecycleOwner)
        verify(networkStateManager).isNetworkConnected
    }

    @Test
    fun `onCreate updates offline view state`() = runTest {
        val stateTestFlow = testFlowOf(mainViewModel.state)
        val connectedFlow = MutableStateFlow(true)
        whenever(networkStateManager.isNetworkConnected).thenReturn(connectedFlow)
        whenever(followableRepository.getFilteredLeagues(any())).thenReturn(emptyList())

        mainViewModel.onCreate(lifecycleOwner)
        connectedFlow.emit(false)
        connectedFlow.emit(true)

        assertStream(stateTestFlow).hasReceivedExactly(
            stateFixture(showOfflineAlert = false),
            stateFixture(showOfflineAlert = true),
            stateFixture(showOfflineAlert = false),
        )

        stateTestFlow.finish()
    }

    @Test
    fun `onCreate when User in GracePeriod produces GracePeriodAlert`() = runTest {
        val stateTestFlow = testFlowOf(mainViewModel.state)
        val user = mock<UserEntity> { on { isInGracePeriod } doReturn true }
        whenever(userManager.getCurrentUser()).thenReturn(user)
        whenever(followableRepository.getFilteredLeagues(any())).thenReturn(emptyList())

        mainViewModel.onCreate(lifecycleOwner)

        assertStream(stateTestFlow)
            .lastEvent()
            .isEqualTo(stateFixture(currentAlertType = MainContract.AlertType.GracePeriodAlert))

        stateTestFlow.finish()
    }

    @Test
    fun `onCreate when User has invalid email produces InvalidEmailAlert`() = runTest {
        val stateTestFlow = testFlowOf(mainViewModel.state)
        val user = mock<UserEntity> { on { hasInvalidEmail } doReturn true }
        whenever(userManager.getCurrentUser()).thenReturn(user)
        whenever(followableRepository.getFilteredLeagues(any())).thenReturn(emptyList())

        mainViewModel.onCreate(lifecycleOwner)

        assertStream(stateTestFlow)
            .lastEvent()
            .isEqualTo(stateFixture(currentAlertType = MainContract.AlertType.InvalidEmailAlert))

        stateTestFlow.finish()
    }

    @Test
    fun `onAlertClicked with GracePeriod nulls alert and routes to UpdateCreditCard`() = runTest {
        val testStateFlow = testFlowOf(mainViewModel.state)
        val testEventFlow = testFlowOf(mainViewModel.eventConsumer)
        val user = mock<UserEntity> { on { isInGracePeriod } doReturn true }
        whenever(userManager.getCurrentUser()).thenReturn(user)
        whenever(userManager.getCurrentUserId()).thenReturn(1L)

        mainViewModel.onAlertClicked(MainContract.AlertType.GracePeriodAlert)

        assertStream(testStateFlow).lastEvent().isEqualTo(stateFixture(currentAlertType = null))
        assertStream(testEventFlow).lastEvent()
            .isInstanceOf(MainContract.Event.NavigateToUpdateCreditCard::class.java)

        testStateFlow.finish()
        testEventFlow.finish()
    }

    @Test
    fun `onAlertClicked with InvalidEmailAlert nulls alert and routes to ManageAccount`() =
        runTest {
            val stateTestFlow = testFlowOf(mainViewModel.state)
            val eventTestFlow = testFlowOf(mainViewModel.eventConsumer)

            whenever(userManager.getCurrentUser()).thenReturn(mock())

            mainViewModel.onAlertClicked(MainContract.AlertType.InvalidEmailAlert)

            assertStream(stateTestFlow).lastEvent().isEqualTo(stateFixture(currentAlertType = null))
            assertStream(eventTestFlow).lastEvent()
                .isEqualTo(MainContract.Event.NavigateToManageAccount)

            stateTestFlow.finish()
            eventTestFlow.finish()
        }

    @Test
    fun `updateUserAfterPaymentMethodUpdate logs out and routes to auth if user id different`() =
        runTest {
            val testFlow = testFlowOf(mainViewModel.eventConsumer)
            val user = mock<UserEntity> { on { id } doReturn 2L }
            whenever(userManager.getCurrentUserId()).thenReturn(1L)
            whenever(userRepository.fetchUser()).thenReturn(user)

            mainViewModel.updateUserAfterPaymentMethodUpdate()

            assertStream(testFlow).lastEvent().isEqualTo(MainContract.Event.FinishAndRouteToAuth)
            verify(userManager).logOut()

            testFlow.finish()
        }

    @Test
    fun `updateUserAfterPaymentMethodUpdate logs out and routes to auth if shouldLogUsrOut`() =
        runTest {
            val testFlow = testFlowOf(mainViewModel.eventConsumer)
            val user = mock<UserEntity> {
                on { id } doReturn 1L
                on { shouldLogUserOut } doReturn true
            }
            whenever(userManager.getCurrentUserId()).thenReturn(1L)
            whenever(userRepository.fetchUser()).thenReturn(user)

            mainViewModel.updateUserAfterPaymentMethodUpdate()
            assertStream(testFlow).lastEvent().isEqualTo(MainContract.Event.FinishAndRouteToAuth)
            verify(userManager).logOut()

            testFlow.finish()
        }

    @Test
    fun `updateUserAfterPaymentMethodUpdate saves user if valid`() = runTest {
        val testFlow = testFlowOf(mainViewModel.eventConsumer)
        val user = mock<UserEntity> {
            on { id } doReturn 1L
        }
        whenever(userManager.getCurrentUserId()).thenReturn(1L)
        whenever(userRepository.fetchUser()).thenReturn(user)

        mainViewModel.updateUserAfterPaymentMethodUpdate()

        assertStream(testFlow).hasNoEventReceived()
        verify(userManager).saveCurrentUser(user, false)

        testFlow.finish()
    }

    @Test
    fun `updateUserAfterPaymentMethodUpdate removes currentAlertType`() = runTest {
        val stateTestFlow = testFlowOf(mainViewModel.state)
        // first create a GracePeriod alert
        val user = mock<UserEntity> { on { isInGracePeriod } doReturn true }
        whenever(userManager.getCurrentUser()).thenReturn(user)
        whenever(followableRepository.getFilteredLeagues(any())).thenReturn(emptyList())

        mainViewModel.onCreate(lifecycleOwner)

        // then simulate a user that fixed the billing issue
        val updatedUser = mock<UserEntity> { on { id } doReturn 1L }
        whenever(userManager.getCurrentUser()).thenReturn(updatedUser)
        whenever(userRepository.fetchUser()).thenReturn(updatedUser)
        whenever(userManager.getCurrentUserId()).thenReturn(1L)

        mainViewModel.updateUserAfterPaymentMethodUpdate()

        // now assert the alert is gone
        assertStream(stateTestFlow).hasReceivedExactly(
            // default state
            stateFixture(currentAlertType = null),
            // billing issues
            stateFixture(currentAlertType = MainContract.AlertType.GracePeriodAlert),
            // fixed billing issues
            stateFixture(currentAlertType = null)
        )

        stateTestFlow.finish()
    }

    @Suppress("LongParameterList")
    private fun stateFixture(
        hasLiveRooms: Boolean = false,
        currentLiveRoom: LiveAudioRoomMiniPlayerUiModel? = null,
        navigationItems: NavigationItems = NavigationItems(),
        showOfflineAlert: Boolean = false,
        currentAlertType: MainContract.AlertType? = null,
        currentBottomSheetModal: ModalSheetType? = null
    ) = MainViewModelState(
        hasLiveRooms = hasLiveRooms,
        currentLiveRoom = currentLiveRoom,
        navigationItems = navigationItems,
        showOfflineAlert = showOfflineAlert,
        currentAlertType = currentAlertType,
        currentBottomSheetModal = currentBottomSheetModal
    )
}