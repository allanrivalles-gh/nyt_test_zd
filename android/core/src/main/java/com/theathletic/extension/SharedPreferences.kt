package com.theathletic.extension

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Date

@Suppress("UNCHECKED_CAST")
inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
    return when (T::class) {
        Date::class -> Date(getLong(key, defaultValue as? Long ?: -1)) as T?
        String::class -> getString(key, defaultValue as? String) as T?
        Set::class -> getStringSet(key, defaultValue as? Set<String>) as T?
        HashSet::class -> getStringSet(key, defaultValue as? HashSet<String>) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1L) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1F) as T?
        else -> throw UnsupportedOperationException("Unsupported preference type")
    }
}

@Suppress("UNCHECKED_CAST")
operator fun SharedPreferences.set(key: String, value: Any?) {
    edit {
        when (value) {
            is Date -> putLong(key, value.time)
            is String? -> putString(key, value)
            is Set<*> -> putStringSet(key, value as Set<String>)
            is HashSet<*> -> putStringSet(key, value as HashSet<String>)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> throw UnsupportedOperationException("Unsupported preference type")
        }
    }
}