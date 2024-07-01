package com.theathletic.podcast.ui.widget

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use
import com.theathletic.R

class TinyPodcastPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    data class ViewState(
        val formattedDuration: String?,
        @DrawableRes val controlsDrawable: Int,
        @ColorRes val playButtonTint: Int = R.color.ath_grey_10
    )

    private val icon by lazy { findViewById<ImageView>(R.id.tiny_podcast_icon) }
    private val durationText by lazy { findViewById<TextView>(R.id.tiny_podcast_text) }

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_tiny_podcast_player, this)
        setBackgroundResource(R.drawable.frontpage_carousel_podcast_controls_background)

        context.theme.obtainStyledAttributes(attrs, R.styleable.TinyPodcastPlayer, 0, 0).use {
            val iconSize = it.getDimensionPixelSize(
                R.styleable.TinyPodcastPlayer_iconSize,
                resources.getDimensionPixelSize(R.dimen.podcast_tiny_player_icon_size)
            )
            val textSize = it.getDimension(
                R.styleable.TinyPodcastPlayer_durationTextSize,
                resources.getDimension(R.dimen.podcast_tiny_player_text_size)
            )
            val showDuration = it.getBoolean(
                R.styleable.TinyPodcastPlayer_showDuration,
                true
            )

            icon.layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
            durationText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            durationText.visibility = if (showDuration) VISIBLE else GONE
        }
    }

    fun setViewState(viewState: ViewState?) {
        if (viewState == null) return
        val drawable = AppCompatResources.getDrawable(context, viewState.controlsDrawable)
        val playButtonTint = resources.getColor(viewState.playButtonTint, null)

        icon.setImageDrawable(drawable)
        icon.drawable.setTint(playButtonTint)
        durationText.setTextColor(playButtonTint)
        background.setTint(playButtonTint)
        if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        }

        durationText.text = viewState.formattedDuration
    }
}