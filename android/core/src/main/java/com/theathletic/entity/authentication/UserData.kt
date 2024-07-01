package com.theathletic.entity.authentication

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "user_data")
open class UserData : Serializable {
    @PrimaryKey
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("articles_read")
    var articlesRead: ArrayList<Long> = ArrayList()

    @SerializedName("articles_rated")
    var articlesRated: ArrayList<Long> = ArrayList()

    @SerializedName("articles_saved")
    var articlesSaved: ArrayList<Long> = ArrayList()

    @SerializedName("comments_liked")
    var commentsLiked: ArrayList<Long> = ArrayList()

    @SerializedName("comments_flagged")
    var commentsFlagged: ArrayList<Long> = ArrayList()
}