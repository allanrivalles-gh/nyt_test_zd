package com.theathletic.followable

import androidx.room.Dao
import androidx.room.Query
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM team")
    fun getTeams(): Flow<List<TeamLocal>>

    @Query("SELECT * FROM team WHERE leagueId IN (SELECT id FROM league WHERE hasScores = 1)")
    fun getTeamsWithScores(): Flow<List<TeamLocal>>

    @Query("SELECT * FROM team WHERE id = :id")
    suspend fun getTeam(id: Followable.Id): TeamLocal?
}

@Dao
interface AuthorDao {
    @Query("SELECT * FROM author")
    fun getAuthors(): Flow<List<AuthorLocal>>

    @Query("SELECT * FROM author WHERE id = :id")
    suspend fun getAuthor(id: Followable.Id): AuthorLocal?
}

@Dao
interface LeagueDao {
    @Query("SELECT * FROM league")
    fun getLeagues(): Flow<List<LeagueLocal>>

    @Query("SELECT * FROM league WHERE hasScores = 1")
    fun getLeaguesWithScores(): Flow<List<LeagueLocal>>

    @Query("SELECT * FROM league WHERE id = :id")
    suspend fun getLeague(id: Followable.Id): LeagueLocal?
}