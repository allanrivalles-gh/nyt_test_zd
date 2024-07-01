package com.theathletic.entity.article

import com.squareup.moshi.JsonClass
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class TrendingTopicsEntity(
    override val id: String = "",
    val articleCount: String = "",
    val name: String = "",
    val imageUrl: String?
) : AthleticEntity {
    override val type = AthleticEntity.Type.TRENDING_TOPIC
}