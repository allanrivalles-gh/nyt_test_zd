package com.theathletic.network.rest.deserializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.theathletic.entity.user.UserEntity
import com.theathletic.extension.extLogError
import com.theathletic.utility.logging.ICrashLogHandler
import java.lang.reflect.Type
import java.text.ParseException
import java.util.Date
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserEntityDeserializer : JsonDeserializer<UserEntity>, KoinComponent {
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
    ): UserEntity {
        val exceptionMsg = "[UserEntityDeserializer] UnParsable: $jsonElement."
        var exceptionExtraMsg: String? = null
        try {
            // TT Parse shallow copy of UserEntity
            val entity = gson.fromJson(jsonElement, UserEntity::class.java)

            // TT Fill ambassador entry tags

            if (jsonElement.asJsonObject.has("ambassador_tags")) {
                val ambassadorTags = jsonElement.asJsonObject.getAsJsonObject("ambassador_tags").asJsonObject
                ambassadorTags.getAsJsonArray("team_ids")?.forEach {
                    exceptionExtraMsg = "Team Ids: $it"
                    if (it.isJsonPrimitive)
                        entity.ambassadorTeamIds.add(it.asJsonPrimitive.asLong)
                }
                ambassadorTags.getAsJsonArray("city_ids")?.forEach {
                    exceptionExtraMsg = "City Ids: $it"
                    if (it.isJsonPrimitive)
                        entity.ambassadorCityIds.add(it.asJsonPrimitive.asLong)
                }
                ambassadorTags.getAsJsonArray("league_ids")?.forEach {
                    exceptionExtraMsg = "League Ids: $it"
                    if (it.isJsonPrimitive)
                        entity.ambassadorLeagueIds.add(it.asJsonPrimitive.asLong)
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