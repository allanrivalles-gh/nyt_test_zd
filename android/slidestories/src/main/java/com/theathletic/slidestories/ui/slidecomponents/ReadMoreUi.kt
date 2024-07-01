package com.theathletic.slidestories.ui.slidecomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.slidestories.R
import com.theathletic.themes.AthColor.Companion.Gray300
import com.theathletic.themes.AthColor.Companion.Gray700
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.preview.DevicePreviewSmallAndLarge
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun ReadMore(
    title: String,
    description: String,
    imageUrl: String,
    permalink: String,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick(permalink) }
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clip(RoundedCornerShape(4.dp))
            .background(color = Gray300)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RemoteImageAsync(
            url = imageUrl,
            contentScale = ContentScale.FillBounds,
            placeholder = R.drawable.ic_athletic_a_logo,
            modifier = Modifier.size(44.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.6f)
                .padding(
                    start = 12.dp,
                    end = 8.dp
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Gray700,
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Gray700,
            )
        }

        ResourceIcon(
            resourceId = R.drawable.ic_arrow_right,
            tint = AthTheme.colors.dark700
        )
    }
}

@DayNightPreview
@Composable
fun ReadMore_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        ReadMore(
            "Go Deeper",
            "Wilfried Zaha and Manchester United: What really happened",
            "",
            ""
        ) {}
    }
}

@DayNightPreview
@DevicePreviewSmallAndLarge
@Composable
fun ReadMore_Long_Preview() {
    AthleticTheme(lightMode = isSystemInDarkTheme()) {
        ReadMore(
            "Go Deeper with a very long and lengthy title for the go deeper",
            """Wilfried Zaha and Manchester United: What really happened Wilfried Zaha and Manchester United: What really happened Wilfried Zaha and Manchester United: What really happened""",
            "",
            ""
        ) {}
    }
}