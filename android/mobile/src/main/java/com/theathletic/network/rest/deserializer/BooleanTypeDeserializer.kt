package com.theathletic.network.rest.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/**
 * Deserialize ints into boolean (1 -> true | 0 -> false)
 */
class BooleanTypeDeserializer : JsonDeserializer<Boolean> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Boolean? {
        return try {
            when (json.asInt) {
                0 -> false
                1 -> true
                else -> null
            }
        } catch (e: NumberFormatException) {
            when (json.asString) {
                "0" -> false
                "1" -> true
                "false" -> false
                "true" -> true
                else -> null
            }
        }
    }
}