package com.theathletic.remoteconfig.local

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun RemoteConfigDataSource.getStringList(entry: RemoteConfigEntry): Flow<List<String>> {
    val moshi = Moshi.Builder().build()
    val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter(
        Types.newParameterizedType(
            List::class.java,
            String::class.java
        )
    )
    return getString(entry).map { jsonAdapter.maybeFromJson(it) ?: emptyList() }
}

private fun <T> JsonAdapter<T>.maybeFromJson(json: String) = try {
    fromJson(json)
} catch (exception: JsonDataException) {
    null
}