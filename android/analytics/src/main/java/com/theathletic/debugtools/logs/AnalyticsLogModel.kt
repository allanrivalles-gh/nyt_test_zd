package com.theathletic.debugtools.logs

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.theathletic.analytics.newarch.CollectorKey

@Entity(tableName = "analytics_history_log")
data class AnalyticsLogModel(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("uid")
    var uid: Long = 0L,

    @Expose
    @SerializedName("name")
    val name: String,

    val isNoisy: Boolean,

    val collectors: List<CollectorKey>,

    @Expose
    @SerializedName("params")
    val params: Map<String, String> = emptyMap()
)