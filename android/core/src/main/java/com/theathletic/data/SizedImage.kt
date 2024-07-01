package com.theathletic.data

typealias SizedImages = List<SizedImage>

data class SizedImage(
    val width: Int,
    val height: Int,
    val uri: String
)