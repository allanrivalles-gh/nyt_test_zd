package com.theathletic.comments.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

@Composable
fun Toolbar(
    title: String,
    onBackClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(),
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = AthTheme.colors.dark800
            )
        }
        Text(
            text = title,
            style = AthTextStyle.Slab.Bold.Small.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            color = AthTheme.colors.dark800,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}