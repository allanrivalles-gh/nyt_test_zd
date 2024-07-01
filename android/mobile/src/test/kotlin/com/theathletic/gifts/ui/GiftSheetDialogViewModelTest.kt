package com.theathletic.gifts.ui

import android.content.res.Resources
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.mlykotom.valifi.ValiFi
import com.theathletic.AthleticApplication
import com.theathletic.billing.BillingManager
import com.theathletic.billing.BillingSku
import com.theathletic.billing.BillingState
import com.theathletic.billing.PurchasePending
import com.theathletic.billing.SetupComplete
import com.theathletic.gifts.data.GiftPlan
import com.theathletic.gifts.data.GiftsRepository
import com.theathletic.gifts.data.GiftsResponse
import com.theathletic.injection.baseModule
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.user.IUserManager
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.flow.MutableStateFlow
import org.alfonz.view.StatefulLayout
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

class GiftSheetDialogViewModelTest : KoinTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            baseModule,
            module {
                factory { giftsRepository }
                factory { billingManager }
                factory { userManager }
                factory { coroutineTestRule.dispatcherProvider }
            }
        )
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Mock private lateinit var giftsRepository: GiftsRepository
    @Mock private lateinit var billingManager: BillingManager
    @Mock private lateinit var userManager: IUserManager

    private lateinit var billingState: MutableStateFlow<BillingState?>
    private lateinit var viewModel: GiftSheetDialogViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        declareMock<ICrashLogHandler>()

        billingState = MutableStateFlow(null)
        whenever(billingManager.billingState).thenReturn(billingState)
        whenever(userManager.isAnonymous).thenReturn(false)

        val application: AthleticApplication = mock()
        val resources: Resources = mock()

        AthleticApplication.setInstance(application)
        whenever(application.resources).thenReturn(resources)
        whenever(application.getString(anyInt())).thenReturn("")

        ValiFi.install()

        viewModel = GiftSheetDialogViewModel()
    }

    @Test
    fun `setupBillingManager creates BillingManager`() {
        viewModel.setupBillingManager()

        verify(billingManager).onCreate()
    }

    @Test
    fun `handleBillingStates gets gifts on SetupComplete`() = runTest {
        whenever(Looper.getMainLooper()).thenReturn(Looper.getMainLooper())
        val skuId = "product"
        val plan = GiftPlan(id = 1, googleProductId = skuId, popular = false, skuDetails = null)
        val giftsResponse = GiftsResponse(plans = listOf(plan))
        val skuDetailsResponse = mock<SkuDetails>()
        val emptyQuery = BillingManager.QueryPurchasesResult(
            purchases = emptyList(),
            isError = false,
            responseCode = BillingClient.BillingResponseCode.OK
        )

        val billingSku = mock<BillingSku> {
            on { sku } doReturn skuId
            on { skuDetails } doReturn skuDetailsResponse
        }

        whenever(giftsRepository.getGifts()).thenReturn(Response.success(giftsResponse))
        whenever(billingManager.getInAppSkus(listOf(skuId))).thenReturn(listOf(billingSku))
        whenever(billingManager.getInAppPurchases()).thenReturn(emptyQuery)

        viewModel.setupBillingManager()
        billingState.value = SetupComplete

        verify(giftsRepository).getGifts()
        assertEquals(StatefulLayout.CONTENT, viewModel.state.get())
    }

    @Test
    fun `handleBillingStates sets state to progress on PurchasePending`() {
        viewModel.setupBillingManager()
        billingState.value = PurchasePending

        assertEquals(StatefulLayout.PROGRESS, viewModel.state.get())
    }

    @Test
    fun selectDefaultPlan_found_selectPopular() {
        viewModel.plans.apply {
            clear()
            add(GiftPlan(id = 1, googleProductId = "product_1", popular = false, skuDetails = null))
            add(GiftPlan(id = 2, googleProductId = "product_2", popular = true, skuDetails = null))
            add(GiftPlan(id = 3, googleProductId = "product_3", popular = false, skuDetails = null))
        }
        viewModel.selectDefaultPlan()
        assertEquals("product_2", viewModel.valiSelectedPlan.get())
    }

    @Test
    fun selectDefaultPlan_notFound_defaultToFirst() {
        viewModel.plans.apply {
            clear()
            add(GiftPlan(id = 1, googleProductId = "product_1", popular = false, skuDetails = null))
            add(GiftPlan(id = 2, googleProductId = "product_2", popular = false, skuDetails = null))
            add(GiftPlan(id = 3, googleProductId = "product_3", popular = false, skuDetails = null))
        }
        viewModel.selectDefaultPlan()
        assertEquals("product_1", viewModel.valiSelectedPlan.get())
    }
}