package com.theathletic.extension

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun JsonElement.asStringOrNull(): String? {
    return if (isJsonNull)
        null
    else
        asString
}

fun JsonElement.asJsonPrimitiveOrNull(): JsonPrimitive? {
    return if (isJsonPrimitive)
        asJsonPrimitive
    else
        null
}

fun JsonElement.asJsonObjectOrNull(): JsonObject? {
    return if (isJsonNull)
        null
    else
        asJsonObject
}