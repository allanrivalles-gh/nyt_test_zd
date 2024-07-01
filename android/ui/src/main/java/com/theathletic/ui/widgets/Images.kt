package com.theathletic.ui.widgets

import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.theathletic.data.SizedImage
import com.theathletic.data.SizedImages
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.asAndroidColorInt
import com.theathletic.ui.utility.conditional

@Composable
fun ResourceIcon(
    @DrawableRes resourceId: Int,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
) {
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = null,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
    )
}

@Deprecated("Deprecated due to Coil update, use RemoteImageAsync instead")
@Composable
fun RemoteImage(
    url: String,
    modifier: Modifier = Modifier,
    circular: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    options: ImageRequest.Builder.() -> Unit = {},
    alignment: Alignment = Alignment.Center,
) {
    Image(
        painter = rememberImagePainter(
            data = url,
            builder = {
                crossfade(true)
                if (circular) {
                    transformations(CircleCropTransformation())
                }
                options()
            },
        ),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier,
        alignment = alignment
    )
}

@Composable
fun RemoteImageAsync(
    url: String?,
    modifier: Modifier = Modifier,
    circular: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null,
    @DrawableRes fallbackImage: Int? = null,
    @StringRes contentDescription: Int? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .transformations(if (circular) listOf(CircleCropTransformation()) else listOf())
            .build(),
        placeholder = placeholder?.let { painterResource(it) },
        error = error?.let { painterResource(it) },
        fallback = fallbackImage?.let { painterResource(it) },
        contentDescription = contentDescription?.let { stringResource(it) },
        contentScale = contentScale,
        modifier = modifier,
        alignment = alignment,
        colorFilter = colorFilter,
        onError = onError,
        onLoading = onLoading,
        onSuccess = onSuccess
    )
}

/**
 * The current version of Compose libraries (v1.0.1) does not have support for animated vector
 * drawables. We can use this [ImageView] wrapper to implement animated vectors in the meantime.
 */
@Composable
fun AnimatedDrawableImage(
    @DrawableRes resourceId: Int,
    modifier: Modifier = Modifier,
    animating: Boolean = true,
    tint: Color? = null,
) {
    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    tint?.let {
                        imageTintList = ColorStateList.valueOf(tint.asAndroidColorInt)
                    }
                    val drawable = AppCompatResources.getDrawable(context, resourceId)
                    setImageDrawable(drawable)
                }
            },
            update = { imageView ->
                if (animating) {
                    (imageView.drawable as? AnimatedVectorDrawable)?.start()
                    (imageView.drawable as? AnimatedVectorDrawableCompat)?.start()
                } else {
                    (imageView.drawable as? AnimatedVectorDrawable)?.stop()
                    (imageView.drawable as? AnimatedVectorDrawableCompat)?.stop()
                }
            }
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun TextAvatar(
    text: String,
    textColor: Color = AthTheme.colors.dark800,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(17.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(top = 1.3.dp),
            text = text,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall.copy(
                textAlign = TextAlign.Center,
                baselineShift = BaselineShift.None,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            color = textColor
        )
    }
}

@Composable
fun TeamLogo(
    teamUrls: SizedImages,
    preferredSize: Dp,
    modifier: Modifier = Modifier
) {
    val teamUrl = teamUrls.getPreferred(preferredSize)?.uri.orEmpty()
    RemoteImageAsync(
        url = teamUrl,
        modifier = modifier,
        error = R.drawable.ic_team_logo_placeholder,
    )
}

@Composable
fun VendorLogo(
    imageUrls: SizedImages,
    preferredSize: Dp,
    modifier: Modifier = Modifier
) {
    val imageUrl = imageUrls.getPreferred(preferredSize)?.uri.orEmpty()
    RemoteImageAsync(
        url = imageUrl,
        modifier = modifier
    )
}

/*
    This displays a headshot with the team color as it's background. If there is no
    headshot then the team's logo is displayed instead
 */
@Composable
fun Headshot(
    headshotsUrls: SizedImages,
    teamUrls: SizedImages,
    teamColor: Color,
    preferredSize: Dp,
    modifier: Modifier = Modifier
) {
    val teamUrl = teamUrls.getPreferred(preferredSize)?.uri
    val imageUrl = if (headshotsUrls.isNotEmpty()) {
        headshotsUrls.getPreferred(preferredSize)?.uri ?: teamUrl.orEmpty()
    } else {
        teamUrl.orEmpty()
    }
    Box(
        modifier = modifier
            .background(teamColor, shape = CircleShape)
            .conditional(headshotsUrls.isEmpty()) {
                padding(8.dp)
            }
    ) {
        RemoteImageAsync(
            url = imageUrl,
            modifier = modifier,
            circular = headshotsUrls.isNotEmpty(),
            error = R.drawable.ic_headshot_placeholder,
        )
    }
}

@Composable
fun SizedImages.getPreferred(preferredSize: Dp): SizedImage? {
    val preferredPxSize = with(LocalDensity.current) { preferredSize.toPx() }.toInt()
    return sortedBy { it.width }.find { it.width >= preferredPxSize } ?: lastOrNull()
}