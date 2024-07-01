package com.theathletic.navigation.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

enum class NavigationSource(val dbKey: String) {
    FEED("feed"),
    SCORES("scores")
}

@Dao
abstract class NavigationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(navigationEntities: List<RoomNavigationEntity>)

    @Query("DELETE FROM navigation_entity WHERE sourceKey = :sourceKey")
    abstract suspend fun deleteBySourceKey(sourceKey: String)

    @Query("SELECT * FROM navigation_entity WHERE sourceKey = :sourceKey")
    abstract fun getNavigationEntities(sourceKey: String): Flow<List<RoomNavigationEntity>>

    @Query("DELETE FROM navigation_entity")
    abstract suspend fun deleteAllNavigationEntities()

    @Transaction
    open suspend fun replaceNavigationEntities(
        navigationSource: NavigationSource,
        navigationEntities: List<RoomNavigationEntity>
    ) {
        deleteBySourceKey(navigationSource.dbKey)
        insertAll(navigationEntities)
    }
}