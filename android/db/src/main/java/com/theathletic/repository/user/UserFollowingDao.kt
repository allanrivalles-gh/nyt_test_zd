package com.theathletic.repository.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class UserFollowingDao {
    @Query("DELETE FROM user_following")
    abstract fun clearFollowing()

    @Query("SELECT * FROM team JOIN user_following WHERE team.id = user_following.id")
    abstract fun getFollowingTeams(): Flow<List<TeamLocal>>

    @Query("SELECT * FROM league JOIN user_following WHERE league.id = user_following.id")
    abstract fun getFollowingLeagues(): Flow<List<LeagueLocal>>

    @Query("SELECT * FROM author JOIN user_following WHERE author.id = user_following.id")
    abstract fun getFollowingAuthors(): Flow<List<AuthorLocal>>

    @Transaction
    open fun updateUserFollowingItems(userFollowingItems: List<UserFollowingItem>) {
        clearFollowing()
        insertUserFollowingItems(userFollowingItems)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUserFollowingItems(userFollowingItems: List<UserFollowingItem>)

    fun getFollowingTeamsDistinct() = getFollowingTeams().distinctUntilChanged()
    fun getFollowingLeaguesDistinct() = getFollowingLeagues().distinctUntilChanged()
    fun getFollowingAuthorsDistinct() = getFollowingAuthors().distinctUntilChanged()
}