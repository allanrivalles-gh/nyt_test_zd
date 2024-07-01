package com.theathletic.ui.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.theathletic.ui.ContentTextSize
import com.theathletic.ui.markdown.MarkdownRenderer

@BindingAdapter("htmlText", "htmlTextSize", requireAll = false)
fun htmlText(view: TextView, text: CharSequence?, textSize: ContentTextSize?) {
    if (text == null) {
        view.text = null
        return
    }

    val converted = text.replace(Regex("\n"), "<br>")
    val safeTextSize = textSize ?: ContentTextSize.DEFAULT
    view.text = MarkdownRenderer.fromMarkdown(view.context, converted, safeTextSize).trim()
}