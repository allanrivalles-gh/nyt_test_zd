package com.theathletic.profile.ui

import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingProducts
import com.theathletic.billing.BillingSku
import com.theathletic.billing.BillingState
import com.theathletic.billing.GeneralBillingFailure
import com.theathletic.billing.PurchaseFailure
import com.theathletic.billing.PurchasePending
import com.theathletic.billing.PurchaseSuccess
import com.theathletic.billing.SetupComplete
import com.theathletic.billing.data.local.BillingPurchase
import com.theathletic.entity.authentication.SubscriptionDataEntity
import com.theathletic.profile.GetCurrentUserUseCase
import com.theathletic.profile.R
import com.theathletic.profile.SaveUserUseCase
import com.theathletic.profile.manageAccountUserFixture
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.testFlowOf
import com.theathletic.utility.BillingPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ManageAccountViewModelTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private val getCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    private val saveUserUseCase = mockk<SaveUserUseCase>()
    private val billingPreferences = mockk<BillingPreferences>()
    private val analytics = mockk<IAnalytics>()

    private val testBillingState = MutableStateFlow<BillingState?>(null)
    private val billingManager = mockk<BillingManager>(relaxUnitFun = true) {
        every { billingState } returns testBillingState
    }

    private val initialManageAccountUser = manageAccountUserFixture()

    private lateinit var testViewModel: ManageAccountViewModel

    @Before
    fun setUp() {
        every { getCurrentUserUseCase.invoke() } returns Result.success(initialManageAccountUser)
        testViewModel = ManageAccountViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            saveUserUseCase = saveUserUseCase,
            billingManager = billingManager,
            billingPreferences = billingPreferences,
            analytics = analytics
        )
    }

    @Test
    fun `calls getCurrentUserUseCase and billingManger onCreate on init`() {
        verify(exactly = 1) { getCurrentUserUseCase.invoke() }
        verify(exactly = 1) { billingManager.onCreate() }
    }

    @Test
    fun `view model emits data when getCurrentUserUseCase succeeds`() {
        val manageAccountUser = manageAccountUserFixture()
        val getCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
        every { getCurrentUserUseCase.invoke() } returns Result.success(manageAccountUserFixture())

        val testObject = ManageAccountViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            saveUserUseCase = saveUserUseCase,
            billingManager = billingManager,
            billingPreferences = billingPreferences,
            analytics = analytics
        )
        val expectedState = ManageAccountViewState(
            uiModel = ManageAccountUiModel(userInformation = manageAccountUser.toUserInformation()),
            isLoading = false
        )

        verify { getCurrentUserUseCase.invoke() }
        assertEquals(expectedState, testObject.viewState.value)
    }

    @Test
    fun `view model emits non loading when getCurrentUserUseCase fails`() {
        val getCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
        every { getCurrentUserUseCase.invoke() } returns Result.failure(Throwable())

        val testObject = ManageAccountViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            saveUserUseCase = saveUserUseCase,
            billingManager = billingManager,
            billingPreferences = billingPreferences,
            analytics = analytics
        )
        val expectedState = ManageAccountViewState(
            isLoading = false
        )

        verify { getCurrentUserUseCase.invoke() }
        assertEquals(expectedState, testObject.viewState.value)
    }

    @Test
    fun `updateFirstName updates currentFirstName and sets valuesChanged to true`() {
        val newName = "New Name"

        testViewModel.updateFirstName(newName)
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(newName, it?.currentCustomer?.firstName)
            assertEquals(true, it?.valuesChanged)
        }
    }

    @Test
    fun `updateFirstName sets valuesChanged to false if current name is same as old name`() {
        val newName = "New Name"

        testViewModel.updateFirstName(newName)
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(newName, it?.currentCustomer?.firstName)
            assertEquals(true, it?.valuesChanged)
        }

        testViewModel.updateFirstName(initialManageAccountUser.firstName.orEmpty())
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(initialManageAccountUser.firstName, it?.currentCustomer?.firstName)
            assertEquals(false, it?.valuesChanged)
        }
    }

    @Test
    fun `updateLastName updates currentLastName and sets valuesChanged to true`() {
        val newName = "New Name"

        testViewModel.updateLastName(newName)
        assertEquals(newName, testViewModel.viewState.value.uiModel.userInformation?.currentCustomer?.lastName)
        assertEquals(true, testViewModel.viewState.value.uiModel.userInformation?.valuesChanged)
    }

    @Test
    fun `updateLastName sets valuesChanged to false if current name is same as old name`() {
        val newName = "New Name"

        testViewModel.updateLastName(newName)
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(newName, it?.currentCustomer?.lastName)
            assertEquals(true, it?.valuesChanged)
        }

        testViewModel.updateLastName(initialManageAccountUser.lastName.orEmpty())
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(initialManageAccountUser.lastName, it?.currentCustomer?.lastName)
            assertEquals(false, it?.valuesChanged)
        }
    }

    @Test
    fun `updateEmail updates currentEmail and sets valuesChanged to true`() {
        val newEmail = "newEmail@email.com"

        testViewModel.updateEmail(newEmail)
        assertEquals(newEmail, testViewModel.viewState.value.uiModel.userInformation?.currentCustomer?.email)
        assertEquals(true, testViewModel.viewState.value.uiModel.userInformation?.valuesChanged)
    }

    @Test
    fun `updateEmail sets valuesChanged to false if current name is same as old email`() {
        val newEmail = "newEmail@email.com"

        testViewModel.updateEmail(newEmail)
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(newEmail, it?.currentCustomer?.email)
            assertEquals(true, it?.valuesChanged)
        }

        testViewModel.updateEmail(initialManageAccountUser.email.orEmpty())
        testViewModel.viewState.value.uiModel.userInformation.let {
            assertEquals(initialManageAccountUser.email, it?.currentCustomer?.email)
            assertEquals(false, it?.valuesChanged)
        }
    }

    @Test
    fun `saveUserChanges invokes saveUserChangesUseCase`() {
        coEvery { saveUserUseCase.invoke(any()) } returns Result.success(Unit)

        val newCustomer = Customer(
            firstName = "newEmail@email.com",
            lastName = "NewFirstName",
            email = "NewLateName",
        )
        testViewModel.updateEmail(newCustomer.email)
        testViewModel.updateFirstName(newCustomer.firstName)
        testViewModel.updateLastName(newCustomer.lastName)

        testViewModel.saveUserChanges()
        coVerify { saveUserUseCase(newCustomer) }
    }

    @Test
    fun `saveUserChanges sets isLoading back to false on failure`() = runTest {
        coEvery { saveUserUseCase.invoke(any()) } coAnswers {
            delay(1)
            Result.failure(Throwable())
        }

        val testFlow = testFlowOf(testViewModel.viewState)
        val newFirstName = "NewFirstName"
        val newLastName = "NewLateName"
        val newEmail = "newEmail@email.com"
        val expectedUIModel = ManageAccountUiModel(
            userInformation = initialManageAccountUser.toUserInformation().copy(
                currentCustomer = Customer(
                    firstName = newFirstName,
                    lastName = newLastName,
                    email = newEmail,
                )
            )
        )

        testViewModel.updateFirstName(newFirstName)
        testViewModel.updateLastName(newLastName)
        testViewModel.updateEmail(newEmail)

        val expectedLoadingState = ManageAccountViewState(
            uiModel = expectedUIModel,
            isLoading = true
        )

        testViewModel.saveUserChanges()

        advanceUntilIdle()

        assertStream(testFlow).eventAt(testFlow.numberOfEvents - 2) { viewState ->
            assertEquals(expectedLoadingState, viewState)
        }
        assertStream(testFlow).lastEvent { viewState ->
            assertEquals(expectedLoadingState.copy(isLoading = false), viewState)
        }
        testFlow.finish()
    }

    @Test
    fun `saveUserChanges calls loadData on success`() = runTest {
        val newEmail = "newEmail@email.com"
        val newFirstName = "NewFirstName"
        val newLastName = "NewLateName"
        val newManageAccountUser = manageAccountUserFixture(
            firstName = newFirstName,
            lastName = newLastName,
            email = newEmail,
        )
        coEvery { saveUserUseCase.invoke(any()) } returns Result.success(Unit)

        every { getCurrentUserUseCase.invoke() } returns Result.success(newManageAccountUser)

        val testFlow = testFlowOf(testViewModel.viewState)

        testViewModel.updateFirstName(newFirstName)
        testViewModel.updateLastName(newLastName)
        testViewModel.updateEmail(newEmail)

        val expectedState = ManageAccountViewState(
            uiModel = ManageAccountUiModel(newManageAccountUser.toUserInformation()),
            isLoading = false
        )

        testViewModel.saveUserChanges()

        advanceUntilIdle()

        verify(exactly = 2) { getCurrentUserUseCase.invoke() }

        assertStream(testFlow).lastEvent { viewState ->
            assertEquals(expectedState, viewState)
        }
        testFlow.finish()
    }

    @Test
    fun `viewModel does not call getSubSkus on SetupComplete when lastPurchaseSku is null`() = runTest {
        val queryPurchasesResult = BillingManager.QueryPurchasesResult(
            purchases = emptyList(),
            isError = false,
            responseCode = 200
        )
        coEvery { billingManager.getSubscriptionPurchases() } returns queryPurchasesResult

        testBillingState.emit(SetupComplete)

        coVerify(exactly = 0) { billingManager.getSubSkus(any()) }
        assertFalse(testViewModel.viewState.value.uiModel.isMonthlySubscriber)
    }

    @Test
    fun `viewModel calls getSubSkus on SetupComplete when lastPurchaseSku is not null`() = runTest {
        val purchase = mockk<BillingPurchase> {
            every { skus } returns arrayListOf("1")
        }
        val queryPurchasesResult = BillingManager.QueryPurchasesResult(
            purchases = listOf(purchase),
            isError = false,
            responseCode = 200
        )
        val billingSku = mockk<BillingSku>(relaxed = true)
        every { billingSku.isMonthlySubscription } returns true
        coEvery { billingManager.getSubscriptionPurchases() } returns queryPurchasesResult
        coEvery { billingManager.getSubSkus(listOf("1")) } returns listOf(billingSku)

        testBillingState.emit(SetupComplete)

        coVerify(exactly = 1) { billingManager.getSubSkus(any()) }
        assertTrue(testViewModel.viewState.value.uiModel.isMonthlySubscriber)
    }

    @Test
    fun `viewModel handles PurchaseSuccess billing state being received`() = runTest {
        val purchase = mockk<BillingPurchase>()
        val purchaseProductEvent = mockk<Event.Payments.ProductPurchase>(relaxed = true)
        coEvery { billingManager.getPurchaseAnalytics(purchase = purchase, isSubSku = true) } returns
            purchaseProductEvent

        testBillingState.emit(PurchaseSuccess(purchase))

        coVerify { billingManager.registerSubPurchaseIfNeeded(purchase = purchase, source = "profile") }
        coVerify { billingManager.getPurchaseAnalytics(purchase = purchase, isSubSku = true) }
        verify { analytics.track(purchaseProductEvent) }
        assertFalse(testViewModel.viewState.value.uiModel.isMonthlySubscriber)
    }

    @Test
    fun `emits ShowSnackBar event when PurchasePending billing state received`() = runTest {
        val testFlow = testFlowOf(testViewModel.viewEvents)

        testBillingState.emit(PurchasePending)

        assertStream(testFlow).hasReceivedExactly(
            ManageAccountEvent.ShowMessage(R.string.gifts_payment_pending_processing)
        )
        testFlow.finish()
        assertFalse(testViewModel.viewState.value.uiModel.isMonthlySubscriber)
    }

    @Test
    fun `emits ShowSnackBar event when PurchaseFailure billing state received`() = runTest {
        val testFlow = testFlowOf(testViewModel.viewEvents)

        testBillingState.emit(PurchaseFailure)

        assertStream(testFlow).hasReceivedExactly(
            ManageAccountEvent.ShowMessage(R.string.global_billing_error_internal)
        )
        testFlow.finish()
    }

    @Test
    fun `emits ShowSnackBar event when GeneralBillingFailure billing state received`() = runTest {
        val testFlow = testFlowOf(testViewModel.viewEvents)

        testBillingState.emit(GeneralBillingFailure)

        assertStream(testFlow).hasReceivedExactly(
            ManageAccountEvent.ShowMessage(R.string.global_error)
        )
        testFlow.finish()
    }

    @Test
    fun `onManageAccountsClicked emits ShowGooglePlaySubscription event when subscriptionData not null`() = runTest {
        val testFlow = testFlowOf(testViewModel.viewEvents)
        every { billingPreferences.subscriptionData } returns SubscriptionDataEntity("", "")

        testViewModel.onManageAccountsClicked()

        assertStream(testFlow).hasReceivedExactly(ManageAccountEvent.ShowGooglePlaySubscription)
        testFlow.finish()
    }

    @Test
    fun `onManageAccountsClicked emits ShowManageSubscriptionDialog when subscriptionData is null and user is subscribed`() = runTest {
        val testFlow = testFlowOf(testViewModel.viewEvents)
        every { billingPreferences.subscriptionData } returns null
        every { getCurrentUserUseCase.invoke() } returns
            Result.success(manageAccountUserFixture(isUserSubscribed = true))

        testViewModel.loadData()
        testViewModel.onManageAccountsClicked()

        assertStream(testFlow).hasReceivedExactly(ManageAccountEvent.ShowManageSubscriptionDialog)
        testFlow.finish()
    }

    @Test
    fun `onManageAccountsClicked emits nothing when subscriptionData is null and user is not subscribed`() = runTest {
        val testFlow = testFlowOf(testViewModel.viewEvents)
        every { billingPreferences.subscriptionData } returns null
        every { getCurrentUserUseCase.invoke() } returns
            Result.success(manageAccountUserFixture(isUserSubscribed = false))

        testViewModel.loadData()
        testViewModel.onManageAccountsClicked()

        assertStream(testFlow).hasNoEventReceived()
        testFlow.finish()
    }

    @Test
    fun `onSwitchToAnnualPlanClicked gets sub skus and calls startSubscriptionChangeFlow`() = runTest {
        val billingSku = mockk<BillingSku>(relaxed = true) {
            every { isMonthlySubscription } returns true
        }
        coEvery { billingManager.getSubSkus(any()) } returns listOf(billingSku)

        testViewModel.onSwitchToAnnualPlanClicked()

        coVerify(exactly = 1) { billingManager.getSubSkus(listOf(BillingProducts.IAB_PRODUCT_ANNUAL.planId)) }
        coVerify(exactly = 1) { billingManager.startSubscriptionChangeFlow(billingSku) }
    }

    @Test
    fun `onSwitchToAnnualPlanClicked does not call startSubscriptionChangeFlow with no sku`() = runTest {
        coEvery { billingManager.getSubSkus(any()) } returns emptyList()

        testViewModel.onSwitchToAnnualPlanClicked()

        coVerify(exactly = 1) { billingManager.getSubSkus(listOf(BillingProducts.IAB_PRODUCT_ANNUAL.planId)) }
        coVerify(exactly = 0) { billingManager.startSubscriptionChangeFlow(any()) }
    }
}