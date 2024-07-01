package com.theathletic.scores.data

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.api.Optional
import com.benasher44.uuid.Uuid
import com.google.common.truth.Truth.assertThat
import com.theathletic.GetLeagueScheduleQuery
import com.theathletic.GetScheduleFeedGroupQuery
import com.theathletic.GetTeamScheduleQuery
import com.theathletic.entity.main.League
import com.theathletic.scores.data.di.scheduleFixture
import com.theathletic.scores.data.di.scheduleGroupFixture
import com.theathletic.scores.data.local.ScheduleLocalDataSource
import com.theathletic.scores.data.remote.ScheduleApi
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import com.theathletic.type.LeagueCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ScheduleRepositoryTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var mockLocalDataSource: ScheduleLocalDataSource
    @Mock private lateinit var mockScheduleApi: ScheduleApi
    private lateinit var repository: ScheduleRepository

    private val filterId = "filterId"
    private val key = "key"
    private val groupId = "groupId"

    @BeforeTest
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        repository = ScheduleRepository(
            coroutineTestRule.dispatcherProvider,
            mockScheduleApi,
            mockLocalDataSource
        )
    }

    @Test
    fun `exception is thrown when getting the team schedule throws an exception`() = runTest {
        whenever(mockScheduleApi.getTeamSchedule("teamId")).then { throw Exception("API Failed") }

        try {
            repository.fetchTeamSchedule("teamId")
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Error fetching Team Schedule for team Id: teamId, message: API Failed")
        }
    }

    @Test
    fun `exception is thrown when getting the league schedule throws an exception`() = runTest {
        whenever(mockScheduleApi.getLeagueSchedule(League.NFL)).then { throw Exception("API Failed") }

        try {
            repository.fetchLeagueSchedule(League.NFL)
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Error fetching League Schedule for league: NFL, message: API Failed")
        }
    }

    @Test
    fun `exception is thrown when getting the team schedule response has errors`() = runTest {
        val response = ApolloResponse.Builder<GetTeamScheduleQuery.Data>(
            requestUuid = Uuid.randomUUID(),
            data = null,
            operation = GetTeamScheduleQuery("", "")
        )
            .errors(
                listOf(
                    Error("Error 1", null, null, null, null),
                    Error("Error 2", null, null, null, null),
                    Error("Error 3", null, null, null, null)
                )
            ).build()

        whenever(mockScheduleApi.getTeamSchedule("teamId")).thenReturn(response)

        try {
            repository.fetchTeamSchedule("teamId")
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Error fetching Team Schedule for team Id: teamId, message: Error 1\nError 2\nError 3")
        }
    }

    @Test
    fun `exception is thrown when getting the league schedule response has errors`() = runTest {
        val response = ApolloResponse.Builder<GetLeagueScheduleQuery.Data>(
            requestUuid = Uuid.randomUUID(),
            data = null,
            operation = GetLeagueScheduleQuery("", LeagueCode.nfl)
        )
            .errors(listOf(Error("Server Error", null, null, null, null)))
            .build()

        whenever(mockScheduleApi.getLeagueSchedule(League.NFL)).thenReturn(response)

        try {
            repository.fetchLeagueSchedule(League.NFL)
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Error fetching League Schedule for league: NFL, message: Server Error")
        }
    }

    @Test
    fun `success is return when getting the team schedule response with no errors`() = runTest {
        val response = ApolloResponse.Builder<GetTeamScheduleQuery.Data>(
            data = null,
            requestUuid = Uuid.randomUUID(),
            operation = GetTeamScheduleQuery("", "")
        )
            .build()

        whenever(mockScheduleApi.getTeamSchedule("teamId")).thenReturn(response)

        try {
            repository.fetchTeamSchedule("teamId")
        } catch (e: Exception) {
            fail("Exception should NOT have been thrown")
        }
    }

    @Test
    fun `success is return when getting the league schedule response with no errors`() = runTest {
        val response = ApolloResponse.Builder(GetLeagueScheduleQuery("", LeagueCode.nfl), UUID.randomUUID(), null)
            .build()

        whenever(mockScheduleApi.getLeagueSchedule(League.NFL)).thenReturn(response)

        try {
            repository.fetchLeagueSchedule(League.NFL)
        } catch (e: Exception) {
            fail("Exception should NOT have been thrown")
        }
    }

    @Test
    fun `exception thrown when requesting a single feed grouping of a schedule with an exception`() = runTest {
        whenever(mockScheduleApi.getScheduleFeedGroup("groupId", "filterId")).then { throw Exception("API Failed") }

        try {
            repository.fetchScheduleFeedGroup("entityId", "groupId", "filterId")
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Error fetching Schedule Group for entity: entityId, id: groupId, message: API Failed")
        }
    }

    @Test
    fun `exception thrown when requesting a single feed grouping of a schedule with an error`() = runTest {
        val response = ApolloResponse.Builder(
            GetScheduleFeedGroupQuery("", "", Optional.Absent),
            UUID.randomUUID(),
            null
        )
            .errors(listOf(Error("Server Error", null, null, null, null)))
            .build()

        whenever(mockScheduleApi.getScheduleFeedGroup("groupId", "filterId")).thenReturn(response)

        try {
            repository.fetchScheduleFeedGroup("entityId", "groupId", "filterId")
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Error fetching Schedule Group for entity: entityId, id: groupId, message: Server Error")
        }
    }

    @Test
    fun `success is return when requesting a single feed grouping of a schedule with no errors`() = runTest {
        val response = ApolloResponse.Builder(
            GetScheduleFeedGroupQuery("", "", Optional.Absent),
            UUID.randomUUID(),
            null
        )
            .build()

        whenever(mockScheduleApi.getScheduleFeedGroup("groupId", "filterId")).thenReturn(response)

        try {
            repository.fetchScheduleFeedGroup("entityId", "groupId", "filterId")
        } catch (e: Exception) {
            fail("Exception should NOT have been thrown")
        }
    }

    @Test
    fun `exception thrown when requesting to subscribe to schedule updates and there is an error`() = runTest {
        whenever(mockScheduleApi.getScheduleUpdates(listOf("id"))).then { throw Exception("Subscription Failed") }

        try {
            repository.subscribeToScheduleUpdates("key", "groupId", listOf("id"))
            fail("Exception should have been thrown")
        } catch (e: Exception) {
            assertThat(e.message)
                .isEqualTo("Subscribing to schedule updates failed for ids: [id] with error: Subscription Failed")
        }
    }

    @Test
    fun `Group is returned if filter Id does match with stored groups filter`() = runTest {
        val group1 = scheduleGroupFixture(filterId, groupId)
        val group2 = scheduleGroupFixture(filterId, groupId.plus("2"))

        val flow = MutableStateFlow(scheduleFixture(listOf(group1, group2)))
        whenever(mockLocalDataSource.observeItem(key)).doReturn(flow)

        val result = repository.getScheduleFeedGroup(key, groupId, filterId)
        assertThat(group1).isEqualTo(result)
    }

    @Test
    fun `No group is returned if filter Id does not match with stored groups filter`() = runTest {
        val group1 = scheduleGroupFixture(filterId.plus("123"), groupId)
        val group2 = scheduleGroupFixture(filterId, groupId.plus("2"))

        val flow = MutableStateFlow(scheduleFixture(listOf(group1, group2)))
        whenever(mockLocalDataSource.observeItem(key)).doReturn(flow)

        val result = repository.getScheduleFeedGroup(key, groupId, filterId)
        assertThat(result).isEqualTo(null)
    }
}