package com.theathletic.network.rest.deserializer

import androidx.databinding.ObservableBoolean
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class ObservableBooleanTypeDeserializer : JsonDeserializer<ObservableBoolean> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ObservableBoolean {
        return try {
            when {
                json.isJsonNull || json.isJsonObject -> ObservableBoolean(false)
                else -> ObservableBoolean(json.asBoolean)
            }
        } catch (e: NumberFormatException) {
            when (json.asString) {
                "0" -> ObservableBoolean(false)
                "1" -> ObservableBoolean(true)
                "false" -> ObservableBoolean(false)
                "true" -> ObservableBoolean(true)
                else -> ObservableBoolean(false)
            }
        }
    }
}