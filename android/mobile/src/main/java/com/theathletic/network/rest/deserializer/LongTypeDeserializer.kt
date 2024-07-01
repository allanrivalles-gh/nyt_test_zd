package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.theathletic.extension.extLogError
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Deserialize ints into long. If parsing fails, try parse the value as string.
 */
class LongTypeDeserializer : JsonDeserializer<Long>, KoinComponent {
    private val crashLogHandler by inject<ICrashLogHandler>()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Long? {
        return try {
            val number = json.asLong
            number
        } catch (e: Exception) {
            crashLogHandler.trackException(e, log = "[LongTypeDeserializer] UnParsable: $json.")
            e.extLogError()
            val num = json.asString.toLongOrNull() ?: 0L
            num
        }
    }
}