package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.theathletic.entity.main.FeedItemEntryType
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Deprecated("Should be removed in favor of FeedV2")
class FeedEntryTypeDeserializer : JsonDeserializer<FeedItemEntryType>, KoinComponent {
    private val crashLogHandler by inject<ICrashLogHandler>()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FeedItemEntryType? {
        return try {
            FeedItemEntryType.from(json.asString)
        } catch (e: NumberFormatException) {
            crashLogHandler.trackException(e, log = "[FeedEntryTypeDeserializer] UnParsable: $json")
            FeedItemEntryType.UNKNOWN
        }
    }
}

fun feedItemEntryTypeSerializer() = JsonSerializer<FeedItemEntryType> { src, _, _ ->
    JsonPrimitive(src.value)
}