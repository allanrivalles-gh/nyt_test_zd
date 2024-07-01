package com.theathletic.ui.widgets

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.ColorScheme
import com.theathletic.ui.R

@Composable
private fun PulseAnimation(
    layoutSize: Dp,
    circleSize: Dp,
    circleColor: Color,
    strokeWidth: Dp = 5.dp,
    durationAddition: Int = 0,
    startDelayMillis: Int = 0,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val opacityAnimation by infiniteTransition.animateFloat(
        0f,
        0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                0f at 0
                0.25f at 1000 with LinearEasing
                0.12f at 2000 with LinearOutSlowInEasing
                0f at 2600 with FastOutLinearInEasing
                durationMillis = 3000 + durationAddition
                delayMillis = startDelayMillis
            }
        )
    )

    val radiusScale by infiniteTransition.animateValue(
        initialValue = 0.05f,
        targetValue = 1.0f,
        Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                0f at 0
                0.2f at 500
                0.4f at 1000
                0.6f at 1500
                0.8f at 2000
                1f at 2500
                durationMillis = 3000 + durationAddition
                delayMillis = startDelayMillis
            }
        )
    )
    Canvas(
        modifier = Modifier
            .size(layoutSize)
            .graphicsLayer {
                clip = true
                shape = CircleShape
                scaleX = 1.0f
                scaleY = 1.0f
            }
    ) {
        val radiusSize = circleSize.toPx() * radiusScale
        val strokeSize = strokeWidth.toPx()

        drawCircle(
            color = circleColor,
            alpha = opacityAnimation,
            radius = radiusSize,
            style = Stroke(width = strokeSize, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PulsingIcon(
    modifier: Modifier = Modifier,
    layoutSize: Dp,
    iconId: Int,
    iconSize: Dp,
    colorScheme: ColorScheme? = null,
    animationColor: Color = Color.Transparent,
    color: Color? = null,
    circleSize: Dp,
    strokeWidth: Dp,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize(),
    ) {
        ResourceIcon(
            resourceId = iconId,
            tint = colorScheme?.getColor() ?: color,
            modifier = Modifier
                .size(iconSize)
                .padding(top = (iconSize / 6))
        )
        PulseAnimation(
            layoutSize = layoutSize,
            circleSize = circleSize,
            circleColor = colorScheme?.getColor() ?: animationColor,
            strokeWidth = strokeWidth,
            startDelayMillis = 750
        )
        PulseAnimation(
            layoutSize = layoutSize,
            circleSize = circleSize,
            circleColor = colorScheme?.getColor() ?: animationColor,
            strokeWidth = strokeWidth,
            durationAddition = 750
        )
    }
}

@Preview
@Composable
fun PulsingIcon_LightPreview() {
    AthleticTheme(lightMode = true) {
        PulsingIcon(
            layoutSize = 100.dp,
            iconId = R.drawable.ic_news_comment,
            iconSize = 24.dp,
            colorScheme = ColorScheme(AthTheme.colors.dark300),
            circleSize = 40.dp,
            strokeWidth = 8.dp,
        )
    }
}

@Preview
@Composable
fun PulsingIcon_DarkPreview() {
    AthleticTheme(lightMode = false) {
        PulsingIcon(
            layoutSize = 100.dp,
            iconId = R.drawable.ic_news_comment,
            iconSize = 24.dp,
            colorScheme = ColorScheme(AthTheme.colors.dark800),
            circleSize = 40.dp,
            strokeWidth = 5.dp,
        )
    }
}