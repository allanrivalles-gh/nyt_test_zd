package com.theathletic.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.theathletic.analytics.IAnalytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.billing.data.local.BillingPurchase
import com.theathletic.billing.data.local.BillingPurchaseState
import com.theathletic.featureswitch.Features
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.user.IUserManager
import com.theathletic.utility.ActivityProvider
import com.theathletic.utility.BackoffState
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.IPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val okResult = BillingResult
    .newBuilder()
    .setResponseCode(BillingClient.BillingResponseCode.OK)
    .build()

private val failureResult = BillingResult
    .newBuilder()
    .setResponseCode(BillingClient.BillingResponseCode.ERROR)
    .build()

class BillingManagerImplTest {

    @Mock private lateinit var analytics: IAnalytics
    @Mock private lateinit var billingClient: BillingClient
    @Mock private lateinit var preferences: IPreferences
    @Mock private lateinit var billingPreferences: BillingPreferences
    @Mock private lateinit var userManager: IUserManager
    @Mock private lateinit var billingRepository: BillingRepository
    @Mock private lateinit var registerGooglePurchaseScheduler: RegisterGooglePurchaseScheduler
    @Mock private lateinit var crashLogHandler: ICrashLogHandler
    @Mock private lateinit var backoffStrategy: BackoffState
    @Mock private lateinit var activityProvider: ActivityProvider
    @Mock private lateinit var features: Features

    private lateinit var billingManager: BillingManagerImpl
    private var closeable: AutoCloseable? = null

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
        whenever(userManager.isUserSubscribedOnBackend()).thenReturn(false)
        whenever(features.isExtraSubLoggingEnabled).thenReturn(true)

