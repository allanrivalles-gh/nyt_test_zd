package com.theathletic.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun SimpleAlert(
    text: String,
    onAlertClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AthTheme.colors.red)
            .clickable { onAlertClicked() }
            .height(80.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(end = 10.dp),
            text = text,
            lineHeight = 15.sp,
            color = AthTheme.colors.dark800,
            overflow = TextOverflow.Ellipsis,
            style = AthTextStyle.Calibre.Utility.Regular.Small
        )
        ResourceIcon(
            resourceId = R.drawable.ic_arrow_right,
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = AthTheme.colors.dark800
        )
    }
}

@Preview
@Composable
private fun SimpleAlert_Preview() {
    SimpleAlert("Gift Purchase Successful. Tap here to finish") {
    }
}

@Preview
@Composable
private fun SimpleAlert_LongPreview() {
    SimpleAlert("Something happened, please tap here to see what. ".repeat(8)) {
    }
}

@Preview
@Composable
private fun SimpleAlert_LightPreview() {
    AthleticTheme(lightMode = true) {
        SimpleAlert("Gift Purchase Successful. Tap here to finish") {
        }
    }
}