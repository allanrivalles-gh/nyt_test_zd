package com.theathletic.repository.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theathletic.entity.authentication.UserData
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDataDao {
    // TT UserData
    // Tt Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserData(article: UserData)

    // Tt Gets
    @get:Query("SELECT * FROM user_data")
    abstract val allUserData: List<UserData>

    @Query("SELECT * FROM user_data WHERE id = 0 LIMIT 1")
    abstract fun getUserData(): Maybe<UserData>

    @Query("SELECT * FROM user_data WHERE id = 0 LIMIT 1")
    abstract fun getUserDataFlow(): Flow<UserData?>

    // Tt Delete
    @Query("DELETE FROM user_data") // TODO  AND isSaved == 0 rewrite to UserData().articlesSaved
    abstract fun clearUserData()
}