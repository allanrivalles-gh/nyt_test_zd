package com.theathletic.rooms.ui

import com.theathletic.audio.LiveAudioStageUser
import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

internal class LiveAudioRoomTransformerTest {

    lateinit var transformer: LiveAudioRoomTransformer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        transformer = LiveAudioRoomTransformer()
    }

    @Test
    fun `when user is not on stage, don't add them to stage row`() {
        val dataState = LiveAudioRoomState(
            liveAudioRoom = entity,
            currentUserId = USER_ID,
            usersOnStage = usersOnStage,
            isOnStage = false
        )

        val viewState = transformer.transform(dataState)

        assertFalse(viewState.isOnStage)

        assertEquals(expected = "3", actual = viewState.speakers[0].id)
        assertEquals(expected = "7", actual = viewState.speakers[1].id)
        assertEquals(expected = 2, actual = viewState.speakers.size)
    }

    @Test
    fun `when user is on stage, add them to stage row`() {
        val dataState = LiveAudioRoomState(
            liveAudioRoom = entity,
            currentUserId = USER_ID,
            usersOnStage = usersOnStage,
            isOnStage = true
        )

        val viewState = transformer.transform(dataState)

        assertTrue(viewState.isOnStage)

        assertEquals(expected = USER_ID, actual = viewState.speakers[0].id)
        assertEquals(expected = "3", actual = viewState.speakers[1].id)
        assertEquals(expected = "7", actual = viewState.speakers[2].id)
    }

    @Test
    fun `when user is not on stage, add them to the stage row`() {
        val dataState = LiveAudioRoomState(
            liveAudioRoom = entity,
            currentUserId = USER_ID,
            usersOnStage = usersOnStage,
            userInRoomDetails = details,
            isOnStage = false,
        )

        val viewState = transformer.transform(dataState)

        assertFalse(viewState.isOnStage)

        val audience = viewState.audience.filterIsInstance<LiveRoomUi.AudienceCell.User>()
        assertEquals(expected = "0", actual = audience[0].id)
        assertEquals(expected = "1", actual = audience[1].id)
        assertEquals(expected = "2", actual = audience[2].id)
        assertEquals(expected = "4", actual = audience[3].id)
    }

    @Test
    fun `when user is on stage, don't add them to the stage row`() {
        val dataState = LiveAudioRoomState(
            liveAudioRoom = entity,
            currentUserId = USER_ID,
            usersOnStage = usersOnStage,
            userInRoomDetails = details,
            isOnStage = true,
        )

        val viewState = transformer.transform(dataState)

        assertTrue(viewState.isOnStage)

        val audience = viewState.audience.filterIsInstance<LiveRoomUi.AudienceCell.User>()
        assertEquals(expected = "1", actual = audience[0].id)
        assertEquals(expected = "2", actual = audience[1].id)
        assertEquals(expected = "4", actual = audience[2].id)
        assertEquals(expected = "5", actual = audience[3].id)
    }

    companion object {
        const val USER_ID = "0"

        val entity = LiveAudioRoomEntity(usersInRoom = List(10) { it.toString() })

        val usersOnStage = setOf(
            LiveAudioStageUser(id = "3"),
            LiveAudioStageUser(id = "7"),
        )

        val details = List(10) { createUserDetails(it.toString()) }.associateBy { it.id }
    }
}

private fun createUserDetails(id: String) = LiveAudioRoomUserDetails(
    id = id,
    firstname = "firstname$id",
    lastname = "lastname$id",
    name = "firstname$id lastname$id",
    staffInfo = null,
)