package com.theathletic.main.ui.listen

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.entity.main.PodcastItem
import com.theathletic.podcast.data.PodcastRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainListenScreenViewModelTest {
    @get:Rule val coroutineTestRule = CoroutineTestRule()

    @Mock private lateinit var analytics: Analytics
    @Mock private lateinit var podcastRepository: PodcastRepository

    private lateinit var testViewModel: MainListenScreenViewModel
    private val followedPodcasts = MutableSharedFlow<List<PodcastItem>>(replay = 1)
    private val listenTabEventFlow = MutableSharedFlow<ListenTabEvent>(replay = 1)
    private val listenTabEventProducer = ListenTabEventProducer(listenTabEventFlow)

    private val listenTabEventConsumer: ListenTabEventConsumer = ListenTabEventConsumer(listenTabEventProducer)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(podcastRepository.followedPodcasts).thenReturn(followedPodcasts)
        testViewModel = MainListenScreenViewModel(
            listenTabEventConsumer,
            analytics,
            podcastRepository
        )
    }

    @Test
    fun `trackTabView calls analytics track method`() {
        testViewModel.trackTabView(0)

        verify(analytics).track(
            Event.Listen.View(
                element = "following"
            )
        )
    }

    @Test
    fun `trackItemClicked calls analytics track method`() {
        testViewModel.trackItemClicked(1)

        verify(analytics).track(
            Event.Podcast.Click(
                view = "listen",
                element = "feed_navigation",
                object_type = "discover"
            )
        )
    }

    @Test
    fun `calling trackItemClicked with invalid position does not call analytics track method`() {
        testViewModel.trackTabView(4)
        verifyZeroInteractions(analytics)
    }

    @Test
    fun `calling trackTabView with invalid position does not call analytics track method`() {
        testViewModel.trackTabView(-1)
        verifyZeroInteractions(analytics)
    }

    @Test
    fun `viewState emits following tab initially`() = runTest {
        assertEquals(ListenScreenViewState(CurrentlySelectedTab.FOLLOWING_TAB), testViewModel.viewState.value)
    }

    @Test
    fun `viewState emits discover tab when there are no followedPodcasts`() = runTest {
        assertEquals(ListenScreenViewState(CurrentlySelectedTab.FOLLOWING_TAB), testViewModel.viewState.value)

        followedPodcasts.emit(listOf())

        assertEquals(ListenScreenViewState(CurrentlySelectedTab.DISCOVER_TAB), testViewModel.viewState.value)
    }

    @Test
    fun `viewState does not emit discover tab when there are followedPodcasts`() = runTest {
        assertEquals(ListenScreenViewState(CurrentlySelectedTab.FOLLOWING_TAB), testViewModel.viewState.value)

        followedPodcasts.emit(listOf(PodcastItem()))

        assertEquals(ListenScreenViewState(CurrentlySelectedTab.FOLLOWING_TAB), testViewModel.viewState.value)
    }

    @Test
    fun `viewState emits discover tab when SwitchToDiscoverTab event is emitted`() = runTest {
        assertEquals(ListenScreenViewState(CurrentlySelectedTab.FOLLOWING_TAB), testViewModel.viewState.value)

        listenTabEventFlow.emit(ListenTabEvent.SwitchToDiscoverTab)

        assertEquals(ListenScreenViewState(CurrentlySelectedTab.DISCOVER_TAB), testViewModel.viewState.value)
    }
}