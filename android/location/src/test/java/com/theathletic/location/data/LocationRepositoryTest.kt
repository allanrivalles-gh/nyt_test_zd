package com.theathletic.location.data

import com.theathletic.location.data.remote.CurrentLocationApi
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()
    @Mock private lateinit var mockLocationApi: CurrentLocationApi

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `country code from ip address with no context provided`() = runTest {
        whenever(mockLocationApi.getCurrentLocation()).thenReturn(sampleApiResponse)
        val locationRepository = LocationRepository(mockLocationApi, coroutineTestRule.dispatcherProvider)
        val cc = locationRepository.getCountryCode()
        assertEquals("US", cc)
    }

    @Test
    fun `state from ip address with no context provided`() = runBlocking {
        whenever(mockLocationApi.getCurrentLocation()).thenReturn(sampleApiResponse)
        val locationRepository = LocationRepository(mockLocationApi, coroutineTestRule.dispatcherProvider)
        val state = locationRepository.getState()
        assertEquals("CA", state)
    }

    companion object {
        private val sampleApiResponse = CurrentLocationResponse(
            country = "US",
            state = "CA"
        )
    }
}