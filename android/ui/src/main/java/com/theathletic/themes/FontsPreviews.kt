package com.theathletic.themes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xff000000,
    name = "Slab",
    group = "Dark",
    device = Devices.NEXUS_10
)
private fun Fonts_Slab() {
    DesignSystemFonts(slab)
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xff000000,
    name = "Tiempos Headline",
    group = "Dark",
    device = Devices.NEXUS_10
)

private fun Fonts_TiemposHeadline() {
    DesignSystemFonts(tiemposHeadline)
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xff000000,
    name = "Tiempos Text",
    group = "Dark",
    device = Devices.NEXUS_10
)
private fun Fonts_TiemposText() {
    DesignSystemFonts(tiemposText)
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xff000000,
    name = "Calibre",
    group = "Dark",
    device = Devices.NEXUS_10
)
private fun Fonts_Calibre() {
    DesignSystemFonts(calibre)
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xffffffff,
    name = "Slab",
    group = "Light",
    device = Devices.NEXUS_10
)
private fun Fonts_SlabLight() {
    AthleticTheme(lightMode = true) {
        DesignSystemFonts(slab)
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xffffffff,
    name = "Tiempos Headline",
    group = "Light",
    device = Devices.NEXUS_10
)
private fun Fonts_TiemposHeadlineLight() {
    AthleticTheme(lightMode = true) {
        DesignSystemFonts(tiemposHeadline)
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xffffffff,
    name = "Tiempos Text",
    group = "Light",
    device = Devices.NEXUS_10
)
private fun Fonts_TiemposTextLight() {
    AthleticTheme(lightMode = true) {
        DesignSystemFonts(tiemposText)
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xffffffff,
    name = "Calibre",
    group = "Light",
    device = Devices.NEXUS_10
)
private fun Fonts_CalibreLight() {
    AthleticTheme(lightMode = true) {
        DesignSystemFonts(calibre)
    }
}

private val slab = mapOf(
    AthTextStyle.Slab.Bold.Large to "Slab.Bold.Large",
    AthTextStyle.Slab.Bold.Medium to "Slab.Bold.Medium",
    AthTextStyle.Slab.Bold.Small to "Slab.Bold.Small",
)

private val tiemposHeadline = mapOf(
    AthTextStyle.TiemposHeadline.Regular.ExtraExtraLarge to "TiemposHeadline.Regular.ExtraExtraLarge",
    AthTextStyle.TiemposHeadline.Regular.ExtraLarge to "TiemposHeadline.Regular.ExtraLarge",
    AthTextStyle.TiemposHeadline.Regular.Large to "TiemposHeadline.Regular.Large",
    AthTextStyle.TiemposHeadline.Regular.Medium to "TiemposHeadline.Regular.Medium",
    AthTextStyle.TiemposHeadline.Regular.Small to "TiemposHeadline.Regular.Small",
    AthTextStyle.TiemposHeadline.Regular.ExtraSmall to "TiemposHeadline.Regular.ExtraSmall",
    AthTextStyle.TiemposHeadline.Regular.ExtraExtraSmall to "TiemposHeadline.Regular.ExtraExtraSmall",
)

private val tiemposText = mapOf(
    AthTextStyle.TiemposBody.Regular.Large to "TiemposBody.Regular.Large",
    AthTextStyle.TiemposBody.Medium.Large to "TiemposBody.Medium.Large",
    AthTextStyle.TiemposBody.Regular.Medium to "TiemposBody.Regular.Medium",
    AthTextStyle.TiemposBody.Medium.Medium to "TiemposBody.Medium.Medium",
    AthTextStyle.TiemposBody.Regular.Small to "TiemposBody.Regular.Small",
    AthTextStyle.TiemposBody.Medium.Small to "TiemposBody.Medium.Small",
    AthTextStyle.TiemposBody.Regular.ExtraSmall to "TiemposBody.Regular.ExtraSmall",
    AthTextStyle.TiemposBody.Medium.ExtraSmall to "TiemposBody.Medium.ExtraSmall"
)

private val calibre = mapOf(
    AthTextStyle.Calibre.Headline.Regular.ExtraLarge to "Calibre.Headline.Regular.ExtraLarge",
    AthTextStyle.Calibre.Headline.SemiBold.Large to "Calibre.Headline.SemiBold.Large",
    AthTextStyle.Calibre.Headline.SemiBold.Medium to "Calibre.Headline.SemiBold.Medium",
    AthTextStyle.Calibre.Headline.SemiBold.Small to "Calibre.Headline.SemiBold.Small",
    AthTextStyle.Calibre.Headline.Medium.Small to "Calibre.Headline.Medium.Small",
    AthTextStyle.Calibre.Utility.Medium.ExtraLarge to "Calibre.Utility.Medium.ExtraLarge",
    AthTextStyle.Calibre.Utility.Regular.ExtraLarge to "Calibre.Utility.Regular.ExtraLarge",
    AthTextStyle.Calibre.Utility.Medium.Large to "Calibre.Utility.Medium.Large",
    AthTextStyle.Calibre.Utility.Regular.Large to "Calibre.Utility.Regular.Large",
    AthTextStyle.Calibre.Tag.Medium.Large to "Calibre.Tag.Medium.Large",
    AthTextStyle.Calibre.Utility.Regular.Small to "Calibre.Utility.Regular.Small",
    AthTextStyle.Calibre.Utility.Medium.Small to "Calibre.Utility.Medium.Small",
    AthTextStyle.Calibre.Utility.Regular.ExtraSmall to "Calibre.Utility.Regular.ExtraSmall",
    AthTextStyle.Calibre.Utility.Medium.ExtraSmall to "Calibre.Utility.Medium.ExtraSmall",
    AthTextStyle.Sohne.Data to "Sohne.Data"
)

@Composable
private fun DesignSystemFonts(fontMap: Map<TextStyle, String>) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        fontMap.forEach { (style, text) ->
            Text(
                text = text,
                color = AthTheme.colors.dark800,
                style = style,
            )
        }
    }
}