package com.theathletic.entity.local

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.local.merge.ArticleEntityMerger
import com.theathletic.entity.local.merge.BoxScoreEntityMerger
import com.theathletic.entity.local.merge.EntityMerger
import com.theathletic.entity.local.merge.LiveBlogEntityMerger
import com.theathletic.feed.data.local.AnnouncementEntity
import com.theathletic.feed.data.local.AnnouncementEntityMerger
import com.theathletic.liveblog.data.local.LiveBlogEntity
import com.theathletic.podcast.data.local.PodcastEpisodeEntity
import com.theathletic.podcast.data.local.PodcastEpisodeMerger
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.scores.data.local.BoxScoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * The primary way to interact with [AthleticEntity] when writing them to and reading them from the
 * DB. All repositories and fetchers which use entities should do so by this class which will handle
 * the serialization/deserialization to the key/value pairs stored in the DB.
 *
 * When dealing with just one entity type, the inline functions are very convenient as you can
 * specify the [AthleticEntity] class you want and query for it just using a [String] id such as:
 *
 * dataSource.get<ArticleEntity>(id)
 *
 * When expecting more than one entity type, you can use the more general functions that take in
 * [AthleticEntity.Id] and return generalized [AthleticEntity].
 */
class EntityDataSource @AutoKoin(Scope.SINGLE) constructor(
    val entitySerializer: EntitySerializer,
    val serializedEntityDao: SerializedEntityDao,
    val userDataRepository: IUserDataRepository
) {

    companion object {
        // https://stackoverflow.com/a/15313495
        // SQLite `in` queries have a max variable count of 1000. 900 should cover the vast majority
        // of our use cases in a single DB query.
        const val MAX_QUERY_CHUNK_SIZE = 900
    }

    val updateFlow: Flow<Set<AthleticEntity.Type>>
        get() = _updateFlow
    private val _updateFlow = MutableSharedFlow<Set<AthleticEntity.Type>>(0, extraBufferCapacity = 1)

    suspend inline fun <reified T : AthleticEntity> insert(entity: T) {
        entitySerializer.serialize(entity)?.let {
            serializedEntityDao.insert(it)
            notifyTypesUpdated(setOf(entity.type))
        }
    }

    suspend inline fun <reified T : AthleticEntity> insertOrUpdate(entity: T) {
        val mergedEntity = get<T>(entity.id)?.let { oldEntity ->
            entity.merger?.merge(oldEntity, entity)
        } ?: entity

        entitySerializer.serialize(mergedEntity)?.let {
            serializedEntityDao.insert(it)
            notifyTypesUpdated(setOf(entity.type))
        }
    }

    suspend inline fun insert(entities: List<AthleticEntity>) {
        serializedEntityDao.insert(entities.map { entitySerializer.serialize(it) })
        notifyTypesUpdated(entities.map { it.type }.toSet())
    }

    suspend inline fun <reified T : AthleticEntity> insertOrUpdate(entities: List<T>) {
        val dedupedEntities = deduplicateEntities(entities)
        val mergedEntities = dedupedEntities.map { entity ->
            val oldEntity = getEntity(AthleticEntity.Id(entity.id, entity.type))
            oldEntity?.let {
                entity.merger?.merge(it as T, entity)
            } ?: entity
        }

        serializedEntityDao.insert(mergedEntities.mapNotNull { entitySerializer.serialize(it) })
        notifyTypesUpdated(entities.map { it.type }.toSet())
    }

    inline fun <reified T : AthleticEntity> deduplicateEntities(entities: List<T>): List<T> {
        return entities.groupingBy(AthleticEntity::entityId).reduce { _, accumulator, element ->
            accumulator.merger?.merge(accumulator, element) ?: accumulator
        }.values.toList()
    }

    suspend inline fun <reified T : AthleticEntity> get(id: String): T? {
        val serializedEntity = serializedEntityDao.getEntity(
            AthleticEntity.Id(id = id, type = T::class.entityToType)
        )
        return serializedEntity?.let { entitySerializer.deserialize(it) as? T }
    }

    inline fun <reified T : AthleticEntity> getFlow(id: String): Flow<T?> {
        val serializedEntityFlow = serializedEntityDao.getEntityFlow(
            AthleticEntity.Id(id = id, type = T::class.entityToType)
        )
        return serializedEntityFlow.map {
            it?.let { entitySerializer.deserialize(it) as? T }
        }.distinctUntilChanged()
    }

    suspend inline fun <reified T : AthleticEntity> get(ids: List<String>): List<T> {
        val entityIds = ids.map { AthleticEntity.Id(id = it, type = T::class.entityToType) }

        return entityIds.chunked(MAX_QUERY_CHUNK_SIZE).flatMap {
            serializedEntityDao.getEntities(it)
        }.mapNotNull {
            entitySerializer.deserialize(it) as? T
        }
    }

    suspend inline fun getEntity(id: AthleticEntity.Id): AthleticEntity? {
        return serializedEntityDao.getEntity(id)?.let(entitySerializer::deserialize)
    }

    suspend inline fun getEntities(ids: List<AthleticEntity.Id>): List<AthleticEntity> {
        return ids.chunked(MAX_QUERY_CHUNK_SIZE).flatMap {
            serializedEntityDao.getEntities(it)
        }.mapNotNull(entitySerializer::deserialize)
    }

    suspend inline fun getEntitiesWithType(type: List<AthleticEntity.Type>): List<AthleticEntity> {
        return type.chunked(MAX_QUERY_CHUNK_SIZE).flatMap {
            serializedEntityDao.getEntitiesWithType(it)
        }.mapNotNull(entitySerializer::deserialize)
    }

    suspend inline fun <reified T : AthleticEntity> update(
        id: String,
        ignoreMerger: Boolean = false,
        updateBlock: T.() -> T
    ): T? {
        val entity = serializedEntityDao.getEntity(
            AthleticEntity.Id(id = id, type = T::class.entityToType)
        )?.let {
            entitySerializer.deserialize(it) as? T
        } ?: return null

        if (ignoreMerger) {
            insert(updateBlock(entity))
        } else {
            insertOrUpdate(updateBlock(entity))
        }

        return entity
    }

    suspend fun deleteOldEntities() {
        serializedEntityDao.deleteEntitiesBefore(
            timeSpan = "-30 day",
            excludeIds = userDataRepository.savedArticleIds().toArticleEntityId()
        )
        serializedEntityDao.deleteEntitiesExceptLatest(100_000)
    }

    suspend fun notifyTypesUpdated(types: Set<AthleticEntity.Type>) {
        _updateFlow.emit(types)
    }

    val <T : AthleticEntity> T.merger: EntityMerger<T>?
        get() = when (this) {
            is ArticleEntity -> ArticleEntityMerger
            is AnnouncementEntity -> AnnouncementEntityMerger
            is BoxScoreEntity -> BoxScoreEntityMerger
            is PodcastEpisodeEntity -> PodcastEpisodeMerger
            is LiveBlogEntity -> LiveBlogEntityMerger
            else -> null
        } as? EntityMerger<T>

    private fun List<Long>.toArticleEntityId() = map {
        AthleticEntity.Id(
            it.toString(),
            AthleticEntity.Type.ARTICLE
        )
    }
}