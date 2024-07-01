package com.theathletic.network.rest.deserializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.extension.extLogError
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import java.text.ParseException
import java.util.Date
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserTopicsDeserializer : JsonDeserializer<UserTopics>, KoinComponent {
    private val crashLogHandler by inject<ICrashLogHandler>()

    private val gson: Gson = GsonBuilder().apply {
        registerTypeAdapter(Date::class.java, DateDeserializer())
        registerTypeAdapter(Boolean::class.java, BooleanTypeDeserializer())
        registerTypeAdapter(Long::class.java, LongTypeDeserializer())
        setLenient()
    }.create()

    override fun deserialize(
        jsonElement: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UserTopics {
        val exceptionMsg = "[UserTopicsDeserializer] UnParsable: $jsonElement."
        var exceptionExtraMsg: String? = null
        try {
            // TT Parse shallow copy of UserTopics
            val entity = gson.fromJson(jsonElement, UserTopics::class.java)
            entity.teams.forEach { it.isFollowed = true }
            entity.leagues.forEach { it.isFollowed = true }
            entity.authors.forEach { it.isFollowed = true }

            // TT Fill other league and other cities
            if (jsonElement.asJsonObject.has("other_leagues")) {
                jsonElement.asJsonObject.get("other_leagues").asJsonArray.forEach {
                    exceptionExtraMsg = "Leagues: $it"
                    if (it.isJsonObject) {
                        entity.leagues.add(gson.fromJson(it, UserTopicsItemLeague::class.java))
                    }
                }
            }

            return entity
        } catch (e: ParseException) {
            crashLogHandler.trackException(e, message = exceptionExtraMsg, log = exceptionMsg)
            e.extLogError()
        }
        throw JsonParseException(exceptionMsg)
    }
}