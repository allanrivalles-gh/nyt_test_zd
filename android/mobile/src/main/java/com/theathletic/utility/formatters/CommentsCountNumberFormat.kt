package com.theathletic.utility.formatters

import com.theathletic.R
import com.theathletic.extension.extGetString

object CommentsCountNumberFormat {
    fun format(number: Int?): String {
        return format(number?.toLong())
    }

    fun format(number: Long?): String {
        return when {
            number == null -> ""
            number >= 1000 -> R.string.global_count_thousand.extGetString(number / 1000)
            else -> number.toString()
        }
    }
}