package com.theathletic.remoteconfig

import com.google.common.truth.Truth.assertThat
import com.theathletic.remoteconfig.local.RemoteConfigDataSource
import com.theathletic.remoteconfig.local.RemoteConfigEntry
import com.theathletic.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class RemoteConfigRepositoryTest {

    @Mock
    lateinit var mockDataSource: RemoteConfigDataSource

    private lateinit var remoteConfigRepo: RemoteConfigRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        remoteConfigRepo = RemoteConfigRepository(mockDataSource)
    }

    @Test
    fun `returns string split on underscore from androidForceUpdateVersions value`() = runTest {
        whenever(mockDataSource.getString(RemoteConfigEntry.FORCE_UPDATE_VERSIONS)).thenReturn(flowOf("1_2"))
        assertThat(remoteConfigRepo.androidForceUpdateVersions.first()).isEqualTo(listOf("1", "2"))
    }
}