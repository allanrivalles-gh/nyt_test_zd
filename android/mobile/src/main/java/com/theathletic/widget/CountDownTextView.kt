package com.theathletic.widget

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountDownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    var countDownTimer: CountDownTimer? = null
}