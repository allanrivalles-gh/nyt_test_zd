package com.theathletic.manager

import androidx.databinding.ObservableBoolean
import com.jakewharton.rxrelay2.BehaviorRelay
import com.theathletic.entity.settings.UserTopics
import com.theathletic.entity.settings.UserTopicsBaseItem
import com.theathletic.entity.settings.UserTopicsItemLeague
import com.theathletic.entity.settings.UserTopicsItemTeam
import com.theathletic.extension.extLogError
import com.theathletic.repository.resource.Resource
import com.theathletic.repository.user.UserTopicsData
import com.theathletic.topics.LegacyUserTopicsManager
import com.theathletic.user.UserManager
import com.theathletic.utility.logging.ICrashLogHandler
import io.reactivex.disposables.Disposable
import java.util.Objects
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@Deprecated("Use TopicsRepository instead")
object UserTopicsManager : KoinComponent, LegacyUserTopicsManager {
    val isLoadingData = ObservableBoolean(false)
    private val userTopicsRelay = BehaviorRelay.createDefault<List<UserTopicsBaseItem>>(emptyList())
    var userTopicsValue: List<UserTopicsBaseItem>
        set(value) = userTopicsRelay.accept(value)
        get() = userTopicsRelay.value!!
    private var lastLoadHashCode: Int = 0
    private var userTopicsData: UserTopicsData = UserTopicsData()
    private var userTopicsDataDisposable: Disposable? = null
    private val crashLogHandler by inject<ICrashLogHandler>()

    init {
        userTopicsDataDisposable = userTopicsData.getDataObservable().subscribe(
            { response ->
                when (response.status) {
                    Resource.Status.LOADING, Resource.Status.SUCCESS -> response?.data?.let { newTopics ->
                        val oldSize = userTopicsValue.size
                        val userTopics = arrayListOf<UserTopicsBaseItem>()
                        userTopics.addAll(newTopics.teams)
                        userTopics.addAll(newTopics.leagues.sortedBy { it.displayOrder })
                        userTopics.addAll(newTopics.authors)

                        userTopicsValue = userTopics

                        isLoadingData.set(false)

                        // TT Reload data for all managers
                        val newHashCode = getFollowingTopicsHash()
                        if (lastLoadHashCode != newHashCode || oldSize == 0) {
                            lastLoadHashCode = newHashCode
                        }
                    }
                    Resource.Status.ERROR -> {
                        Timber.e("[UserTopicManager] ERROR: ${response.throwable}!")
                        response.throwable?.extLogError()
                        isLoadingData.set(false)
                    }
                }
            },
            {
                it.extLogError()
                crashLogHandler.trackException(
                    it,
                    "Error getting userTopicsData. " +
                        "userTopics.size: ${userTopicsValue.size}, "
                )
            }
        )
    }

    fun onDestroy() {
        Timber.v("[UserTopicsManager] onDestroy")
        isLoadingData.set(false)
        userTopicsValue = emptyList()
        userTopicsData.dispose()
        userTopicsDataDisposable?.dispose()
    }

    fun loadUserTopics() {
        Timber.v("[UserTopicsManager] loadUserTopics")
        if (!UserManager.isUserLoggedIn() || isLoadingData.get() || userTopicsData.isDataLoading)
            return

        isLoadingData.set(true)

        userTopicsData.load()
    }

    fun loadCachedUserTopics() {
        Timber.v("[UserTopicsManager] loadCachedUserTopics")
        if (!UserManager.isUserLoggedIn() || isLoadingData.get() || userTopicsData.isDataLoading)
            return

        isLoadingData.set(true)

        userTopicsData.loadOnlyCache()
    }

    fun hasNoTopics() = userTopicsValue.isEmpty()

    fun getFollowedTeamsList(): MutableList<UserTopicsItemTeam> = mutableListOf<UserTopicsItemTeam>().apply { addAll(userTopicsValue.filterIsInstance<UserTopicsItemTeam>().filter { it.isFollowed }.map { it.clone() as UserTopicsItemTeam }) }

    fun getFollowedLeaguesList(): MutableList<UserTopicsItemLeague> = mutableListOf<UserTopicsItemLeague>().apply { addAll(userTopicsValue.filterIsInstance<UserTopicsItemLeague>().filter { it.isFollowed }.map { it.clone() as UserTopicsItemLeague }) }

    override fun setFollowedTopics(topics: UserTopics) {
        userTopicsValue = userTopicsValue.toMutableList().apply {
            clear()
            addAll(topics.teams)
            addAll(topics.leagues)
            addAll(topics.authors)
        }
    }

    private fun getFollowingTopicsHash() = Objects.hash(*(getFollowedTeamsList().map { "${it.id}_${it.color}" }).toTypedArray())
}