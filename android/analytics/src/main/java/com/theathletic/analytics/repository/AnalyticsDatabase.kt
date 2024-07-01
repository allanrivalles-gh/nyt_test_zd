package com.theathletic.analytics.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.theathletic.analytics.data.local.AnalyticsEvent
import com.theathletic.analytics.data.local.FlexibleAnalyticsEvent
import com.theathletic.analytics.newarch.context.DeepLinkParams
import com.theathletic.extension.extLogError

@Database(
    entities = [
        AnalyticsEvent::class,
        FlexibleAnalyticsEvent::class
    ],
    version = 10,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AnalyticsDatabase : RoomDatabase() {

    abstract fun analyticsEventDao(): AnalyticsEventDao
    abstract fun flexibleAnalyticsEventDao(): FlexibleAnalyticsEventDao

    companion object {
        fun newInstance(context: Context): AnalyticsDatabase {
            return Room.databaseBuilder(
                context,
                AnalyticsDatabase::class.java,
                "athletic_analytics.db"
            )
                .addMigrations(Migration1To2())
                .addMigrations(Migration2To3())
                .addMigrations(Migration3To4())
                .addMigrations(Migration4To5())
                .addMigrations(Migration5To6())
                .addMigrations(Migration6To7())
                .addMigrations(Migration7To8())
                .addMigrations(Migration8To9())
                .addMigrations(Migration9To10())
                .build()
        }
    }
}

class Converters {
    private val gson: Gson = GsonBuilder().setLenient().create()
    private var type = object : TypeToken<Map<String, String>>() {}.type

    @TypeConverter
    fun stringToParamsMaps(value: String?): Map<String, String>? {
        return try {
            gson.fromJson(value, type)
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

    @TypeConverter
    fun stringToDeeplinkParams(value: String?): DeepLinkParams? {
        return try {
            gson.fromJson(value, type)
        } catch (exception: Throwable) {
            exception.extLogError()
            null
        }
    }

    @TypeConverter
    fun deeplinkParamsToString(params: DeepLinkParams?): String? {
        return try {
            gson.toJson(params)
        } catch (exception: Throwable) {
            exception.extLogError()
            ""
        }
    }
}

private class Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // TT 1. Re-create events table
        database.execSQL("DROP TABLE IF EXISTS events")
        database.execSQL("CREATE TABLE IF NOT EXISTS `events` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `source` TEXT, `action` TEXT, `verb` TEXT NOT NULL, `objectType` TEXT NOT NULL, `objectId` INTEGER NOT NULL, `dateTime` TEXT, `params` TEXT)")
    }
}

private class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // TT 1. Re-create events table
        database.execSQL("DROP TABLE IF EXISTS events")
        database.execSQL("CREATE TABLE IF NOT EXISTS `events` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `source` TEXT, `action` TEXT, `verb` TEXT NOT NULL, `objectType` TEXT, `objectId` INTEGER, `dateTime` TEXT, `params` TEXT, `context` TEXT)")
    }
}

private class Migration3To4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // TT 1. Re-create events table
        database.execSQL("DROP TABLE IF EXISTS events")
        database.execSQL("CREATE TABLE IF NOT EXISTS `events` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `source` TEXT, `action` TEXT, `verb` TEXT NOT NULL, `objectType` TEXT, `objectId` TEXT, `dateTime` TEXT, `params` TEXT, `context` TEXT)")
    }
}

private class Migration4To5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // TT 1. Create the analytics_events table
        database.execSQL("CREATE TABLE IF NOT EXISTS `analytics_events` (`verb` TEXT NOT NULL, `previousView` TEXT, `view` TEXT, `element` TEXT, `objectType` TEXT, `objectId` TEXT, `metaBlob` TEXT, `dateTime` TEXT, `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
    }
}

private class Migration5To6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add timestamp as a long
        database.execSQL("ALTER TABLE `analytics_events` ADD `timestampSeconds` INTEGER NOT NULL DEFAULT 0")
    }
}

private class Migration6To7 : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `analytics_events`")
        database.execSQL("CREATE TABLE IF NOT EXISTS `analytics_events` (`verb` TEXT NOT NULL, `previousView` TEXT, `view` TEXT, `element` TEXT, `objectType` TEXT, `objectId` TEXT, `metaBlob` TEXT, `dateTime` TEXT, `timestampMs` INTEGER NOT NULL, `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
    }
}

private class Migration7To8 : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `flexible_analytics_events`")
        database.execSQL("CREATE TABLE IF NOT EXISTS `flexible_analytics_events` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestampMs` INTEGER NOT NULL, `kafkaTopicName` TEXT NOT NULL, `schemaJsonBlob` TEXT NOT NULL, `extrasJsonBlob` TEXT NOT NULL)")
    }
}

private class Migration8To9 : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `analytics_events` ADD `source` TEXT")
    }
}

private class Migration9To10 : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS `events`")
    }
}