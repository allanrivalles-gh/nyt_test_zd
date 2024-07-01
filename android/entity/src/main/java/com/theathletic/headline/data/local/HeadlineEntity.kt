package com.theathletic.headline.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.datetime.Datetime
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class HeadlineEntity(
    override val id: String,
    val headline: String = "",
    val byline: String = "",
    val commentsCount: Int = 0,
    val createdAt: Datetime = Datetime(0L),
    val updatedAt: Datetime = Datetime(0L),
    val imageUrls: List<String> = emptyList(),
    val commentsDisabled: Boolean = false
) : AthleticEntity {
    override val type = AthleticEntity.Type.HEADLINE
}