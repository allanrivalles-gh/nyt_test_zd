package com.theathletic.repository.user

import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.settings.UserTopicsItemAuthor
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemPodcast
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.extension.applySchedulers
import com.theathletic.repository.resource.NetworkBoundResource
import com.theathletic.settings.data.remote.SettingsRestApi
import com.theathletic.user.UserManager
import io.reactivex.Maybe
import java.util.Arrays
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class UserTopicsData : NetworkBoundResource<UserTopics>(), KoinComponent {
    private val roomDao by inject<UserTopicsDao>()
    private val settingsRestApi by inject<SettingsRestApi>()

    init {
        callback = object : Callback<UserTopics> {
            override fun saveCallResult(response: UserTopics) {
                roomDao.insert(response.teams, response.leagues, response.authors, response.podcasts)
                Timber.v("[ROOM] Saved user topics")
            }

            override fun loadFromDb(): Maybe<UserTopics> {
                val teamsSource = roomDao.getUserTopicsTeams().applySchedulers()
                val leaguesSource = roomDao.getUserTopicsLeagues().applySchedulers()
                val authorsSource = roomDao.getUserTopicsAuthors().applySchedulers()
                val podcastsSource = roomDao.getUserTopicsPodcasts().applySchedulers()

                return Maybe.zip(Arrays.asList(teamsSource, leaguesSource, authorsSource, podcastsSource)) { result ->
                    val teams = (result[0] as List<*>).filterIsInstance<UserTopicsItemTeam>() as MutableList<UserTopicsItemTeam>
                    val leagues = (result[1] as List<*>).filterIsInstance<UserTopicsItemLeague>() as MutableList<UserTopicsItemLeague>
                    val authors = (result[2] as List<*>).filterIsInstance<UserTopicsItemAuthor>() as MutableList<UserTopicsItemAuthor>
                    val podcasts = (result[3] as List<*>).filterIsInstance<UserTopicsItemPodcast>() as MutableList<UserTopicsItemPodcast>
                    Timber.v("[ROOM] Loaded User Topics Teams with size: ${teams.size}")
                    Timber.v("[ROOM] Loaded User Topics Leagues with size: ${leagues.size}")
                    Timber.v("[ROOM] Loaded User Topics Authors with size: ${authors.size}")
                    Timber.v("[ROOM] Loaded User Topics Podcasts with size: ${podcasts.size}")

                    UserTopics(teams, leagues, authors, podcasts)
                }
            }

            override fun createNetworkCall(): Maybe<UserTopics> = settingsRestApi.getUserTopics(
                UserManager.getCurrentUserId()
            )

            override fun mapData(data: UserTopics?): UserTopics? {
                return data
            }
        }
    }
}