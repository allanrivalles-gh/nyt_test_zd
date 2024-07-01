package com.theathletic.entity.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.annotation.autokoin.Scope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface EntityQueries {
    // Following
    fun getFollowedFlow(type: AthleticEntity.Type): Flow<List<AthleticEntity>>
    suspend fun replaceFollowedByType(
        type: AthleticEntity.Type,
        entities: List<AthleticEntity>
    )

    // Saved
    fun getSavedFlow(type: AthleticEntity.Type): Flow<List<AthleticEntity>>
    suspend fun addSaved(entityId: AthleticEntity.Id)
    suspend fun removeSaved(entityId: AthleticEntity.Id)
    suspend fun removeSavedByType(type: AthleticEntity.Type)
    suspend fun replaceSavedByType(type: AthleticEntity.Type, entities: List<AthleticEntity>)
}

@Exposes(EntityQueries::class)
class EntityQueryDataSource @AutoKoin(Scope.SINGLE) constructor(
    private val entitySerializer: EntitySerializer,
    private val serializedEntityQueryDao: SerializedEntityQueryDao
) : EntityQueries {

    override fun getFollowedFlow(type: AthleticEntity.Type) =
        serializedEntityQueryDao.getFollowedEntitiesFlow(type)
            .distinctUntilChanged()
            .map { it.mapNotNull(entitySerializer::deserialize) }

    override suspend fun replaceFollowedByType(
        type: AthleticEntity.Type,
        entities: List<AthleticEntity>
    ) {
        serializedEntityQueryDao.replaceFollowedByType(
            type,
            entities.map { FollowedEntity(id = it.entityId, type = it.type, rawId = it.id) }
        )
    }

    // Saved
    override fun getSavedFlow(type: AthleticEntity.Type) =
        serializedEntityQueryDao.getSavedEntitiesFlow(type)
            .distinctUntilChanged()
            .map { it.mapNotNull(entitySerializer::deserialize) }

    override suspend fun addSaved(entityId: AthleticEntity.Id) {
        serializedEntityQueryDao.addSaved(
            SavedEntity(id = entityId, type = entityId.type, rawId = entityId.id)
        )
    }

    override suspend fun removeSaved(entityId: AthleticEntity.Id) {
        serializedEntityQueryDao.deleteSaved(
            SavedEntity(id = entityId, type = entityId.type, rawId = entityId.id)
        )
    }

    override suspend fun removeSavedByType(type: AthleticEntity.Type) {
        serializedEntityQueryDao.deleteSaved(type)
    }

    override suspend fun replaceSavedByType(
        type: AthleticEntity.Type,
        entities: List<AthleticEntity>
    ) {
        serializedEntityQueryDao.replaceSavedByType(
            type,
            entities.map { SavedEntity(id = it.entityId, type = it.type, rawId = it.id) }
        )
    }
}

inline fun <reified T : AthleticEntity> Flow<List<AthleticEntity>>.filter() = map { list ->
    list.filterIsInstance<T>()
}