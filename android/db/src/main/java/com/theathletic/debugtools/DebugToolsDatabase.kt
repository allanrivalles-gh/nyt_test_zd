package com.theathletic.debugtools

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [RemoteConfigEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(DebugToolsDatabaseConverters::class)
abstract class DebugToolsDatabase : RoomDatabase() {

    abstract fun debugToolsDao(): DebugToolsDao

    companion object {
        fun newInstance(context: Context): DebugToolsDatabase = synchronized(this) {
            return Room.databaseBuilder(context, DebugToolsDatabase::class.java, "developer-tools-database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // Tt our DB operations should be really quick, so we can enable this
                .build()
        }
    }
}

class DebugToolsDatabaseConverters {
    @TypeConverter
    fun observableBooleanToBoolean(value: ObservableBoolean): Boolean = value.get()

    @TypeConverter
    fun booleanToObservableBoolean(value: Boolean): ObservableBoolean = ObservableBoolean(value)
}