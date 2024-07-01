package com.theathletic.followable

import com.theathletic.utility.safeValueOf

typealias FollowableType = Followable.Type
typealias FollowableId = Followable.Id

interface Followable {
    val id: Id
    val name: String
    val shortName: String
    val searchText: String

    enum class Type {
        TEAM,
        LEAGUE,
        AUTHOR;
    }

    data class Id(
        val id: String,
        val type: Type
    ) : java.io.Serializable {
        override fun toString() = "$type:$id"

        companion object {
            fun parse(value: String): Id? {
                if (value.isEmpty()) return null
                if (!value.contains(":")) return null

                val (type, id) = value.split(":")
                val entityType = safeValueOf<Type>(type) ?: return null

                return Id(id = id, type = entityType)
            }
        }
    }
}

val Followable.Id.analyticsType: String
    get() = when (type) {
        Followable.Type.TEAM -> "team_id"
        Followable.Type.LEAGUE -> "league_id"
        Followable.Type.AUTHOR -> "author_id"
    }

val Followable.Id.analyticsId: String
    get() = id

fun Followable.analyticsObjectType(isUnfollow: Boolean = false): String = if (isUnfollow) {
    "user-remove-${typeName()}"
} else {
    "user-add-${typeName()}"
}

fun Followable.analyticsIdType(): String = id.analyticsType
fun Followable.typeName(): String = id.type.name.lowercase()
fun Followable.isTeam(): Boolean = id.type == Followable.Type.TEAM
fun Followable.isLeague(): Boolean = id.type == Followable.Type.LEAGUE
fun Followable.isAuthor(): Boolean = id.type == Followable.Type.AUTHOR
fun Followable.rawId(): Long = id.id.toLong()
val Followable.Id.legacyId: Long? get() = id.toLongOrNull()