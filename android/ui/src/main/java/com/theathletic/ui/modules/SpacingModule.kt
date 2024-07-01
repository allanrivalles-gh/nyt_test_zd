package com.theathletic.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme

data class SpacingModuleV2(
    val id: String,
    val color: Background,
    val height: Height
) : FeedModuleV2 {

    override val moduleId = "SpacingModuleV2:$id"

    enum class Height {
        Small,
        Medium,
        Large,
        ExtraLarge
    }

    enum class Background {
        Transparent,
        StandardForegroundColor
    }

    @Composable
    private fun Height.toDps() =
        when (this) {
            Height.Small -> 8.dp
            Height.Medium -> 16.dp
            Height.Large -> 24.dp
            Height.ExtraLarge -> 40.dp
        }

    @Composable
    private fun Background.toColor() =
        when (this) {
            Background.StandardForegroundColor -> AthTheme.colors.dark200
            Background.Transparent -> Color.Transparent
        }

    @Composable
    override fun Render() {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.toColor())
                .height(height.toDps())
        )
    }
}

@Deprecated("Use SpacingModuleV2")
data class SpacingModule(
    val id: String,
    val color: Background,
    val height: Height
) : FeedModule {

    enum class Height {
        Small,
        Medium,
        Large,
        ExtraLarge
    }

    enum class Background {
        Transparent,
        StandardForegroundColor
    }

    @Composable
    private fun Height.toDps() =
        when (this) {
            Height.Small -> 8.dp
            Height.Medium -> 16.dp
            Height.Large -> 24.dp
            Height.ExtraLarge -> 40.dp
        }

    @Composable
    private fun Background.toColor() =
        when (this) {
            Background.StandardForegroundColor -> AthTheme.colors.dark200
            Background.Transparent -> Color.Transparent
        }

    @Composable
    override fun Render() {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.toColor())
                .height(height.toDps())
        )
    }
}

@Preview
@Composable
fun SpacingModulePreview() {
    SpacingModule(
        id = "uniqueId",
        color = SpacingModule.Background.StandardForegroundColor,
        height = SpacingModule.Height.ExtraLarge
    )
}

@Preview
@Composable
fun SpacingModulePreview_Light() {
    AthleticTheme(lightMode = true) {
        SpacingModule(
            id = "uniqueId",
            color = SpacingModule.Background.StandardForegroundColor,
            height = SpacingModule.Height.Small
        )
    }
}