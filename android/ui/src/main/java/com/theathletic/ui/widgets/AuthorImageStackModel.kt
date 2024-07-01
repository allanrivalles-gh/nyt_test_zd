package com.theathletic.ui.widgets

data class AuthorImageStackModel(
    val authorImage1: String?,
    val authorImage2: String? = null,
    val authorImage3: String? = null,
    val authorImage4: String? = null,
    val excessCount: Int? = null,
    val displayImageCount: Int = 0
)