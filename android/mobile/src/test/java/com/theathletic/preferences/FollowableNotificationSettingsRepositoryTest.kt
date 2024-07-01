package com.theathletic.preferences

import com.theathletic.followables.test.fixtures.leagueIdFixture
import com.theathletic.preferences.notifications.FollowableNotificationSettings
import com.theathletic.preferences.notifications.FollowableNotificationSettingsRepository
import com.theathletic.preferences.notifications.toLocal
import com.theathletic.settings.data.remote.SettingsRestApi
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.user.FollowableNotificationSettingsDao
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FollowableNotificationSettingsRepositoryTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock lateinit var settingsRestApi: SettingsRestApi
    @Mock lateinit var followableNotificationSettingsDao: FollowableNotificationSettingsDao

    private lateinit var repositpry: FollowableNotificationSettingsRepository

    @Before
    fun setUp() {
        repositpry = FollowableNotificationSettingsRepository(
            settingsRestApi,
            followableNotificationSettingsDao,
            coroutineTestRule.dispatcherProvider
        )
    }

    @Test
    fun `add push settings on update stories notifications with notify stories enabled`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notification = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = true, notifyGamesStart = false)

        repositpry.updateStoriesNotification(notification)

        verify(settingsRestApi).addPushSettings("league", "stories", 12)
    }

    @Test
    fun `remove push settings on update stories notifications with notify stories disabled`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notification = FollowableNotificationSettings(leagueId, notifyStories = false, notifyGames = true, notifyGamesStart = false)

        repositpry.updateStoriesNotification(notification)

        verify(settingsRestApi).removePushSettings("league", "stories", 12)
    }

    @Test
    fun `add push settings on update game notifications with notify games enabled`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notification = FollowableNotificationSettings(leagueId, notifyStories = false, notifyGames = true, notifyGamesStart = false)

        repositpry.updateGameNotification(notification)

        verify(settingsRestApi).addPushSettings("league", "games", 12)
    }

    @Test
    fun `remove push settings on update game notifications with notify games disabled`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notification = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = false, notifyGamesStart = false)

        repositpry.updateGameNotification(notification)

        verify(settingsRestApi).removePushSettings("league", "games", 12)
    }

    @Test
    fun `add push settings on update game start notifications with notify games start enabled`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notification = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = false, notifyGamesStart = true)

        repositpry.updateGameStartNotification(notification)

        verify(settingsRestApi).addPushSettings("league", "game_start", 12)
    }

    @Test
    fun `remove push settings on update game start notifications with notify games start disabled`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notification = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = false, notifyGamesStart = false)

        repositpry.updateGameStartNotification(notification)

        verify(settingsRestApi).removePushSettings("league", "game_start", 12)
    }

    @Test
    fun `update notification settings locally on update game notification`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notificationSettings = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = false, notifyGamesStart = false)

        repositpry.updateGameNotification(notificationSettings)

        verify(followableNotificationSettingsDao).updateSettings(notificationSettings.toLocal())
    }

    @Test
    fun `update notification settings locally on update stories notification`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notificationSettings = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = false, notifyGamesStart = false)

        repositpry.updateStoriesNotification(notificationSettings)

        verify(followableNotificationSettingsDao).updateSettings(notificationSettings.toLocal())
    }

    @Test
    fun `update notification settings locally on update game start notification`() = runTest {
        val leagueId = leagueIdFixture(id = "12")
        val notificationSettings = FollowableNotificationSettings(leagueId, notifyStories = true, notifyGames = false, notifyGamesStart = false)

        repositpry.updateGameStartNotification(notificationSettings)

        verify(followableNotificationSettingsDao).updateSettings(notificationSettings.toLocal())
    }
}