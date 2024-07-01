package com.theathletic.entity.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theathletic.datetime.Datetime

@Entity(tableName = "serialized_entity")
class SerializedEntity {
    @PrimaryKey
    var id: AthleticEntity.Id = AthleticEntity.Id("", AthleticEntity.Type.UNKNOWN)
    var type: AthleticEntity.Type = AthleticEntity.Type.UNKNOWN
    var rawId: String = ""
    var jsonBlob: String = ""
    var updatedTime: Datetime = Datetime(0)
}

@Entity(tableName = "followed_entities")
data class FollowedEntity(
    @PrimaryKey val id: AthleticEntity.Id = AthleticEntity.Id("", AthleticEntity.Type.UNKNOWN),
    val type: AthleticEntity.Type = AthleticEntity.Type.UNKNOWN,
    val rawId: String = ""
)

@Entity(tableName = "saved_entities")
data class SavedEntity(
    @PrimaryKey val id: AthleticEntity.Id = AthleticEntity.Id("", AthleticEntity.Type.UNKNOWN),
    val type: AthleticEntity.Type = AthleticEntity.Type.UNKNOWN,
    val rawId: String = ""
)