package com.theathletic.rooms

import com.theathletic.entity.room.LiveAudioRoomEntity
import com.theathletic.entity.room.SpeakingRequest
import com.theathletic.rooms.ui.LiveAudioEvent
import com.theathletic.rooms.ui.LiveAudioEventProducer
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import com.theathletic.user.IUserManager
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class LiveRoomRequestWatcherTest {

    private lateinit var watcher: LiveRoomRequestWatcher

    private lateinit var sharedFlow: MutableSharedFlow<LiveAudioEvent>
    private lateinit var eventProducer: LiveAudioEventProducer
    @Mock lateinit var userManager: IUserManager
    @Mock lateinit var roomsRepository: RoomsRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        sharedFlow = MutableSharedFlow(0, extraBufferCapacity = 10)
        eventProducer = LiveAudioEventProducer(sharedFlow)

        whenever(userManager.getCurrentUserId()).thenReturn(USER_ID.toLong())

        watcher = LiveRoomRequestWatcher(
            eventProducer,
            userManager,
            roomsRepository,
        )
    }

    @Test
    fun `two entities without requests do nothing`() = runTest {
        val testFlow = testFlowOf(sharedFlow)

        watcher.compare(NO_REQUESTS, NO_REQUESTS)

        assertStream(testFlow).hasNoEventReceived()
        verify(roomsRepository, never()).deleteSpeakingRequest(USER_ID, ROOM_ID)
        verify(roomsRepository, never()).deleteDemotionRequest(USER_ID, ROOM_ID)

        testFlow.finish()
    }

    @Test
    fun `empty to unapproved promotions does nothing`() = runTest {
        val testFlow = testFlowOf(sharedFlow)

        watcher.compare(NO_REQUESTS, UNAPPROVED_PROMOTION_REQUEST)

        assertStream(testFlow).hasNoEventReceived()
        verify(roomsRepository, never()).deleteSpeakingRequest(USER_ID, ROOM_ID)
        verify(roomsRepository, never()).deleteDemotionRequest(USER_ID, ROOM_ID)

        testFlow.finish()
    }

    @Test
    fun `unapproved promotion to approved promotion calls deleteSpeakingRequest and sends event`() =
        runTest {
            val testFlow = testFlowOf(sharedFlow)

            watcher.compare(UNAPPROVED_PROMOTION_REQUEST, APPROVED_PROMOTION_REQUEST)

            assertStream(testFlow)
                .hasReceivedExactly(LiveAudioEvent.SwapStageStatus(onStage = true, fromHost = true))
            verify(roomsRepository).deleteSpeakingRequest(USER_ID, ROOM_ID)
            verify(roomsRepository, never()).deleteDemotionRequest(USER_ID, ROOM_ID)

            testFlow.finish()
        }

    @Test
    fun `consecutive calls with approved promotions does not send events again`() = runTest {
        val testFlow = testFlowOf(sharedFlow)

        watcher.compare(UNAPPROVED_PROMOTION_REQUEST, APPROVED_PROMOTION_REQUEST)
        watcher.compare(APPROVED_PROMOTION_REQUEST, APPROVED_PROMOTION_REQUEST)

        assertStream(testFlow)
            .hasReceivedExactly(LiveAudioEvent.SwapStageStatus(onStage = true, fromHost = true))
        verify(roomsRepository, times(2)).deleteSpeakingRequest(USER_ID, ROOM_ID)
        verify(roomsRepository, never()).deleteDemotionRequest(USER_ID, ROOM_ID)

        testFlow.finish()
    }

    @Test
    fun `no demotion to demotion calls deleteDemotionRequest and sends event`() = runTest{
        val testFlow = testFlowOf(sharedFlow)

        watcher.compare(NO_REQUESTS, DEMOTION_REQUEST)

        assertStream(testFlow)
            .hasReceivedExactly(LiveAudioEvent.SwapStageStatus(onStage = false, fromHost = true))
        verify(roomsRepository, never()).deleteSpeakingRequest(USER_ID, ROOM_ID)
        verify(roomsRepository).deleteDemotionRequest(USER_ID, ROOM_ID)

        testFlow.finish()
    }

    @Test
    fun `send proper event when push notification status has changed`() = runTest{
        val testFlow = testFlowOf(sharedFlow)

        watcher.compare(PUSH_NOT_SENT, PUSH_SENT)

        assertStream(testFlow).hasReceivedExactly(LiveAudioEvent.AutoPushSent)

        testFlow.finish()
    }

    companion object {
        private const val USER_ID = "100"
        private const val ROOM_ID = "1"

        val NO_REQUESTS get() = LiveAudioRoomEntity(id = ROOM_ID)
        val UNAPPROVED_PROMOTION_REQUEST get() = LiveAudioRoomEntity(
            id = ROOM_ID,
            promotionRequests = listOf(SpeakingRequest(userId = USER_ID, approved = false))
        )
        val APPROVED_PROMOTION_REQUEST get() = LiveAudioRoomEntity(
            id = ROOM_ID,
            promotionRequests = listOf(SpeakingRequest(userId = USER_ID, approved = true))
        )
        val DEMOTION_REQUEST get() = LiveAudioRoomEntity(
            id = ROOM_ID,
            demotionRequests = listOf(SpeakingRequest(userId = USER_ID, approved = false))
        )

        val PUSH_NOT_SENT get() = LiveAudioRoomEntity(
            id = ROOM_ID,
            autoPushEnabled = true,
            autoPushSent = false,
        )
        val PUSH_SENT get() = LiveAudioRoomEntity(
            id = ROOM_ID,
            autoPushEnabled = true,
            autoPushSent = true,
        )
    }
}