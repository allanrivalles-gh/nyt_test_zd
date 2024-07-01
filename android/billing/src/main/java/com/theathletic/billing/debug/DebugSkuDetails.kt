package com.theathletic.billing.debug

import com.android.billingclient.api.SkuDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.theathletic.billing.BillingProducts
import java.text.NumberFormat
import java.util.Currency

@Suppress("LongParameterList")
@JsonClass(generateAdapter = true)
class DebugSkuDetails(
    val productId: String,
    val type: String,
    val price: String,
    @Json(name = "price_amount_micros") val priceAmountMicros: Int,
    @Json(name = "price_currency_code") val priceCurrencyCode: String,
    val subscriptionPeriod: String? = null,
    val title: String = "",
    val description: String = "",
    val skuDetailsToken: String = "fake_token",
    val freeTrialPeriod: String? = null,
    val introductoryPriceAmountMicros: Int? = null,
    val introductoryPrice: String? = null,
    val introductoryPricePeriod: String? = null,
    val introductoryPriceCycles: Int? = null
)

class SkuDetailsFactory(
    moshi: Moshi,
    debugBillingCurrency: String
) {

    private val adapter = moshi.adapter(DebugSkuDetails::class.java)
    private val currencyCode = debugBillingCurrency
    private val formatter = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(debugBillingCurrency)
    }

    @SuppressWarnings("LongMethod")
    fun createSubscriptionSku(
        sku: String
    ): SkuDetails {
        val skuDetails = when (BillingProducts.parseFromIdentifier(sku)) {
            BillingProducts.IAB_PRODUCT_MONTHLY -> DebugSkuDetails(
                productId = sku,
                type = "subs",
                price = formatter.format(9.99),
                priceAmountMicros = 9990000,
                subscriptionPeriod = "P1M",
                priceCurrencyCode = currencyCode
            )

            BillingProducts.IAB_PRODUCT_ANNUAL_40 -> DebugSkuDetails(
                productId = sku,
                type = "subs",
                price = formatter.format(59.99),
                priceAmountMicros = 59990000,
                subscriptionPeriod = "P1Y",
                introductoryPriceAmountMicros = 35990000,
                introductoryPrice = formatter.format(35.99),
                introductoryPricePeriod = "P1Y",
                introductoryPriceCycles = 1,
                priceCurrencyCode = currencyCode
            )

            BillingProducts.IAB_PRODUCT_ANNUAL_50 -> DebugSkuDetails(
                productId = sku,
                type = "subs",
                price = formatter.format(59.99),
                priceAmountMicros = 59990000,
                subscriptionPeriod = "P1Y",
                introductoryPriceAmountMicros = 29990000,
                introductoryPrice = formatter.format(29.99),
                introductoryPricePeriod = "P1Y",
                introductoryPriceCycles = 1,
                priceCurrencyCode = currencyCode
            )

            BillingProducts.IAB_PRODUCT_ANNUAL_60 -> DebugSkuDetails(
                productId = sku,
                type = "subs",
                price = formatter.format(59.99),
                priceAmountMicros = 59990000,
                subscriptionPeriod = "P1Y",
                introductoryPriceAmountMicros = 23990000,
                introductoryPrice = formatter.format(23.99),
                introductoryPricePeriod = "P1Y",
                introductoryPriceCycles = 1,
                priceCurrencyCode = currencyCode
            )

            BillingProducts.IAB_PRODUCT_ANNUAL_70 -> DebugSkuDetails(
                productId = sku,
                type = "subs",
                price = formatter.format(59.99),
                priceAmountMicros = 59990000,
                subscriptionPeriod = "P1Y",
                introductoryPriceAmountMicros = 19990000,
                introductoryPrice = formatter.format(19.99),
                introductoryPricePeriod = "P1Y",
                introductoryPriceCycles = 1,
                priceCurrencyCode = currencyCode
            )

            else -> DebugSkuDetails(
                productId = sku,
                type = "subs",
                price = formatter.format(59.99),
                priceAmountMicros = 59990000,
                subscriptionPeriod = "P1Y",
                freeTrialPeriod = "P1W",
                priceCurrencyCode = currencyCode
            )
        }

        return SkuDetails(adapter.toJson(skuDetails))
    }

    fun createInAppSku(
        sku: String
    ): SkuDetails {
        val skuDetails = when (sku) {
            "gift_3m" -> DebugSkuDetails(
                productId = sku,
                type = "inapp",
                price = formatter.format(29.99),
                priceAmountMicros = 29990000,
                priceCurrencyCode = currencyCode
            )

            "gift_2y" -> DebugSkuDetails(
                productId = sku,
                type = "inapp",
                price = formatter.format(99.99),
                priceAmountMicros = 99990000,
                priceCurrencyCode = currencyCode
            )

            else -> DebugSkuDetails(
                productId = sku,
                type = "inapp",
                price = formatter.format(59.99),
                priceAmountMicros = 59990000,
                priceCurrencyCode = currencyCode
            )
        }

        return SkuDetails(adapter.toJson(skuDetails))
    }
}