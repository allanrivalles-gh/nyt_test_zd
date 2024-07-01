package com.theathletic.notifications

import com.google.gson.Gson
import com.google.gson.JsonParseException

fun <T> Gson.maybeFromJson(json: String, classOfT: Class<T>): T? = try {
    fromJson(json, classOfT)
} catch (exception: JsonParseException) {
    null
}