package com.theathletic.gifts.data

import com.android.billingclient.api.SkuDetails
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Calendar

data class GiftsResponse(
    @SerializedName("promotion") var promotion: GiftPromotion? = null,
    @SerializedName("headline") var headline: String? = null,
    @SerializedName("plans") var plans: List<GiftPlan> = emptyList(),
    @SerializedName("shirt_sizes") var shirtSizes: List<GiftShirt>? = emptyList()
) : Serializable

data class GiftPromotion(
    @SerializedName("text") var text: String = "",
    @SerializedName("name") var name: String = ""
) : Serializable

data class GiftPlan(
    @SerializedName("id") var id: Long = 0,
    @SerializedName("google_product_id") var googleProductId: String = "",
    @SerializedName("original_price") var originalPrice: Float = -1f,
    @SerializedName("name") var name: String = "",
    @SerializedName("has_shirt") var hasShirt: Boolean = false,
    @SerializedName("popular") var popular: Boolean = false,
    @SerializedName("index") var index: Long = 0,
    var skuDetails: SkuDetails?
) : Serializable

data class GiftShirt(
    @SerializedName("index") var index: Long = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("value") var value: String = ""
) : Serializable

data class GiftPurchaseResponse(
    @SerializedName("success") val success: Boolean = false
)

data class GiftsDataHolder(
    @SerializedName("text_choose_gift_headline") val textChooseGiftHeadline: String?,
    @SerializedName("promotion_name") val promotionName: String?,
    @SerializedName("purchase_as_email") val purchaseAsEmail: Boolean,
    @SerializedName("shirt_included_with_plan") val shirtIncludedWithPlan: Boolean,
    @SerializedName("selected_plan") val selectedPlan: String,
    @SerializedName("selected_plan_backend_id") val selectedPlanBackendId: Long,
    @SerializedName("selected_shirt_size") val selectedShirtSize: String?,
    @SerializedName("address_country_code") val addressCountryCode: String?,
    @SerializedName("recipient_name") val recipientName: String,
    @SerializedName("recipient_email") val recipientEmail: String?,
    @SerializedName("delivery_date") val deliveryDate: Calendar?,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("sender_email") val senderEmail: String,
    @SerializedName("address_name") val addressName: String?,
    @SerializedName("address1") val address1: String?,
    @SerializedName("address2") val address2: String?,
    @SerializedName("address_city") val addressCity: String?,
    @SerializedName("address_state") val addressState: String?,
    @SerializedName("address_zip") val addressZIP: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("displayable_gift_plan_name_and_price") val displayableGiftPlanNameAndPrice: String?
) {
    companion object {
        fun fromJson(json: String): GiftsDataHolder = Gson().fromJson(json, GiftsDataHolder::class.java)
    }

    fun toJson(): String = Gson().toJson(this)
}