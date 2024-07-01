package com.theathletic.slidestories.ui.slidecomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.slidestories.R
import com.theathletic.themes.AthColor.Companion.Gray300
import com.theathletic.themes.AthColor.Companion.Gray500
import com.theathletic.themes.AthColor.Companion.Gray700
import com.theathletic.themes.AthColor.Companion.Red800
import com.theathletic.themes.AthTextStyle
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun SlideLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray300)
    ) {
        CircularProgressIndicator(
            color = Gray500,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SlideErrorState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray300),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ResourceIcon(
            resourceId = R.drawable.slide_error_icon,
            tint = Red800,
            modifier = Modifier
                .size(32.dp)
        )

        Text(
            text = stringResource(R.string.slide_loading_error_message),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            maxLines = 2,
            textAlign = TextAlign.Center,
            color = Gray700,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

enum class SlidesStates {
    LOADING,
    ERROR,
    COMPLETED
}

@Composable
@Preview
fun SlideLoadingState_Preview() {
    SlideLoadingState()
}

@Composable
@Preview
fun SlideErrorState_Preview() {
    SlideErrorState()
}