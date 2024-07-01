package com.theathletic.entity.serialization

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.theathletic.datetime.Datetime

class DatetimeAdapter {
    @ToJson
    fun toJson(datetime: Datetime) = datetime.timeMillis
    @FromJson
    fun fromJson(timeMillis: Long) = Datetime(timeMillis)
}