        billingManager = BillingManagerImpl(
            analytics,
            preferences,
            billingPreferences,
            userManager,
            registerGooglePurchaseScheduler,
            billingRepository,
            crashLogHandler,
            mock { on { getBillingClient(any()) } doReturn billingClient },
            backoffStrategy,
            activityProvider,
            features
        )
    }

    @After
    fun tearDown() {
        closeable?.close()
    }

    @Test
    fun `onCreate starts billing client connection`() {
        billingManager.onCreate()
        verify(billingClient).startConnection(billingManager)
    }

    @Test
    fun `onDestroy ends billing client connection`() {
        billingManager.onDestroy()
        verify(billingClient).endConnection()
    }

    @Test
    fun `getSubscriptionPurchases returns valid purchases`() = runTest {
        val purchase = createPurchaseMockWithDefaults()
        whenever(purchase.purchaseToken).thenReturn("abc")

        doAnswer {
            it.getArgument<PurchasesResponseListener>(1)
                .onQueryPurchasesResponse(okResult, listOf(purchase))
        }.`when`(billingClient).queryPurchasesAsync(any<String>(), any())

        val result = billingManager.getSubscriptionPurchases().purchases.orEmpty()
        assertEquals(1, result.size)
        assertEquals(purchase.purchaseToken, result.first().purchaseToken)
    }

    @Test
    fun `getSubscriptionPurchases returns empty list if no purchases`() = runTest {
        doAnswer {
            it.getArgument<PurchasesResponseListener>(1)
                .onQueryPurchasesResponse(okResult, emptyList())
        }.`when`(billingClient).queryPurchasesAsync(any<String>(), any())

        val result = billingManager.getSubscriptionPurchases().purchases
        assertTrue(result?.isEmpty() ?: false)
    }

    @Test
    fun `getSubscriptionPurchases failure generates billing failure and log it as exception`() = runTest {
        val purchase = mock<Purchase>()
        doAnswer {
            it.getArgument<PurchasesResponseListener>(1)
                .onQueryPurchasesResponse(failureResult, listOf(purchase))
        }.`when`(billingClient).queryPurchasesAsync(any<String>(), any())

        val result = billingManager.getSubscriptionPurchases().purchases
        verify(crashLogHandler).logException(any())
        assertTrue(billingManager.billingState.value is GeneralBillingFailure)
        assertTrue(result == null)
    }

    @Test
    fun `getInAppPurchases returns valid purchases`() = runTest {
        val purchase = createPurchaseMockWithDefaults()
        whenever(purchase.purchaseToken).thenReturn("abc")

        doAnswer {
            it.getArgument<PurchasesResponseListener>(1)
                .onQueryPurchasesResponse(okResult, listOf(purchase))
        }.`when`(billingClient).queryPurchasesAsync(any<String>(), any())

        val result = billingManager.getInAppPurchases().purchases
        assertEquals(1, result?.size)
        assertEquals(purchase.purchaseToken, result?.first()?.purchaseToken)
    }

    @Test
    fun `getInAppPurchases returns empty list if no purchases`() = runTest {
        doAnswer {
            it.getArgument<PurchasesResponseListener>(1)
                .onQueryPurchasesResponse(okResult, emptyList())
        }.`when`(billingClient).queryPurchasesAsync(any<String>(), any())

        val result = billingManager.getInAppPurchases().purchases
        assertTrue(result?.isEmpty() ?: false)
    }

    @Test
    fun `getInAppPurchases failure generates billing failure and log it as exception`() = runTest {
        val purchase = mock<Purchase>()
        doAnswer {
            it.getArgument<PurchasesResponseListener>(1)
                .onQueryPurchasesResponse(failureResult, listOf(purchase))
        }.`when`(billingClient).queryPurchasesAsync(any<String>(), any())

        val result = billingManager.getInAppPurchases().purchases
        verify(crashLogHandler).logException(any())
        assertTrue(billingManager.billingState.value is GeneralBillingFailure)
        assertTrue(result == null)
    }

    @Test
    fun `onPurchasesUpdated emits PurchaseSuccess on successful purchase`() {
        val purchase = createPurchaseMockWithDefaults()
        whenever(purchase.purchaseState).thenReturn(Purchase.PurchaseState.PURCHASED)

        billingManager.onPurchasesUpdated(
            mock { on { responseCode } doReturn BillingClient.BillingResponseCode.OK },
            mutableListOf(purchase)
        )

        assertTrue(billingManager.billingState.value is PurchaseSuccess)
        verify(analytics).track(Event.Billing.BillingSuccessfulPurchase)
    }

    @Test
    fun `onPurchasesUpdated emits PurchaseSuccess on no purchases`() {
        billingManager.onPurchasesUpdated(
            mock { on { responseCode } doReturn BillingClient.BillingResponseCode.OK },
            null
        )

        assertTrue(billingManager.billingState.value is PurchaseFailure)
        verify(analytics).track(Event.Billing.BillingFailedPurchase)
    }

    @Test
    fun `onPurchaseUpdated emits PurchasePending on pending purchase`() {
        val purchase = createPurchaseMockWithDefaults()
        whenever(purchase.purchaseState).thenReturn(Purchase.PurchaseState.PENDING)

        billingManager.onPurchasesUpdated(
            mock { on { responseCode } doReturn BillingClient.BillingResponseCode.OK },
            mutableListOf(purchase)
        )

        assertTrue(billingManager.billingState.value is PurchasePending)
        verify(analytics).track(Event.Billing.BillingPendingPurchase)
    }

    @Test
    fun `onPurchaseUpdated emits PurchaseCancelled on user cancelled purchase`() {
        val purchase = mock<Purchase>()

        billingManager.onPurchasesUpdated(
            mock { on { responseCode } doReturn BillingClient.BillingResponseCode.USER_CANCELED },
            mutableListOf(purchase)
        )

        assertTrue(billingManager.billingState.value is PurchaseCancelled)
        verify(analytics).track(Event.Billing.BillingCancelledPurchase)
    }

    @Test
    fun `onPurchaseUpdated emits PurchaseFailure on billing error`() {
        val purchase = mock<Purchase>()

        billingManager.onPurchasesUpdated(
            mock {
                on { responseCode } doReturn BillingClient.BillingResponseCode.ERROR
                on { debugMessage } doReturn "error"
            },
            mutableListOf(purchase)
        )

        assertTrue(billingManager.billingState.value is PurchaseFailure)
        verify(analytics).track(Event.Billing.BillingFailedPurchase)
    }

    @Test
    fun `onBillingSetupFinished emits SetupComplete`() {
        billingManager.onBillingSetupFinished(mock())
        assertTrue(billingManager.billingState.value is SetupComplete)
    }

    @Test
    fun `try to connect using a backoff strategy on billing service disconnect`() {
        val functionCaptor = argumentCaptor<() -> Unit>()

        billingManager.onBillingServiceDisconnected()

        verify(backoffStrategy).runBlockingBackoff(eq(billingClient.isReady), functionCaptor.capture())
        functionCaptor.firstValue.invoke()
        verify(billingClient).startConnection(billingManager)
    }

    @Test
    fun `registerPurchaseIfNeeded does not register acknowledged purchase when user is sub on backend`() = runTest {
        val purchase = mock<BillingPurchase> {
            on { purchaseState } doReturn BillingPurchaseState.Purchased
            on { purchaseToken } doReturn ""
            on { isAcknowledged } doReturn true
        }
        whenever(userManager.isUserSubscribedOnBackend()).thenReturn(true)

        billingManager.registerSubPurchaseIfNeeded(purchase, "")

        val productId = purchase.skus.lastOrNull()
        val purchaseToken = purchase.purchaseToken
        verify(billingPreferences, never()).setSubscriptionData(productId, purchaseToken)
        verify(registerGooglePurchaseScheduler, never()).scheduleIfNeeded(any())
        verify(analytics, never()).track(Event.Billing.BillingRegisterSubscription)
    }

    @Test
    fun `registerPurchase does not register acknowledged purchase when user is not sub on backend and toggle is off`() = runTest {
        val purchase = mock<BillingPurchase> {
            on { purchaseState } doReturn BillingPurchaseState.Purchased
            on { purchaseToken } doReturn ""
            on { isAcknowledged } doReturn true
        }
        whenever(features.isExtraSubLoggingEnabled).thenReturn(false)

        billingManager.registerSubPurchaseIfNeeded(purchase, "")

        val productId = purchase.skus.lastOrNull()
        val purchaseToken = purchase.purchaseToken
        verify(billingPreferences, never()).setSubscriptionData(productId, purchaseToken)
        verify(registerGooglePurchaseScheduler, never()).scheduleIfNeeded(any())
        verify(analytics, never()).track(Event.Billing.BillingRegisterSubscription)
    }

    companion object {
        private fun createPurchaseMockWithDefaults(): Purchase = mock {
            on { purchaseState } doReturn Purchase.PurchaseState.PURCHASED
            on { purchaseToken } doReturn ""
            on { orderId } doReturn ""
            on { signature } doReturn ""
            on { originalJson } doReturn ""
        }
    }
}