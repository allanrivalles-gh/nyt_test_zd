package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("PrivatePropertyName")
class DateDeserializer : JsonDeserializer<Date>, KoinComponent {
    private val DATE_FORMATS = arrayOf("yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy", "yyyy-MM-dd")
    private val crashLogHandler by inject<ICrashLogHandler>()

    override fun deserialize(
        jsonElement: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        val exceptionMsg = "[DateDeserializer] UnParsable date: $jsonElement. Supported formats: ${DATE_FORMATS.contentToString()}"
        for (format in DATE_FORMATS) {
            try {
                SimpleDateFormat(format, Locale.US).parse(jsonElement.asString)?.let {
                    return it
                }
            } catch (e: ParseException) {
                crashLogHandler.trackException(e, log = exceptionMsg)
            }
        }

        try {
            return Date((jsonElement.asString + "000").toLong()) // Try to parse date that is in seconds
        } catch (e: Exception) {
            crashLogHandler.trackException(e, log = exceptionMsg)
        }

        throw JsonParseException(exceptionMsg)
    }
}