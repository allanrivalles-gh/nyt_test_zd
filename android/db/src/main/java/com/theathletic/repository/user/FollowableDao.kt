package com.theathletic.repository.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theathletic.followable.Followable

@Dao
abstract class FollowableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTeams(teamList: List<TeamLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAuthors(authorList: List<AuthorLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLeagues(leagueList: List<LeagueLocal>)

    @Query("SELECT * FROM team WHERE id = :id")
    abstract suspend fun getTeam(id: Followable.Id): TeamLocal?

    @Query("SELECT * FROM team WHERE graphqlId = :graphqlId")
    abstract suspend fun getTeam(graphqlId: String): TeamLocal?

    @Query("SELECT * FROM team")
    abstract suspend fun getAllTeams(): List<TeamLocal>

    @Query("SELECT COUNT(*) FROM team")
    abstract suspend fun getCountOfTeams(): Long

    @Query("SELECT * FROM league WHERE id = :id")
    abstract suspend fun getLeague(id: Followable.Id): LeagueLocal?

    @Query("SELECT * FROM author WHERE id = :id")
    abstract suspend fun getAuthor(id: Followable.Id): AuthorLocal?

    @Query("SELECT * FROM team WHERE url = :url")
    abstract suspend fun getTeamFromUrl(url: String?): TeamLocal?

    @Query("SELECT * FROM league WHERE url = :url")
    abstract suspend fun getLeagueFromUrl(url: String?): LeagueLocal?

    @Query("SELECT * FROM author WHERE url = :url")
    abstract suspend fun getAuthorFromUrl(url: String?): AuthorLocal?

    @Query("SELECT * FROM league WHERE league IN (:leagues)")
    abstract suspend fun getFilteredLeagues(leagues: Set<com.theathletic.entity.main.League>): List<LeagueLocal>?
}