package com.theathletic.preferences.ui

import com.google.common.truth.Truth.assertThat
import com.theathletic.preferences.data.remote.NotificationNotUpdatedException
import com.theathletic.preferences.data.remote.SettingsApi
import com.theathletic.settings.data.SettingsRepository
import com.theathletic.test.runTest
import com.theathletic.user.IUserManager
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class UpdateTopSportNewsNotificationUseCaseTest {

    private val settingsApi = mockk<SettingsApi>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val userManager = mockk<IUserManager>(relaxed = true)
    private lateinit var topSportNewsNotificationUseCase: UpdateTopSportNewsNotificationUseCase

    @Before
    fun setUp() {
        topSportNewsNotificationUseCase = UpdateTopSportNewsNotificationUseCase(settingsRepository, userManager)
    }

    @Test
    fun `when a user opts in top sport news notification, preference is updated with success`() = runTest {
        coEvery { settingsRepository.updateTopSportsNewsNotification(true) } returns Unit
        val result = topSportNewsNotificationUseCase(true)
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `when a user opts in top sport news notification but result is false or exception, operation is a failure`() = runTest {
        coEvery { settingsRepository.updateTopSportsNewsNotification(any()) } throws NotificationNotUpdatedException()
        val result = topSportNewsNotificationUseCase(true)
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(NotificationNotUpdatedException::class.java)
    }
}