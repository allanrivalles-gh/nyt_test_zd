package com.theathletic.boxscore.ui.formatters

import android.icu.text.MessageFormat
import android.os.Build
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.formatter.Formatter
import java.util.Locale

class OrdinalFormatter @AutoKoin constructor() : Formatter<Int> {

    override fun format(formattable: Int): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val formatter = MessageFormat("{0,ordinal}", Locale.getDefault())
            formatter.format(arrayOf(formattable))
        } else {
            return formattable.toString()
        }
    }
}