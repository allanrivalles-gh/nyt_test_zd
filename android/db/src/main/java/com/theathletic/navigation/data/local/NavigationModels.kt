package com.theathletic.navigation.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class NavigationEntity(
    val title: String,
    @SerializedName("deeplink_url")
    val deeplinkUrl: String, // theathletic://feed/user, etc.
    @SerializedName("entity_type")
    val entityType: String, // nav_link, nav_link_selector
    val index: Int
)

@Entity(tableName = "navigation_entity")
data class RoomNavigationEntity(
    val sourceKey: String,
    val title: String,
    val deeplinkUrl: String,
    val entityType: String,
    val index: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    companion object {
        fun toNavigationEntity(roomEntity: RoomNavigationEntity): NavigationEntity {
            return NavigationEntity(
                roomEntity.title,
                roomEntity.deeplinkUrl,
                roomEntity.entityType,
                roomEntity.index
            )
        }
    }
}