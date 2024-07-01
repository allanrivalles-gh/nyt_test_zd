package com.theathletic.ads.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.theathletic.ads.ui.theme.AdColors
import com.theathletic.themes.AthFont
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.preview.DayNightPreview

sealed interface AdState {
    object Placeholder : AdState
    data class Visible(val view: View) : AdState
    object Collapsed : AdState
}

@Composable
fun AdWrapperUi(state: AdState, lightMode: Boolean) {
    val ad = when (state) {
        AdState.Collapsed -> return
        AdState.Placeholder -> null
        is AdState.Visible -> state.view
    }

    val colors = AdColors(lightMode)
    Box(
        modifier = Modifier
            .background(AthTheme.colors.dark200)
            // workaround to make sure impressions for ads are triggered
            .onGloballyPositioned { ad?.let { it.rootView.requestLayout() } }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .background(colors.adBackground)
                .fillMaxWidth()
        ) {
            Divider(
                color = colors.adDivider,
                thickness = 1.dp
            )
            Text(
                text = stringResource(id = R.string.advertisement_slug).uppercase(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = AthFont.Calibre,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = colors.adSlugText,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
            )
            if (ad != null) {
                AndroidView(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    factory = {
                        // the ad view may have a parent already
                        (ad.parent as? ViewGroup)?.removeAllViews()
                        ad
                    },
                    update = { it.setColorThemeForAd(lightMode) }
                )
            } else {
                Box(modifier = Modifier.height(250.dp))
            }
            Divider(
                color = colors.adDivider,
                thickness = 1.dp
            )
        }
    }
}

private typealias AdStateBuilder = (context: Context) -> AdState

@DayNightPreview
@Composable
private fun AdWrapperUiPreview(@PreviewParameter(AdWrapperParamProvider::class) adStateBuilder: AdStateBuilder) {
    val lightMode = isSystemInDarkTheme().not()
    AthleticTheme(lightMode = lightMode) {
        AdWrapperUi(
            state = adStateBuilder.invoke(LocalContext.current),
            lightMode = lightMode,
        )
    }
}

private class AdWrapperParamProvider : PreviewParameterProvider<AdStateBuilder> {
    override val values = sequenceOf<AdStateBuilder>(
        { AdState.Placeholder },
        { context -> AdState.Visible(AdForPreview(context)) },
        { AdState.Collapsed },
    )
}

@SuppressLint("SetTextI18n")
private class AdForPreview(context: Context) : LinearLayout(context) {
    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )
        setBackgroundColor(context.getColor(R.color.ath_green))
        gravity = Gravity.CENTER

        val textView = TextView(context)
        textView.text = "Ad for Preview"
        textView.setTextColor(context.getColor(R.color.ath_grey_10))
        textView.textSize = 30f
        addView(textView)
    }
}