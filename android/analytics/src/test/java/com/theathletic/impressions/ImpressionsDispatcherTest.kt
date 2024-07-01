package com.theathletic.impressions

import com.theathletic.datetime.Chronos
import io.mockk.MockK
import io.mockk.MockKDsl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

class ImpressionsDispatcherTest {

    private val chronos = mockk<Chronos>()
    private lateinit var impressionsDispatcher: ImpressionsDispatcher

    @Before
    fun setUp() {
        impressionsDispatcher = ImpressionsDispatcher(chronos)
    }

    @Test
    fun `dispatch impression when for PARTIAL visibility after a VISIBILE registry with 500+ms interval`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 1000)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.PARTIAL, impressionPayloadFixture())

        verify { impressionEventListener(impressionEventFixture(startTime = 500, endTime = 1000)) }
    }

    @Test
    fun `dispatch impression when for GONE visibility after a VISIBILE registry with 500+ms interval`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 1000)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.GONE, impressionPayloadFixture())

        verify { impressionEventListener(impressionEventFixture(startTime = 500, endTime = 1000)) }
    }

    @Test
    fun `dispatch impression when for PARTIAL visibility after a VISIBLE register if the custom interval was reached`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        impressionsDispatcher.listenToImpressionEvents(
            interval = 800.milliseconds,
            onImpression = impressionEventListener
        )
        every { chronos.currentTimeMs }.returnsMany(400, 1200)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.PARTIAL, impressionPayloadFixture())

        verify { impressionEventListener(impressionEventFixture(startTime = 400, endTime = 1200)) }
    }

    @Test
    fun `do not dispatch or register impression payload if the component is VISIBLE already`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 1000)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())

        verify(exactly = 0) { impressionEventListener(impressionEventFixture(startTime = 500, endTime = 1000)) }
    }

    @Test
    fun `do not dispatch impression if the component was never VISIBILE before`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 1000)

        impressionsDispatcher.registerImpression(Visibility.PARTIAL, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.GONE, impressionPayloadFixture())

        verify(exactly = 0) { impressionEventListener(impressionEventFixture(startTime = 500, endTime = 1000)) }
    }

    @Test
    fun `do not dispatch impression if the VISIBLE registry interval is less than 500ms`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 999)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.GONE, impressionPayloadFixture())

        verify(exactly = 0) { impressionEventListener(impressionEventFixture(startTime = 500, endTime = 999)) }
    }

    @Test
    fun `do not dispatch if the component leave the screen before 500ms interval`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        val impressionPayload = impressionPayloadFixture()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 700, 1000)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.PARTIAL, impressionPayload)
        impressionsDispatcher.registerImpression(Visibility.GONE, impressionPayload)

        verify(exactly = 0) { impressionEventListener(impressionEventFixture(startTime = 500, endTime = 1000)) }
    }

    @Test
    fun `do not dispatch again if the component did not GONE after an event dispatch interval`() {
        val impressionEventListener = relaxedMock<(ImpressionEvent) -> Unit>()
        val impressionPayload = impressionPayloadFixture()
        impressionsDispatcher.listenToImpressionEvents(onImpression = impressionEventListener)
        every { chronos.currentTimeMs }.returnsMany(500, 1000, 1500, 2000, 2500, 3000)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.PARTIAL, impressionPayload)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayloadFixture())
        impressionsDispatcher.registerImpression(Visibility.GONE, impressionPayload)

        impressionsDispatcher.registerImpression(Visibility.VISIBLE, impressionPayload)
        impressionsDispatcher.registerImpression(Visibility.PARTIAL, impressionPayload)

        verifySequence {
            impressionEventListener(impressionEventFixture(startTime = 500, endTime = 1000))
            impressionEventListener(impressionEventFixture(startTime = 2500, endTime = 3000))
        }
    }

    inline fun <reified T : Any> relaxedMock(
        name: String? = null,
        vararg moreInterfaces: KClass<*>,
        block: T.() -> Unit = {}
    ): T = MockK.useImpl {
        MockKDsl.internalMockk(
            name,
            relaxed = true,
            *moreInterfaces,
            relaxUnitFun = true,
            block = block
        )
    }
}