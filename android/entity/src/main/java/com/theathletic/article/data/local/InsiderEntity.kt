package com.theathletic.article.data.local

import com.squareup.moshi.JsonClass
import com.theathletic.entity.local.AthleticEntity

@JsonClass(generateAdapter = true)
data class InsiderEntity(
    override val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val role: String = "",
    val bio: String = "",
    val imageUrl: String = "",
    val insiderImageUrl: String = ""
) : AthleticEntity {
    override val type = AthleticEntity.Type.INSIDER
}