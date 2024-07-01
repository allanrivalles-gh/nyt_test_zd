package com.theathletic.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theathletic.adapter.TheSame
import com.theathletic.data.RemoteModel
import java.io.Serializable

@Entity(tableName = "saved_stories")
open class SavedStoriesEntity : Serializable, TheSame, RemoteModel {
    @PrimaryKey
    var id: String = ""
    var postTitle: String = ""
    var authorName: String = ""
    var postDateGmt: String = ""
    var postImgUrl: String? = null
    var isReadByUser: Boolean = false
    var commentsCount: Long = 0L

    override fun isItemTheSame(other: Any?): Boolean = when {
        this === other -> true
        other !is SavedStoriesEntity -> false
        id == other.id -> true
        else -> false
    }

    override fun isContentTheSame(other: Any?): Boolean = when {
        other !is SavedStoriesEntity -> false
        isReadByUser != other.isReadByUser -> false
        else -> true
    }
}