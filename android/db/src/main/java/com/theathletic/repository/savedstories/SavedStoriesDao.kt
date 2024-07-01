package com.theathletic.repository.savedstories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.theathletic.entity.SavedStoriesEntity
import io.reactivex.Maybe

@Dao
abstract class SavedStoriesDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(storiesList: List<SavedStoriesEntity>)

    @Delete
    abstract fun delete(story: SavedStoriesEntity)

    @Query("DELETE FROM saved_stories WHERE id = :id")
    abstract fun delete(id: Long): Int

    @Query("DELETE FROM saved_stories")
    abstract fun clear()

    @Transaction
    open fun updateSavedStoriesList(storiesList: List<SavedStoriesEntity>) {
        clear()
        insert(storiesList)
    }

    @Query("SELECT * FROM saved_stories")
    abstract fun getSavedStories(): Maybe<MutableList<SavedStoriesEntity>>

    // TT Sets
    @Query("UPDATE saved_stories SET isReadByUser= :isReadByUser WHERE id = :id")
    abstract fun markItemRead(id: Long, isReadByUser: Boolean): Int
}