package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PodcastTopicEntryTypeDeserializer : JsonDeserializer<PodcastTopicEntryType>, KoinComponent {
    private val crashLogHandler by inject<ICrashLogHandler>()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PodcastTopicEntryType? {
        return try {
            PodcastTopicEntryType.from(json.asString)
        } catch (e: NumberFormatException) {
            crashLogHandler.trackException(e, log = "[PodcastTopicEntryTypeDeserializer] UnParsable: $json")
            PodcastTopicEntryType.UNKNOWN
        }
    }
}