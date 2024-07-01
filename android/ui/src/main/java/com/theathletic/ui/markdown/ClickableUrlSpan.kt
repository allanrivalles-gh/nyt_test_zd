package com.theathletic.ui.markdown

import android.text.style.URLSpan
import android.view.View

class ClickableUrlSpan(
    url: String,
    private val clickListener: OnClickListener? = null
) : URLSpan(url) {

    override fun onClick(widget: View) {
        clickListener?.onUrlClick(url) ?: super.onClick(widget)
    }

    interface OnClickListener {
        fun onUrlClick(url: String)
    }
}