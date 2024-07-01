package com.theathletic.debugtools.logs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

class AnalyticsLogUi {
    data class AnalyticsLogItem(
        val label: String,
        val params: Map<String, String>,
        val collectors: String,
    )

    interface Interactor {
        fun onBackClick()
        fun onClearClicked()
    }
}

@Composable
fun AnalyticsLogScreen(
    logs: List<AnalyticsLogUi.AnalyticsLogItem>,
    interactor: AnalyticsLogUi.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(AthTheme.colors.dark100)
    ) {
        Toolbar(interactor)
        AnalyticsLogList(logs)
    }
}

@Composable
private fun Toolbar(interactor: AnalyticsLogUi.Interactor) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        IconButton(onClick = interactor::onBackClick) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = AthTheme.colors.dark800
            )
        }
        Text(
            text = "Analytics History Log",
            style = AthTextStyle.Slab.Bold.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f, true)
        )

        TextButton(onClick = interactor::onClearClicked) {
            Text(
                text = "CLEAR",
                style = AthTextStyle.Slab.Bold.Small,
                color = AthTheme.colors.dark800,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun AnalyticsLogList(
    infoList: List<AnalyticsLogUi.AnalyticsLogItem>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(infoList) {
            AnalyticsLogItemRow(
                item = it
            )
        }
    }
}

@Composable
private fun AnalyticsLogItemRow(
    item: AnalyticsLogUi.AnalyticsLogItem
) {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            .fillMaxWidth()
    ) {

        Text(
            text = item.label,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                .fillMaxWidth()
        )

        Divider(
            color = AthTheme.colors.dark600,
            thickness = 1.dp,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        )

        Text(
            text = annotatedParamsText(paramsMap = item.params),
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(top = 4.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
        )

        Text(
            text = item.collectors,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier
                .padding(top = 4.dp, start = 20.dp, end = 20.dp, bottom = 4.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun annotatedParamsText(paramsMap: Map<String, String>): AnnotatedString {
    val builder = AnnotatedString.Builder()
    paramsMap.entries.forEach {
        builder.withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = AthTheme.colors.dark500)) {
            append(it.key)
        }
        builder.append(" = ")
        builder.withStyle(style = SpanStyle(color = AthTheme.colors.dark800)) {
            append(it.value)
        }
        builder.append("\n")
    }

    return builder.toAnnotatedString()
}

@Preview
@Composable
private fun AnalyticsLogScreen_Preview() {
    val mockInteractor = object : AnalyticsLogUi.Interactor {
        override fun onBackClick() {}
        override fun onClearClicked() {}
    }

    AnalyticsLogScreen(AnalyticsLogPreviewData.analyticsLogItems, mockInteractor)
}

@Preview
@Composable
private fun AnalyticsLogScreen_PreviewLight() {
    val mockInteractor = object : AnalyticsLogUi.Interactor {
        override fun onBackClick() {}
        override fun onClearClicked() {}
    }

    AthleticTheme(lightMode = true) {
        AnalyticsLogScreen(AnalyticsLogPreviewData.analyticsLogItems, mockInteractor)
    }
}