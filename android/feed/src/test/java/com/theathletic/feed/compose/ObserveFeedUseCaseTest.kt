package com.theathletic.feed.compose

import com.google.common.truth.Truth.assertThat
import com.theathletic.device.IsTabletProvider
import com.theathletic.entity.authentication.UserData
import com.theathletic.feed.compose.data.Article
import com.theathletic.feed.compose.data.FeedRepository
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.data.HeadlineLayout
import com.theathletic.feed.compose.data.HeroListLayout
import com.theathletic.feed.compose.data.Layout
import com.theathletic.feed.compose.data.ListLayout
import com.theathletic.repository.user.IUserDataRepository
import com.theathletic.test.CoroutineTestRule
import com.theathletic.test.assertStream
import com.theathletic.test.runTest
import com.theathletic.test.testFlowOf
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ObserveFeedUseCaseTest {

    @get:Rule val coroutineTestRule = CoroutineTestRule()

    private lateinit var observeFeedUseCase: ObserveFeedUseCase

    @Mock private lateinit var feedRepository: FeedRepository
    @Mock private lateinit var isTabletProvider: IsTabletProvider
    @Mock private lateinit var userDataRepository: IUserDataRepository

    private val feedRequest = FeedRequest(FeedType.DISCOVER)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(userDataRepository.userDataFlow).thenReturn(flowOf(UserData()))
        observeFeedUseCase = ObserveFeedUseCase(feedRepository, userDataRepository, isTabletProvider)
    }

    @Test
    fun `for you layouts are merged when they are followed by another for you`() = runTest {
        val items = listOf(articleFixture())
        val layouts = listOf(
            layoutFixture(type = Layout.Type.THREE_HERO_CURATION, items = items),
            layoutFixture(type = Layout.Type.FOR_YOU, items = items),
            layoutFixture(type = Layout.Type.FOR_YOU, items = items),
            layoutFixture(type = Layout.Type.FOR_YOU, items = items)
        )
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(feedFixture(layouts = layouts)))

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts).hasSize(2)
            assertThat(feed.layouts[0].type).isEqualTo(Layout.Type.THREE_HERO_CURATION)
            assertThat(feed.layouts[1].type).isEqualTo(Layout.Type.FOR_YOU)
            assertThat(feed.layouts[1].items).hasSize(3)
        }
    }

    @Test
    fun `one content curated followed by a four content curated merges to a topper for phones`() = runTest {
        val layouts = listOf(
            layoutFixture(type = Layout.Type.ONE_CONTENT_CURATED, items = listOf(articleFixture())),
            layoutFixture(type = Layout.Type.FOUR_CONTENT_CURATED, items = listOf(articleFixture(), articleFixture())),
            layoutFixture(type = Layout.Type.FIVE_HERO_CURATION),
        )
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(feedFixture(layouts = layouts)))
        whenever(isTabletProvider.isTablet).thenReturn(false)

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts).hasSize(2)
            assertThat(feed.layouts[0].type).isEqualTo(Layout.Type.TOPPER)
            assertThat(feed.layouts[0].items).hasSize(3)
            assertThat(feed.layouts[1].type).isEqualTo(Layout.Type.FIVE_HERO_CURATION)
        }
    }

    @Test
    fun `one content curated followed by a four content curated do not becomes a topper hero for tablets`() = runTest {
        val layouts = listOf(
            layoutFixture(type = Layout.Type.ONE_CONTENT_CURATED),
            layoutFixture(type = Layout.Type.FOUR_CONTENT_CURATED)
        )
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(feedFixture(layouts = layouts)))
        whenever(isTabletProvider.isTablet).thenReturn(true)

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts).hasSize(2)
            assertThat(feed.layouts[0].type).isEqualTo(Layout.Type.ONE_CONTENT_CURATED)
            assertThat(feed.layouts[1].type).isEqualTo(Layout.Type.FOUR_CONTENT_CURATED)
        }
    }

    @Test
    fun `two content curated are mapped to a Hero List Layout`() = runTest {
        val layouts = listOf(layoutFixture(type = Layout.Type.TWO_CONTENT_CURATED))
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(feedFixture(layouts = layouts)))

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts[0]).isInstanceOf(HeroListLayout::class.java)
        }
    }

    @Test
    fun `highlight_three_content are mapped to a For You List Layout`() = runTest {
        val layouts = listOf(layoutFixture(type = Layout.Type.HIGHLIGHT_THREE_CONTENT))
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(feedFixture(layouts = layouts)))

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts[0]).isInstanceOf(ListLayout::class.java)
        }
    }

    @Test
    fun `limit headline count to eight`() = runTest {
        val items = createHeadlines(10)
        val layouts = listOf(layoutFixture(type = Layout.Type.HEADLINE, items = items))
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(feedFixture(layouts = layouts)))

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts[0]).isInstanceOf(HeadlineLayout::class.java)
            assertThat(feed.layouts[0].items.size).isEqualTo(8)
        }
    }

    @Test
    fun `update article items when theres a content metadata update for the given article id`() = runTest {
        val articleIdOne = 123L
        val articleIdTwo = 456L

        val items = listOf(
            articleFixture(id = articleIdOne.toString(), isRead = false), articleFixture(id = articleIdTwo.toString(), isSaved = false)
        )

        val layouts = feedFixture(layouts = listOf(layoutFixture(items = items)))
        whenever(feedRepository.observeFeed(feedRequest)).thenReturn(flowOf(layouts))
        whenever(userDataRepository.isItemRead(articleIdOne)).thenReturn(true)
        whenever(userDataRepository.isItemBookmarked(articleIdTwo)).thenReturn(true)

        val testFlow = testFlowOf(observeFeedUseCase(feedRequest))

        assertStream(testFlow).lastEvent { feed ->
            assertThat(feed.layouts[0].items[0] as Article).isEqualTo(articleFixture(id = articleIdOne.toString(), isRead = true))
            assertThat(feed.layouts[0].items[1] as Article).isEqualTo(articleFixture(id = articleIdTwo.toString(), isSaved = true))
        }
    }

    private fun createHeadlines(count: Int): MutableList<Layout.Item> {
        val items = mutableListOf<Layout.Item>()
        for (n in 1..count) {
            items.add(headlineFixture(id = "headline$n"))
        }
        return items
    }
}