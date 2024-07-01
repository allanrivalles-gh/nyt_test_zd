package com.theathletic.network.rest.deserializer

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.theathletic.entity.main.PodcastTopicEntryType
import java.lang.reflect.Type

class PodcastTopicEntryTypeSerializer : JsonSerializer<PodcastTopicEntryType> {
    override fun serialize(
        src: PodcastTopicEntryType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.value ?: "")
    }
}