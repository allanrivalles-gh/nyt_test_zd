package com.theathletic.billing

import java.io.Serializable

/**
 * Models all the different products we may offer. Do not remove unused values, as they may be
 * dynamically returned and referenced from a RemoteConfig definition.
 */
enum class BillingProducts(val planId: String) {
    IAB_PRODUCT_ANNUAL("sub_annual3"), // annual, 1 week trial, no introductory discount
    IAB_PRODUCT_MONTHLY("sub_monthly_999_t0"), // monthly, no trial, no intro discount
    IAB_PRODUCT_ANNUAL_TRIAL("sub_annual3_trial"), // annual, 30 day trial, no intro discount
    IAB_PRODUCT_ANNUAL_40(SpecialOffer.Annual40.planId), // annual, no trial, 40% off for first year
    IAB_PRODUCT_ANNUAL_50(SpecialOffer.Annual50.planId), // annual, no trial, 50% off for first year
    IAB_PRODUCT_ANNUAL_60(SpecialOffer.Annual60.planId), // annual, no trial, 60% off for first year
    IAB_PRODUCT_ANNUAL_70(SpecialOffer.Annual70.planId), // annual, no trial, 70% off for first year
    IAB_PRODUCT_ANNUAL_UK30_FIXED("sub_annual_uk_30_fixed"), // annual, 7-day trial, 30% off GBP forever
    IAB_PRODUCT_ANNUAL_UK40_FIXED("sub_annual_uk_40_fixed"), // annual, 7-day trial, 40% off GBP forever
    IAB_PRODUCT_ANNUAL_UK30_INTRO("sub_annual_uk_30_intro"), // annual, 7-day trial, 30% off GBP for first year
    IAB_PRODUCT_ANNUAL_UK40_INTRO("sub_annual_uk_40_intro"); // annual, 7-day trial, 40% off GBP for first year

    companion object {
        fun parseFromIdentifier(planId: String?): BillingProducts? = values().firstOrNull { it.planId == planId }
    }
}

sealed class SpecialOffer(val planId: String) : Serializable {
    object Annual40 : SpecialOffer("sub_annual3_40")
    object Annual50 : SpecialOffer("sub_annual3_50")
    object Annual60 : SpecialOffer("sub_annual3_60")
    object Annual70 : SpecialOffer("sub_annual3_70")
}