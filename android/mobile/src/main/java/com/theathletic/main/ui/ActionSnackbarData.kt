package com.theathletic.main.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString

data class ActionSnackbarData(
    val text: AnnotatedString,
    val icon: ImageVector? = null,
    val tag: String? = null,
    val duration: Long = 2000,
    val isSuccess: Boolean
)