package com.theathletic.entity.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SerializedEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: SerializedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: List<SerializedEntity>)

    @Query("SELECT * FROM serialized_entity WHERE id = :id LIMIT 1")
    abstract suspend fun getEntity(id: AthleticEntity.Id): SerializedEntity?

    @Query("SELECT * FROM serialized_entity WHERE id = :id LIMIT 1")
    abstract fun getEntityFlow(id: AthleticEntity.Id): Flow<SerializedEntity?>

    @Query("SELECT * FROM serialized_entity WHERE id IN(:ids)")
    abstract suspend fun getEntities(ids: List<AthleticEntity.Id>): List<SerializedEntity>

    @Query("SELECT * FROM serialized_entity WHERE type IN(:types)")
    abstract suspend fun getEntitiesWithType(types: List<AthleticEntity.Type>): List<SerializedEntity>

    @Query("DELETE FROM serialized_entity WHERE datetime(updatedTime / 1000, 'unixepoch', 'utc') <= datetime('now','utc',:timeSpan) AND id NOT IN (:excludeIds)")
    abstract suspend fun deleteEntitiesBefore(timeSpan: String, excludeIds: List<AthleticEntity.Id>)

    @Query("DELETE FROM serialized_entity WHERE id IN (SELECT id FROM serialized_entity ORDER BY updatedTime DESC LIMIT -1 OFFSET :keepCount)")
    abstract suspend fun deleteEntitiesExceptLatest(keepCount: Long)
}