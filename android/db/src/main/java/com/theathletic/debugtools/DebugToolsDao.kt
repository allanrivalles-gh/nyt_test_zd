package com.theathletic.debugtools

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Maybe

@Dao
abstract class DebugToolsDao {
    // TT Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertModifiedRemoteConfig(modifiedRemoteConfigEntity: RemoteConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertModifiedRemoteConfig(modifiedRemoteConfigEntity: List<RemoteConfigEntity>)

    @Transaction
    open fun insert(modifiedRemoteConfigEntity: List<RemoteConfigEntity>) {
        insertModifiedRemoteConfig(modifiedRemoteConfigEntity)
    }

    // TT Gets
    @Query("SELECT * FROM developer_tools_modified_remote_config")
    abstract fun getModifiedRemoteConfig(): Maybe<MutableList<RemoteConfigEntity>>

    @Query("SELECT * FROM developer_tools_modified_remote_config")
    abstract fun getModifiedRemoteConfigSync(): MutableList<RemoteConfigEntity>

    @Query("SELECT * FROM developer_tools_modified_remote_config WHERE developer_tools_modified_remote_config.entryKey = :key  LIMIT 1")
    abstract fun getModifiedRemoteConfig(key: String): Maybe<RemoteConfigEntity>

    @Query("SELECT * FROM developer_tools_modified_remote_config WHERE developer_tools_modified_remote_config.entryKey = :key  LIMIT 1")
    abstract fun getModifiedRemoteConfigSync(key: String): RemoteConfigEntity?

    // TT Deletes
    @Query("DELETE FROM developer_tools_modified_remote_config")
    abstract fun clearModifiedRemoteConfig()

    @Query("DELETE FROM developer_tools_modified_remote_config WHERE developer_tools_modified_remote_config.entryKey = :key")
    abstract fun deleteModifiedRemoteConfig(key: String)

    @Transaction
    open fun clear() {
        clearModifiedRemoteConfig()
    }
}