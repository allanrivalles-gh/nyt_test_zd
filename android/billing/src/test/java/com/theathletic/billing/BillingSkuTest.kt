package com.theathletic.billing

import com.android.billingclient.api.SkuDetails
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

internal class BillingSkuTest {

    @Mock private lateinit var skuDetails: SkuDetails
    private lateinit var billingSku: BillingSku

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        whenever(skuDetails.sku).thenReturn("SKU")
        whenever(skuDetails.priceCurrencyCode).thenReturn("USD")
        whenever(skuDetails.subscriptionPeriod).thenReturn("P1Y")
        whenever(skuDetails.introductoryPrice).thenReturn("$29.99")
        whenever(skuDetails.price).thenReturn("$59.99")
        whenever(skuDetails.priceAmountMicros).thenReturn(59_990_000)
        whenever(skuDetails.introductoryPricePeriod).thenReturn("")

        billingSku = BillingSku(skuDetails)
    }

    @Test
    fun `percentOff is correctly calculated for our common discounts`() {
        whenever(skuDetails.introductoryPriceAmountMicros).thenReturn(35_990_000)
        assertEquals(40, billingSku.percentOff)

        whenever(skuDetails.introductoryPriceAmountMicros).thenReturn(29_990_000)
        assertEquals(50, billingSku.percentOff)

        whenever(skuDetails.introductoryPriceAmountMicros).thenReturn(23_990_000)
        assertEquals(60, billingSku.percentOff)

        whenever(skuDetails.introductoryPriceAmountMicros).thenReturn(19_990_000)
        assertEquals(67, billingSku.percentOff)
    }

    @Test
    fun `monthlyPrice is correctly calculated for normal annual subscription`() {
        assertEquals("$4.99", billingSku.monthlyPrice)
    }

    @Test
    fun `monthlyPrice is correctly calculated for intro price`() {
        whenever(skuDetails.introductoryPricePeriod).thenReturn("P1Y")
        whenever(skuDetails.introductoryPriceAmountMicros).thenReturn(29_990_000)
        billingSku = BillingSku(skuDetails)

        assertEquals("$2.49", billingSku.monthlyPrice)
    }

    @Test
    fun `monthlyPrice is correctly calculated for normal monthly subscription()`() {
        whenever(skuDetails.subscriptionPeriod).thenReturn("P1M")
        whenever(skuDetails.priceAmountMicros).thenReturn(9_990_000)
        billingSku = BillingSku(skuDetails)

        assertEquals("$9.99", billingSku.monthlyPrice)
    }
}