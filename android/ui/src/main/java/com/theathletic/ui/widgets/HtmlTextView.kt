package com.theathletic.ui.widgets

import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.R
import com.theathletic.ui.markdown.ClickableUrlSpan
import com.theathletic.ui.markdown.MarkdownRenderer

@Composable
fun HtmlTextView(
    htmlText: String,
    contentTextSize: ContentTextSize,
    onUrlClicked: (String) -> Unit
) {
    val localContext = LocalContext.current
    val spanned = remember(htmlText) {
        MarkdownRenderer.fromMarkdown(
            context = localContext,
            markdown = htmlText.replace(Regex("\n"), "<br>"),
            textSize = ContentTextSize.DEFAULT,
            urlOnClickListener = object : ClickableUrlSpan.OnClickListener {
                override fun onUrlClick(url: String) {
                    onUrlClicked(url)
                }
            }
        ).trim()
    }
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                setTextColor(ContextCompat.getColor(context, R.color.ath_grey_10))
                linksClickable = true
                movementMethod = LinkMovementMethod.getInstance()
                typeface = ResourcesCompat.getFont(context, R.font.calibre_regular)
                letterSpacing = 0.015f
                setLineSpacing(0f, 1.22f)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.news_default_text_size))
                setLinkTextColor(ContextCompat.getColor(context, R.color.ath_grey_10))
            }
        },
        update = {
            it.text = spanned
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.context.resources.getDimension(contentTextSize.toDimenRes()))
        }
    )
}

private fun ContentTextSize.toDimenRes() = when (this) {
    ContentTextSize.MEDIUM -> R.dimen.news_medium_text_size
    ContentTextSize.LARGE -> R.dimen.news_large_text_size
    ContentTextSize.EXTRA_LARGE -> R.dimen.news_extra_large_text_size
    else -> R.dimen.news_default_text_size
}