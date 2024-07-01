package com.theathletic.billing

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_data")
data class PurchaseDataEntity(
    @PrimaryKey val googleToken: String,
    val price: Double?,
    val priceCurrency: String?,
    val planId: String = "",
    val productSku: String,
    val planTerm: String?,
    val planNum: String,
    val lastArticleId: Long?,
    val lastPodcastId: Long?,
    val source: String?,
    val isSubPurchase: Boolean
)