package com.theathletic.remoteconfig.local

import com.google.common.truth.Truth.assertThat
import com.theathletic.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class RemoteConfigDataSourceGetStringListTest {
    @Mock
    lateinit var mockDataSource: RemoteConfigDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `returns string parsed as json list of strings if valid`() = runTest {
        whenever(mockDataSource.getString(RemoteConfigEntry.PRIVACY_CCPA_SUPPORTED_STATES)).thenReturn(flowOf("[\"CA\", \"CO\", \"VA\"]"))
        assertThat(mockDataSource.getStringList(RemoteConfigEntry.PRIVACY_CCPA_SUPPORTED_STATES).first()).isEqualTo(listOf("CA", "CO", "VA"))
    }

    @Test
    fun `returns empty list if value is not a valid list of strings json`() = runTest {
        whenever(mockDataSource.getString(RemoteConfigEntry.PRIVACY_CCPA_SUPPORTED_STATES)).thenReturn(flowOf("1"))
        assertThat(mockDataSource.getStringList(RemoteConfigEntry.PRIVACY_CCPA_SUPPORTED_STATES).first()).isEqualTo(listOf<String>())
    }
}