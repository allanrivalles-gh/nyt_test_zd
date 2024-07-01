package com.theathletic.followables.data

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.EmptyParams
import com.theathletic.followable.AuthorDao
import com.theathletic.followable.FollowableId
import com.theathletic.followable.FollowableType
import com.theathletic.followable.LeagueDao
import com.theathletic.followable.TeamDao
import com.theathletic.followables.data.domain.Followable
import com.theathletic.followables.data.remote.FollowableItemsFetcherV2
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.FollowableDao
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private typealias LeagueEnum = com.theathletic.entity.main.League
private typealias FollowableLocal = com.theathletic.followable.Followable

class FollowableRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val followableItemsFetcher: FollowableItemsFetcherV2,
    private val followableDao: FollowableDao,
    private val teamDao: TeamDao,
    private val leagueDao: LeagueDao,
    private val authorDao: AuthorDao,
    private val supportedLeagues: SupportedLeagues
) : CoroutineRepository {
    override val repositoryScope = CoroutineScope(dispatcherProvider.io + SupervisorJob())

    val followableStream: Flow<List<Followable>>
        get() = combine(
            teamDao.getTeams(), leagueDao.getLeagues(), authorDao.getAuthors()
        ) { teams, leagues, authors ->
            teams.map { it.toDomain() } + leagues.map { it.toDomain() } + authors.map { it.toDomain() }
        }

    val scoresFollowableStream: Flow<List<Followable>>
        get() = combine(
            teamDao.getTeamsWithScores(), leagueDao.getLeaguesWithScores()
        ) { teams, leagues ->
            teams.map { it.toDomain() } + leagues.map { it.toDomain() }
        }

    // todo : Migrate to domain objects .. return just a followable
    suspend fun getFollowable(id: FollowableId): FollowableLocal? {
        return when (id.type) {
            FollowableType.TEAM -> followableDao.getTeam(id)
            FollowableType.LEAGUE -> followableDao.getLeague(id)
            FollowableType.AUTHOR -> followableDao.getAuthor(id)
        }
    }

    suspend fun getTeam(teamId: FollowableId): TeamLocal? {
        return followableDao.getTeam(teamId)
    }

    suspend fun getAllTeams(): List<Followable.Team> {
        return followableDao.getAllTeams().map { it.toDomain() }
    }

    suspend fun getCountOfTeams(): Long {
        return followableDao.getCountOfTeams()
    }

    suspend fun getTeam(graphqlId: String): TeamLocal? {
        return followableDao.getTeam(graphqlId = graphqlId)
    }

    suspend fun getLeague(leagueId: FollowableId): LeagueLocal? {
        return followableDao.getLeague(leagueId)
    }

    fun fetchFollowableItems() {
        repositoryScope.launch {
            followableItemsFetcher.fetchRemote(EmptyParams)
        }
    }

    suspend fun getCollegeLeagues(): List<Followable.League> {
        return getFilteredLeagues(supportedLeagues.collegeLeagues).map { it.toDomain() }
    }

    suspend fun getFilteredLeagues(leagues: Set<LeagueEnum>) = followableDao.getFilteredLeagues(leagues).orEmpty()

    suspend fun getLeagueFromUrl(url: String): LeagueLocal? {
        return followableDao.getLeagueFromUrl(url)
    }

    suspend fun getTeamFromUrl(url: String): TeamLocal? {
        return followableDao.getTeamFromUrl(url)
    }

    suspend fun getAuthorFromUrl(url: String): AuthorLocal? {
        return followableDao.getAuthorFromUrl(url)
    }

    suspend fun getAuthorFromId(id: FollowableId): AuthorLocal? {
        return followableDao.getAuthor(id)
    }
}