package com.theathletic.widget

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextUtils
import android.text.util.Linkify
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.links.deep.DeeplinkEventProducer
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Basic idea is to use ellipsize at the beginning to analyze if the content fits or not and
 * should be collapsible. This is why we have to set the ellipsize in the constructor.
 *
 * We also need to keep track of the [originalMaxLines] as we are switching between the original
 * set in the layout or programmatically and the max Int values. Now you may ask why we are
 * setting the [originalMaxLines] in the constructor again. In fact, the styles are handled in the
 * extended [TextView] class constructor, and gets overwritten when we create our local variable.
 *
 * Sadly we are not able to support ellipsize at the end as it is not
 * working when there is any [AutoLinkMethod].
 */
class LinkableCollapsibleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), KoinComponent {
    private var originalMaxLines = Integer.MAX_VALUE
    private var setupOnce = true
    private var isCollapsible = false
    private var isCollapsed = false
    private var withGradient = true

    private val deeplinkEventProducer by inject<DeeplinkEventProducer>()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinkableCollapsibleTextView)

        try {
            withGradient = typedArray.getBoolean(R.styleable.LinkableCollapsibleTextView_withGradient, true)
            originalMaxLines = typedArray.getInt(R.styleable.LinkableCollapsibleTextView_collapsedLineCount, Integer.MAX_VALUE)
            super.setMaxLines(originalMaxLines)
        } finally {
            typedArray.recycle()
        }

        ellipsize = TextUtils.TruncateAt.END
        setupListeners()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        ellipsize = TextUtils.TruncateAt.END
        if (this.text != text)
            setupOnce = true
        super.setText(text, type)
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        if (maxLines > 0 && maxLines < Integer.MAX_VALUE)
            originalMaxLines = maxLines
    }

    fun isCollapsed() = isCollapsed

    fun isCollapsible() = isCollapsible

    private fun setupLinkMovementMethod() {
        BetterLinkMovementMethod
            .linkify(Linkify.WEB_URLS, this)
            .setOnLinkClickListener { _, url ->
                if (url.contains(AthleticConfig.BASE_URL_US)) {
                    deeplinkEventProducer.tryEmit(url)
                } else {
                    false
                }
            }
    }

    private fun setupListeners() {
        setOnClickListener {
            maxLines = if (maxLines > 0 && maxLines < Integer.MAX_VALUE) {
                Integer.MAX_VALUE
            } else {
                originalMaxLines
            }

            post {
                if (isCollapsible) {
                    if (maxLines == Integer.MAX_VALUE) {
                        isCollapsed = false
                        removeGradient()
                    } else {
                        isCollapsed = true
                        applyGradient()
                    }
                }
            }
        }

        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (width <= 0)
                return@addOnLayoutChangeListener

            if (setupOnce) {
                setupOnce = false

                var hasLongContent = false
                layout?.let { layout ->
                    if (layout.lineCount > 0) {
                        hasLongContent = layout.lineCount > maxLines || layout.getEllipsisCount(layout.lineCount - 1) > 0
                    }
                }
                isCollapsible = hasLongContent

                if (isCollapsible) {
                    isCollapsed = true
                    applyGradient()
                }

                setupLinkMovementMethod()
            }
        }
    }

    private fun applyGradient() {
        if (!withGradient)
            return

        paint.shader = LinearGradient(
            0f, paddingTop.toFloat(), 0f, height.toFloat() - paddingBottom - paddingTop,
            intArrayOf(currentTextColor, Color.TRANSPARENT),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )
        invalidate()
    }

    private fun removeGradient() {
        if (!withGradient)
            return

        paint.shader = null
        invalidate()
    }
}