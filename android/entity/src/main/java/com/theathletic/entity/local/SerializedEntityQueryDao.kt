package com.theathletic.entity.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SerializedEntityQueryDao {
    // Followed
    @Query("SELECT * FROM serialized_entity WHERE type = :type AND id IN (SELECT id from followed_entities)")
    abstract fun getFollowedEntitiesFlow(type: AthleticEntity.Type): Flow<List<SerializedEntity>>

    @Query("DELETE FROM followed_entities WHERE type = :type")
    abstract suspend fun deleteFollowed(type: AthleticEntity.Type)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addFollowedEntities(ids: List<FollowedEntity>)

    @Transaction
    open suspend fun replaceFollowedByType(
        type: AthleticEntity.Type,
        entities: List<FollowedEntity>
    ) {
        deleteFollowed(type)
        addFollowedEntities(entities)
    }

    // Saved
    @Query("SELECT * FROM serialized_entity WHERE type = :type AND id IN (SELECT id from saved_entities)")
    abstract fun getSavedEntitiesFlow(type: AthleticEntity.Type): Flow<List<SerializedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addSaved(entity: SavedEntity)

    @Delete
    abstract suspend fun deleteSaved(entity: SavedEntity)

    @Query("DELETE FROM saved_entities WHERE type = :type")
    abstract suspend fun deleteSaved(type: AthleticEntity.Type)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addSavedEntities(entities: List<SavedEntity>)

    @Transaction
    open suspend fun replaceSavedByType(
        type: AthleticEntity.Type,
        entities: List<SavedEntity>
    ) {
        deleteSaved(type)
        addSavedEntities(entities)
    }
}