package com.theathletic.billing

import com.theathletic.billing.data.local.BillingPurchase
import com.theathletic.billing.data.local.BillingPurchaseState
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.utility.BillingPreferences
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BillingStartupHelperTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var billingManager: BillingManager
    @Mock private lateinit var billingPreferences: BillingPreferences
    @Mock private lateinit var subPurchase: BillingPurchase
    @Mock private lateinit var inappPurchase: BillingPurchase
    @Mock private lateinit var crashLogHandler: ICrashLogHandler

    private lateinit var billingStateFlow: MutableStateFlow<BillingState?>
    private lateinit var billingStartupHelper: BillingStartupHelper

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        billingStateFlow = MutableStateFlow(null)
        whenever(billingManager.billingState).thenReturn(billingStateFlow)
        whenever(inappPurchase.purchaseState).thenReturn(BillingPurchaseState.Purchased)
        whenever(subPurchase.purchaseToken).thenReturn("")

        billingStartupHelper = BillingStartupHelper(
            billingManager,
            billingPreferences,
            crashLogHandler,
            coroutineTestRule.dispatcherProvider,
        )
    }

    @Test
    fun `startup failed and throws an exception`() = runTest {
        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupFailed

        verify(crashLogHandler).logException(any())
        verify(billingManager, times(1)).onDestroy()
        verify(callback, never()).invoke()
    }

    @Test
    fun `user with sub and in app purchase, purchase history updated and callback called`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(okQuery(listOf(subPurchase)))
        whenever(billingManager.getInAppPurchases()).thenReturn(okQuery(listOf(inappPurchase)))

        val hasSubHistory = true
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager).registerSubPurchaseIfNeeded(subPurchase, null)
        val productId = subPurchase.skus.lastOrNull()
        val purchaseToken = subPurchase.purchaseToken
        verify(billingPreferences).setSubscriptionData(productId, purchaseToken)
        verify(callback).invoke()
        verify(billingManager, times(1)).onDestroy()
    }

    @Test
    fun `user with sub, purchase history updated and callback not called`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(okQuery(listOf(subPurchase)))
        whenever(billingManager.getInAppPurchases()).thenReturn(emptyQuery)

        val hasSubHistory = true
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager).registerSubPurchaseIfNeeded(subPurchase, null)
        val productId = subPurchase.skus.lastOrNull()
        val purchaseToken = subPurchase.purchaseToken
        verify(billingPreferences).setSubscriptionData(productId, purchaseToken)
        verify(callback, never()).invoke()
        verify(billingManager, times(1)).onDestroy()
    }

    @Test
    fun `user with no sub or in app purchase, purchase history updated and callback not called`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(emptyQuery)
        whenever(billingManager.getInAppPurchases()).thenReturn(emptyQuery)

        val hasSubHistory = false
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager, never()).registerSubPurchaseIfNeeded(subPurchase, null)
        verify(billingPreferences).setSubscriptionData(null, null)
        verify(callback, never()).invoke()
        verify(billingManager, times(1)).onDestroy()
    }

    @Test
    fun `user with in app purchase, null callback does not cause crash`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(emptyQuery)
        whenever(billingManager.getInAppPurchases()).thenReturn(okQuery(listOf(inappPurchase)))

        val hasSubHistory = false
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        billingStartupHelper.updateBillingInfo(null)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager, never()).registerSubPurchaseIfNeeded(subPurchase, null)
        verify(billingPreferences).setSubscriptionData(null, null)
        verify(billingManager, times(1)).onDestroy()
    }

    @Test
    fun `user failed to query inapp and sub, purchase history and prefs not updated, callback not called`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(errorQuery)
        whenever(billingManager.getInAppPurchases()).thenReturn(errorQuery)

        val hasSubHistory = false
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager, never()).registerSubPurchaseIfNeeded(subPurchase, null)
        verify(billingPreferences, never()).setSubscriptionData(null, null)
        verify(callback, never()).invoke()
        verify(billingManager, never()).registerGiftPurchasesStartup()
        verify(billingManager, times(1)).onDestroy()
    }

    @Test
    fun `user with inapp and failed to query sub, purchase history and prefs not updated, callback called`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(errorQuery)
        whenever(billingManager.getInAppPurchases()).thenReturn(okQuery(listOf(inappPurchase)))

        val hasSubHistory = false
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager, never()).registerSubPurchaseIfNeeded(subPurchase, null)
        verify(billingPreferences, never()).setSubscriptionData(null, null)
        verify(callback, times(1)).invoke()
        verify(billingManager, times(1)).registerGiftPurchasesStartup()
        verify(billingManager, times(1)).onDestroy()
    }

    @Test
    fun `user with sub and failed to query inapp, purchase history and prefs updated, callback not called`() = runTest {
        whenever(billingManager.getSubscriptionPurchases()).thenReturn(okQuery(listOf(subPurchase)))
        whenever(billingManager.getInAppPurchases()).thenReturn(errorQuery)

        val hasSubHistory = true
        whenever(billingManager.hasSubscriptionPurchaseHistory()).thenReturn(hasSubHistory)

        val callback = mock<() -> Unit>()
        billingStartupHelper.updateBillingInfo(callback)
        billingStateFlow.value = SetupComplete

        verify(billingManager, times(1)).onCreate()
        verify(billingPreferences).hasPurchaseHistory = hasSubHistory
        verify(billingManager).registerSubPurchaseIfNeeded(subPurchase, null)
        val productId = subPurchase.skus.lastOrNull()
        val purchaseToken = subPurchase.purchaseToken
        verify(billingPreferences).setSubscriptionData(productId, purchaseToken)
        verify(callback, never()).invoke()
        verify(billingManager, never()).registerGiftPurchasesStartup()
        verify(billingManager, times(1)).onDestroy()
    }
}