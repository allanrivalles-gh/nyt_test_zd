package com.theathletic.datetime

import android.content.Context
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.ui.Transformer
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class GmtStringToDatetimeTransformer @AutoKoin constructor(
    context: Context
) : Transformer<String?, Datetime> {

    companion object {
        private const val GMT_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private const val GMT_SHORT_FORMAT = "yyyy-MM-dd"
        private const val TIMEZONE_GMT = "GMT"
    }

    private val locale: Locale = context.resources.configuration.locale

    override fun transform(data: String?): Datetime {
        data?.let {
            try {
                val originalFormat = SimpleDateFormat(GMT_FORMAT, locale)
                originalFormat.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
                originalFormat.parse(it)?.let { parsedDate ->
                    return Datetime(parsedDate.time)
                }

                val shortFormat = SimpleDateFormat(GMT_SHORT_FORMAT, locale)
                shortFormat.timeZone = TimeZone.getTimeZone(TIMEZONE_GMT)
                shortFormat.parse(it)?.let { parsedDate ->
                    return Datetime(parsedDate.time)
                }
            } catch (ignored: Exception) {
                Timber.e("Invalid GMT String: $data")
            }
        }
        // This is probably the most easy and stable way we can handle the time error now.
        // Returning null would be probably better, but it looks like it's already too late to do so.
        return Datetime(0)
    }
}