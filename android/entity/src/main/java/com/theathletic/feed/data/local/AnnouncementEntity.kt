package com.theathletic.feed.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class AnnouncementEntity(
    // Static data
    override val id: String,
    val title: String = "",
    val subtitle: String = "",
    val ctaText: String = "",
    val imageUrl: String = "",
    val deeplinkUrl: String = "",
    val endDate: Datetime = Datetime(0),

    // Mutable data
    var isDismissed: Boolean = false
) : AthleticEntity {
    override val type = AthleticEntity.Type.ANNOUNCEMENT
}