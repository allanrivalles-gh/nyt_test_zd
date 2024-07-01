package com.theathletic.notifications

import android.os.Bundle

interface NotificationPayloadCompanion<T> {
    val payloadKey: String
    fun maybeFromJson(json: String): T?
}

fun <T> NotificationPayloadCompanion<T>.fromBundle(bundle: Bundle): T? {
    val json = bundle.getString(payloadKey) ?: return null
    return maybeFromJson(json)
}

fun <T> NotificationPayloadCompanion<T>.fromMap(map: Map<String, String>): T? {
    val json = map[payloadKey] ?: return null
    return maybeFromJson(json)
}