package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.theathletic.entity.main.FeedItemStyle
import com.theathletic.utility.safeValueOf

fun feedItemStyleDeserializer() = JsonDeserializer { json, _, _ ->
    safeValueOf(json.asString) ?: FeedItemStyle.UNKNOWN
}

fun feedItemStyleSerializer() = JsonSerializer<FeedItemStyle> { src, _, _ -> JsonPrimitive(src.name) }