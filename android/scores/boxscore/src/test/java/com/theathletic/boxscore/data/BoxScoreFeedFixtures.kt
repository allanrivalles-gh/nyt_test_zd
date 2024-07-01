package com.theathletic.boxscore.data

import com.theathletic.boxscore.data.local.Article
import com.theathletic.boxscore.data.local.BasicHeader
import com.theathletic.boxscore.data.local.BoxScore
import com.theathletic.boxscore.data.local.BoxScoreModules
import com.theathletic.boxscore.data.local.Items
import com.theathletic.boxscore.data.local.LatestNewsModule
import com.theathletic.boxscore.data.local.ModuleHeader
import com.theathletic.boxscore.data.local.PodcastEpisode
import com.theathletic.boxscore.data.local.Section
import com.theathletic.boxscore.data.local.SectionType
import com.theathletic.datetime.Datetime
import com.theathletic.podcast.ui.DownloadState
import com.theathletic.podcast.ui.PlaybackState

object BoxScoreFeedFixtures {

    val boxScore = BoxScoreFeedFixtures.boxScoreFeedFixture(
        id = "001",
        sections = listOf(
            BoxScoreFeedFixtures.boxScoreFeedSectionFixture(
                id = "002",
                type = SectionType.GAME,
                modules = listOf(
                    BoxScoreFeedFixtures.boxScoreFeedLatestNewsModuleFixture(
                        id = "003",
                        header = BoxScoreFeedFixtures.boxScoreFeedBasicHeaderFixture(
                            id = "004",
                            title = "Latest Newsroom"
                        ),
                        blocks = listOf(
                            BoxScoreFeedFixtures.boxScoreFeedArticleItemFixture(
                                id = "006",
                                title = "Sample podcast",
                            )
                        )
                    )
                )
            )
        )
    )

    val boxScoreTwoArticles = BoxScoreFeedFixtures.boxScoreFeedFixture(
        id = "001",
        sections = listOf(
            BoxScoreFeedFixtures.boxScoreFeedSectionFixture(
                id = "002",
                type = SectionType.GAME,
                modules = listOf(
                    BoxScoreFeedFixtures.boxScoreFeedLatestNewsModuleFixture(
                        id = "003",
                        header = BoxScoreFeedFixtures.boxScoreFeedBasicHeaderFixture(
                            id = "004",
                            title = "Latest Newsroom"
                        ),
                        blocks = listOf(
                            BoxScoreFeedFixtures.boxScoreFeedArticleItemFixture(
                                id = "006",
                                title = "Sample podcast",
                            ),
                            BoxScoreFeedFixtures.boxScoreFeedArticleItemFixture(
                                id = "007",
                                title = "Sample podcast",
                            )
                        )
                    )
                )
            )
        )
    )

    fun getBoxScoreArticleAndPodcast(
        playbackState: PlaybackState,
        downloadState: DownloadState,
        timeElapsed: Int
    ) = BoxScoreFeedFixtures.boxScoreFeedFixture(
        id = "001",
        sections = listOf(
            BoxScoreFeedFixtures.boxScoreFeedSectionFixture(
                id = "002",
                type = SectionType.GAME,
                modules = listOf(
                    BoxScoreFeedFixtures.boxScoreFeedLatestNewsModuleFixture(
                        id = "003",
                        header = BoxScoreFeedFixtures.boxScoreFeedBasicHeaderFixture(
                            id = "004",
                            title = "Latest Newsroom"
                        ),
                        blocks = listOf(
                            BoxScoreFeedFixtures.boxScoreFeedArticleItemFixture(
                                id = "006",
                                title = "Sample podcast",
                            ),
                            BoxScoreFeedFixtures.boxScoreFeedPodcastItemFixture(
                                id = "007",
                                title = "Sample podcast",
                                playbackState = playbackState,
                                timeElapsed = timeElapsed,
                                downloadState = downloadState
                            )
                        )
                    )
                )
            )
        )
    )

    fun boxScoreFeedFixture(
        id: String,
        sections: List<Section>
    ) = BoxScore(
        id = id,
        sections = sections
    )

    fun boxScoreFeedSectionFixture(
        id: String,
        type: SectionType,
        modules: List<BoxScoreModules>
    ) = Section(
        id = id,
        type = type,
        modules = modules
    )

    fun boxScoreFeedLatestNewsModuleFixture(
        id: String,
        header: ModuleHeader,
        blocks: List<Items>
    ) = LatestNewsModule(
        id = id,
        header = header,
        blocks = blocks
    )

    fun boxScoreFeedBasicHeaderFixture(
        id: String,
        title: String
    ) = BasicHeader(
        id = id,
        title = title
    )

    fun boxScoreFeedArticleItemFixture(
        id: String,
        title: String
    ) = Article(
        id = id,
        authors = null,
        title = title,
        commentCount = 10,
        description = null,
        imageUri = null,
        permalink = null,
        articleId = id,
        isBookmarked = false,
        isRead = false
    )

    private fun boxScoreFeedPodcastItemFixture(
        id: String,
        title: String,
        playbackState: PlaybackState,
        downloadState: DownloadState,
        timeElapsed: Int
    ) = PodcastEpisode(
        id = id,
        permalink = "",
        commentCount = 10,
        podcastId = id,
        title = title,
        clips = emptyList(),
        mp3Url = "",
        podcastTitle = null,
        publishedAt = Datetime(0L),
        imageUrl = null,
        finished = false,
        episodeId = id,
        description = null,
        duration = 0,
        timeElapsed = timeElapsed,
        playbackState = playbackState,
        downloadState = downloadState
    )
}