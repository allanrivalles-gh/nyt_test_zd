package com.theathletic.billing

import com.android.billingclient.api.SkuDetails
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.roundToInt

class BillingSku(val skuDetails: SkuDetails) {

    val sku = skuDetails.sku
    val isShowVAT = skuDetails.priceCurrencyCode == "GBP"
    val isMonthlySubscription = skuDetails.subscriptionPeriod == "P1M"

    /**
     * @return formatted introductory price of the sku for the introductory period
     * This is the price the user will be charged if they purchase the sku
     */
    val introPrice: String = skuDetails.introductoryPrice

    /**
     * @return formatted price of the sku for the sku's period.
     * This is the price the user will be charged if they purchase the sku
     * or when the intro price expires.
     */
    val price: String = skuDetails.price

    /**
     * @return percent off offered by the intro price of the sku. This is always a whole number so
     * for example a sku that offers 60% off, this function will return 60.
     */
    val percentOff: Int get() {
        val intro = skuDetails.introductoryPriceAmountMicros / 1_000_000.0
        val normal = skuDetails.priceAmountMicros / 1_000_000.0
        return 100 - ((intro / normal) * 100).roundToInt()
    }

    /**
     * @return formatted monthly price of a sku.
     */
    val monthlyPrice: String get() {
        val format = createNumberFormatter(skuDetails.priceCurrencyCode)
        val price = if (!hasIntroPricePeriod) {
            skuDetails.priceAmountMicros.toDouble() / 1_000_000
        } else {
            skuDetails.introductoryPriceAmountMicros.toDouble() / 1_000_000
        }
        val divideBy = if (isMonthlySubscription) 1 else 12
        return format.format(price / divideBy)
    }

    /*
    * @return the numeric cost that the user will pay
    * */
    val purchasePrice: Double get() {
        return if (hasIntroPricePeriod) {
            skuDetails.introductoryPriceAmountMicros / 1_000_000.0
        } else {
            skuDetails.priceAmountMicros / 1_000_000.0
        }
    }

    private val hasIntroPricePeriod = skuDetails.introductoryPricePeriod.isNotBlank()

    private fun createNumberFormatter(currencyCode: String): NumberFormat {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            roundingMode = RoundingMode.FLOOR
            currency = Currency.getInstance(currencyCode)
        }
    }
}