package com.theathletic.debugtools.logs.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.theathletic.analytics.newarch.CollectorKey
import com.theathletic.debugtools.logs.AnalyticsLogModel
import com.theathletic.extension.extLogError

@Database(entities = [AnalyticsLogModel::class], version = 4, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AnalyticsLogDatabase : RoomDatabase() {
    abstract fun analyticsHistoryDao(): AnalyticsLogDao

    companion object {
        fun newInstance(context: Context): AnalyticsLogDatabase {
            return Room.databaseBuilder(
                context,
                AnalyticsLogDatabase::class.java,
                "athletic_analytics_history_log.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

class Converters {
    private val gson: Gson = GsonBuilder().setLenient().create()
    private var mapOfStringToString = object : TypeToken<Map<String, String>>() {}.type
    private var collectorKeyList = object : TypeToken<List<CollectorKey>>() {}.type

    @TypeConverter
    fun collectKeyToString(key: List<CollectorKey>) = gson.toJson(key)

    @TypeConverter
    fun stringToCollectorKey(str: String): List<CollectorKey> = gson.fromJson(str, collectorKeyList)

    @TypeConverter
    fun stringToParamsMaps(value: String?): Map<String, String>? {
        return try {
            gson.fromJson(value, mapOfStringToString)
        } catch (exception: Throwable) {
            exception.extLogError()
            emptyMap()
        }
    }

    @TypeConverter
    fun paramsMapToString(params: Map<String, String>?): String? {
        return try {
            gson.toJson(params)
        } catch (exception: Throwable) {
            exception.extLogError()
            ""
        }
    }
}