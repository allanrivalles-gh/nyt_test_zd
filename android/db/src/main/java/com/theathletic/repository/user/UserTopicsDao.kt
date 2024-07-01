package com.theathletic.repository.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemPodcast
import com.theathletic.entity.settings.UserTopicsItemTeam
import io.reactivex.Maybe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class UserTopicsDao {
    // TT Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTeam(team: UserTopicsItemTeam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTeamSuspend(team: UserTopicsItemTeam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTeams(teamList: List<UserTopicsItemTeam>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLeague(league: UserTopicsItemLeague)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLeagueSuspend(league: UserTopicsItemLeague)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLeagues(leagueList: List<UserTopicsItemLeague>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAuthor(author: UserTopicsItemAuthor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAuthorSuspend(author: UserTopicsItemAuthor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAuthors(authorList: List<UserTopicsItemAuthor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPodcasts(authorList: List<UserTopicsItemPodcast>)

    @Transaction
    @Suppress("SENSELESS_COMPARISON")
    open fun insert(
        teams: List<UserTopicsItemTeam>,
        leagues: List<UserTopicsItemLeague>,
        authors: List<UserTopicsItemAuthor>,
        podcasts: List<UserTopicsItemPodcast>
    ) {
        // Tt Do not rewrite evergreenPostsReadCount!
        val oldTeams = getUserTopicsTeamsRaw()
        teams.forEach { team ->
            oldTeams.firstOrNull { it.id == team.id }?.let { team.evergreenPostsReadCount = it.evergreenPostsReadCount }
        }

        clear()

        insertTeams(teams)
        insertLeagues(leagues)
        insertAuthors(authors)
        insertPodcasts(podcasts)
    }

    // TT Updates
    @Query("UPDATE user_topics_league SET isFollowed = :isFollowed WHERE id = :leagueId")
    abstract fun updateUserTopicsLeagueFollowStatus(leagueId: Long, isFollowed: Boolean)

    @Query("UPDATE user_topics_team SET notifyStories = :notification WHERE id = :teamId")
    abstract fun updateUserTopicsTeamDailyStoriesNotification(teamId: Long, notification: Boolean)

    @Query("UPDATE user_topics_team SET notifyGames = :notification WHERE id = :teamId")
    abstract fun updateUserTopicsTeamGameResultNotification(teamId: Long, notification: Boolean)

    @Query("UPDATE user_topics_author SET notifyStories = :notification WHERE id = :authorId")
    abstract fun updateUserTopicsAuthorStoriesNotification(authorId: Long, notification: Boolean)

    @Query("UPDATE user_topics_league SET notifyStories = :notification WHERE id = :leagueId")
    abstract fun updateUserTopicsLeagueNotification(leagueId: Long, notification: Boolean)

    @Query("UPDATE user_topics_team SET evergreenPostsReadCount = :count WHERE id = :teamId")
    abstract fun updateUserTopicsTeamEvergreenPostReadCount(teamId: Long, count: Long)

    // TT Gets
    @Query("SELECT * FROM user_topics_team")
    abstract fun getUserTopicsTeams(): Maybe<MutableList<UserTopicsItemTeam>>

    @Query("SELECT * FROM user_topics_team")
    abstract fun getUserTopicsTeamsRaw(): MutableList<UserTopicsItemTeam>

    @Query("SELECT * FROM user_topics_league")
    abstract fun getUserTopicsLeagues(): Maybe<MutableList<UserTopicsItemLeague>>

    @Query("SELECT * FROM user_topics_author")
    abstract fun getUserTopicsAuthors(): Maybe<MutableList<UserTopicsItemAuthor>>

    @Query("SELECT * FROM user_topics_podcast")
    abstract fun getUserTopicsPodcasts(): Maybe<MutableList<UserTopicsItemPodcast>>

    @Query("SELECT * FROM user_topics_team where isFollowed = 1")
    abstract fun getFollowedTeams(): Flow<List<UserTopicsItemTeam>>

    fun getFollowedTeamsDistinct() = getFollowedTeams().distinctUntilChanged()

    @Query("SELECT * FROM user_topics_league where isFollowed = 1")
    abstract fun getFollowedLeagues(): Flow<List<UserTopicsItemLeague>>

    fun getFollowedLeaguesDistinct() = getFollowedLeagues().distinctUntilChanged()

    @Query("SELECT * FROM user_topics_league where hasScores = 1")
    abstract suspend fun getLeaguesWithScores(): List<UserTopicsItemLeague>

    @Query("SELECT * FROM user_topics_author where isFollowed = 1")
    abstract fun getFollowedAuthors(): Flow<List<UserTopicsItemAuthor>>

    fun getFollowedAuthorsDistinct() = getFollowedAuthors().distinctUntilChanged()

    @Query("SELECT * FROM user_topics_team WHERE id = :id")
    abstract suspend fun getTeamById(id: Long): UserTopicsItemTeam?

    @Query("SELECT * FROM user_topics_team WHERE graphqlId = :id")
    abstract suspend fun getTeamByGraphId(id: String): UserTopicsItemTeam?

    @Query("SELECT * FROM user_topics_league WHERE id = :id")
    abstract suspend fun getLeagueById(id: Long): UserTopicsItemLeague?

    @Query("SELECT * FROM user_topics_author WHERE id = :id")
    abstract suspend fun getAuthorById(id: Long): UserTopicsItemAuthor?

    // TT Delete
    @Query("DELETE FROM user_topics_team WHERE id = :teamId")
    abstract fun removeTeam(teamId: Long)

    @Query("DELETE FROM user_topics_league WHERE id = :leagueId")
    abstract fun removeLeague(leagueId: Long)

    @Query("DELETE FROM user_topics_author WHERE id = :authorId")
    abstract fun removeAuthor(authorId: Long)

    @Query("DELETE FROM user_topics_team")
    abstract fun clearTeams()

    @Query("DELETE FROM user_topics_league")
    abstract fun clearLeagues()

    @Query("DELETE FROM user_topics_author")
    abstract fun clearAuthors()

    @Query("DELETE FROM user_topics_podcast")
    abstract fun clearPodcasts()

    @Transaction
    open fun updateFollowedTopics(userTopics: UserTopics) {
        updateAllTeamFollowStatus(false)
        updateAllLeagueFollowStatus(false)
        updateAllAuthorFollowStatus(false)

        insertTeams(userTopics.teams)
        insertLeagues(userTopics.leagues)
        insertAuthors(userTopics.authors)
    }

    @Query("UPDATE user_topics_league SET isFollowed = :isFollowed")
    abstract fun updateAllLeagueFollowStatus(isFollowed: Boolean)

    @Query("UPDATE user_topics_team SET isFollowed = :isFollowed")
    abstract fun updateAllTeamFollowStatus(isFollowed: Boolean)

    @Query("UPDATE user_topics_author SET isFollowed = :isFollowed")
    abstract fun updateAllAuthorFollowStatus(isFollowed: Boolean)

    @Transaction
    open fun clear() {
        clearTeams()
        clearLeagues()
        clearAuthors()
        clearPodcasts()
    }
}