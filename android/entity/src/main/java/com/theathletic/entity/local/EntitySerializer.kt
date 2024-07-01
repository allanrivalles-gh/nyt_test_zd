package com.theathletic.entity.local

import com.squareup.moshi.Moshi
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.datetime.TimeProvider
import com.theathletic.entity.BuildConfig
import timber.log.Timber
import java.lang.Exception

/**
 * Responsible for serializing [AthleticEntity] to [SerializedEntity] and back. In short, this
 * converts an entity to key-value storage with an id, type, and json blob in the [serialize]
 * function. Using those things, we can rebuild the in-memory model using [deserialize].
 */

class EntitySerializer @AutoKoin(Scope.SINGLE) constructor(
    private val moshi: Moshi,
    private val timeProvider: TimeProvider
) {

    fun deserialize(data: SerializedEntity): AthleticEntity? {
        val typeToken = data.id.type.typeToEntity?.java ?: return null

        return try {
            moshi.adapter(typeToken).fromJson(data.jsonBlob)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
            Timber.e(e, "Failed to deserialize: ${data.type}-${data.id.id}")
            null
        }
    }

    fun serialize(data: AthleticEntity): SerializedEntity {
        val entityId = AthleticEntity.Id(
            id = data.id,
            type = data.type
        )
        val jsonBlobString = try {
            moshi.adapter(data.javaClass).toJson(data)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                throw e
            }
            Timber.e(e, "Failed to serialize: ${entityId.type}-${entityId.id}")
            null
        }
        return SerializedEntity().apply {
            id = entityId
            type = entityId.type
            rawId = entityId.id
            jsonBlob = jsonBlobString.orEmpty()
            updatedTime = timeProvider.currentDatetime
        }
    }
}