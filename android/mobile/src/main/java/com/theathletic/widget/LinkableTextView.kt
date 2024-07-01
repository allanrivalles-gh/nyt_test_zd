package com.theathletic.widget

import android.content.Context
import android.text.util.Linkify
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.analytics.data.ClickSource
import com.theathletic.utility.ActivityUtility
import java.util.regex.Pattern
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class LinkableTextView : AppCompatTextView {
    companion object {
        fun handleLinks(textView: TextView) {
            BetterLinkMovementMethod
                .linkify(Linkify.WEB_URLS, textView)
                .setOnLinkClickListener { _, url ->
                    var result = false
                    if (url.contains(AthleticConfig.BASE_URL_US)) {
                        val pattern = Pattern.compile("([0-9])\\d+")
                        val matcher = pattern.matcher(url.toString())
                        if (matcher.find()) {
                            matcher.group(0)?.let { id ->
                                ActivityUtility.startArticleActivity(AthleticApplication.getContext(), id.toLong(), ClickSource.UNKNOWN)
                                result = true
                            }
                        }
                    }
                    result
                }
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        handleLinks(this)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        handleLinks(this)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        handleLinks(this)
    }
}