package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.theathletic.entity.main.FeedItemType
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeedItemTypeDeserializer : JsonDeserializer<FeedItemType>, KoinComponent {
    private val crashLogHandler by inject<ICrashLogHandler>()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): FeedItemType? {
        return try {
            FeedItemType.from(json.asString)
        } catch (e: NumberFormatException) {
            crashLogHandler.trackException(e, log = "[FeedItemTypeDeserializerV2] UnParsable: $json")
            FeedItemType.UNKNOWN
        }
    }
}

fun feedItemTypeSerializer() = JsonSerializer<FeedItemType> { src, _, _ -> JsonPrimitive(src.value) }