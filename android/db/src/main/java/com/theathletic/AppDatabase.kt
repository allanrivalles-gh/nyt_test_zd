package com.theathletic

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theathletic.entity.SavedStoriesEntity

// TT this is just placeholder for old database to handle all migrations
@Database(entities = [SavedStoriesEntity::class], version = 36, exportSchema = true)
abstract class AppDatabase : RoomDatabase()