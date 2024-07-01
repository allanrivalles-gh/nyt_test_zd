package com.theathletic.links

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.theathletic.analytics.data.ClickSource
import com.theathletic.article.data.ArticleRepository
import com.theathletic.billing.SpecialOffer
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.article.ArticleEntity
import com.theathletic.entity.main.League
import com.theathletic.feed.FeedType
import com.theathletic.followable.Followable
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.hub.HubTabType
import com.theathletic.links.deep.DeeplinkDestination
import com.theathletic.notification.NotificationOption
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.repository.user.AuthorLocal
import com.theathletic.repository.user.LeagueLocal
import com.theathletic.repository.user.TeamLocal
import com.theathletic.rooms.analytics.LiveRoomEntryPoint
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.data.SupportedLeagues
import com.theathletic.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
internal class LinkParserTest {

    @Mock lateinit var contextUpdater: AnalyticsContextUpdater
    @Mock lateinit var linkAnalytics: LinkAnalytics
    @Mock lateinit var articleRepository: ArticleRepository
    @Mock lateinit var supportedLeagues: SupportedLeagues
    @Mock lateinit var followableRepository: FollowableRepository

    private lateinit var linkHelper: LinkHelper
    private lateinit var linkParser: LinkParser

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(supportedLeagues.isSupportedId(7L)).thenReturn(true)
        mockFollowables()
        runBlocking {
            whenever(articleRepository.getArticle(any(), any())).thenReturn(ArticleEntity(postTypeId = 1L))
        }

        linkHelper = LinkHelper()
        linkParser = LinkParser(
            contextUpdater,
            linkAnalytics,
            articleRepository,
            followableRepository,
            linkHelper
        )
    }

    private fun mockFollowables() {
        runBlocking {
            whenever(followableRepository.getTeamFromUrl("bears")).thenReturn(
                TeamLocal(
                    Followable.Id("1", Followable.Type.TEAM),
                    name = "Chicago Bears",
                    shortName = "Bears",
                    searchText = "",
                    url = "bears",
                    colorScheme = TeamLocal.ColorScheme(),
                    displayName = "",
                    leagueId = Followable.Id(
                        League.NFL.leagueId.toString(),
                        Followable.Type.LEAGUE
                    )
                )
            )
            whenever(followableRepository.getLeagueFromUrl("nfl")).thenReturn(
                LeagueLocal(
                    Followable.Id("1", Followable.Type.LEAGUE),
                    name = "NFL",
                    shortName = "",
                    searchText = "",
                    league = League.NFL,
                    url = "nfl",
                    displayName = "NFL"
                )
            )
            whenever(followableRepository.getLeagueFromUrl("womens-football")).thenReturn(
                LeagueLocal(
                    Followable.Id("35", Followable.Type.LEAGUE),
                    name = "UK Women\'s Football",
                    shortName = "",
                    searchText = "",
                    league = League.NFL,
                    url = "womens-football",
                    displayName = "UK Women\'s Football"
                )
            )
            whenever(followableRepository.getAuthorFromUrl("shayna-goldman")).thenReturn(
                AuthorLocal(
                    Followable.Id("1", Followable.Type.AUTHOR),
                    name = "Shayna Goldman",
                    shortName = "",
                    searchText = "",
                    imageUrl = "",
                    url = "shayna-goldman"
                )
            )
        }
    }

    private val deeplinkExpectations = listOf(
        "theathletic://article/1234" to DeeplinkDestination.Article(1234L, ClickSource.DEEPLINK),
        "theathletic://article/1234?comment_id=12" to DeeplinkDestination.Comments(
            "1234",
            CommentsSourceType.ARTICLE,
            commentId = "12",
            ClickSource.DEEPLINK
        ),
        "theathletic://league/3" to DeeplinkDestination.StandaloneFeedLeague(3),
        "theathletic://league/3/schedule" to DeeplinkDestination.StandaloneFeedLeague(3, initialTab = HubTabType.Schedule),
        "theathletic://league/3/standings" to DeeplinkDestination.StandaloneFeedLeague(3, initialTab = HubTabType.Standings),
        "theathletic://league/3/bracket" to DeeplinkDestination.StandaloneFeedLeague(3, initialTab = HubTabType.Brackets),
        "theathletic://team/88" to DeeplinkDestination.StandaloneFeedTeam(88),
        "theathletic://team/88/feed" to DeeplinkDestination.StandaloneFeedTeam(88, initialTab = HubTabType.Home),
        "theathletic://team/88/schedule" to DeeplinkDestination.StandaloneFeedTeam(88, initialTab = HubTabType.Schedule),
        "theathletic://team/88/standings" to DeeplinkDestination.StandaloneFeedTeam(88, initialTab = HubTabType.Standings),
        "theathletic://team/88/stats" to DeeplinkDestination.StandaloneFeedTeam(88, initialTab = HubTabType.Stats),
        "theathletic://team/88/roster" to DeeplinkDestination.StandaloneFeedTeam(88, initialTab = HubTabType.Roster),
        "theathletic://author/34" to DeeplinkDestination.StandaloneFeedAuthor(34),
        "theathletic://category/66?title=warriors-greatest-hits" to
            DeeplinkDestination.StandaloneFeedCategory(66, "Warriors Greatest Hits"),
        "theathletic://category/66" to
            DeeplinkDestination.StandaloneFeedCategory(66, ""),
        "theathletic://headline" to DeeplinkDestination.None,
        "theathletic://headline/someHeadline" to
            DeeplinkDestination.Headline(
                "someHeadline",
                ClickSource.DEEPLINK.value
            ),
        "theathletic://reactions" to DeeplinkDestination.None,
        "theathletic://frontpage" to DeeplinkDestination.Frontpage,
        "theathletic://scores" to DeeplinkDestination.Scores,
        "theathletic://discussions" to DeeplinkDestination.None,
        "theathletic://discussions/123-someDiscussion" to DeeplinkDestination.Comments(
            "123",
            CommentsSourceType.DISCUSSION
        ),
        "theathletic://livediscussions" to DeeplinkDestination.None,
        "theathletic://livediscussions/123-someDiscussion" to DeeplinkDestination.Comments(
            "123",
            CommentsSourceType.QANDA
        ),
        "theathletic://podcasts" to DeeplinkDestination.PodcastFeed,
        "theathletic://podcast" to DeeplinkDestination.PodcastFeed,
        "theathletic://podcast/123-something" to DeeplinkDestination.Podcast(123),
        "theathletic://podcast/1" to DeeplinkDestination.Podcast(1),
        "theathletic://podcast/14?episode=188" to
            DeeplinkDestination.PodcastEpisode(-1, "14", 188, ""),
        "theathletic://podcast/14?episode=188&comment_id=9884838" to
            DeeplinkDestination.PodcastEpisode(-1, "14", 188, "9884838"),
        "theathletic://gift" to DeeplinkDestination.GiftPurchase,
        "theathletic://plans" to DeeplinkDestination.Plans(null, emptyMap()),
        "theathletic://plans/discount40" to
            DeeplinkDestination.Plans(SpecialOffer.Annual40, emptyMap()),
        "theathletic://share" to DeeplinkDestination.Share,
        "theathletic://settings" to DeeplinkDestination.Settings,
        "theathletic://register" to DeeplinkDestination.CreateAccount,
        "theathletic://login" to DeeplinkDestination.Login,
        "theathletic://boxscore" to DeeplinkDestination.None,
        "theathletic://boxscore/GameId9" to DeeplinkDestination.MatchCentre("GameId9"),
        "theathletic://boxscore/GameId5?comment_id=12345" to
            DeeplinkDestination.GameDetails("GameId5", "12345", GameDetailTab.DISCUSS),
        "theathletic://boxscore/J0bX2ANUoBZ0n5l5/season-stats" to
            DeeplinkDestination.MatchCentre("J0bX2ANUoBZ0n5l5", ScrollToModule.SEASON_STATS),
        "theathletic://live-blogs/" to DeeplinkDestination.None,
        "theathletic://live-blogs/liveBlogId" to DeeplinkDestination.LiveBlog("liveBlogId", postId = null),
        "theathletic://live-blogs/liveBlogId/postId" to DeeplinkDestination.LiveBlog("liveBlogId", postId = "postId"),
        "theathletic://live-rooms/" to DeeplinkDestination.None,
        "theathletic://live-rooms/liveRoomId" to DeeplinkDestination.LiveRoom(
            "liveRoomId",
            LiveRoomEntryPoint.DEEPLINK
        ),
        "theathletic://manage-teams" to DeeplinkDestination.ManageTopics(null),
        "theathletic://manage-teams/?type=league&id=1" to DeeplinkDestination.ManageTopics(UserTopicId.League(1)),
        "theathletic://feed/?league=1" to DeeplinkDestination.FeedSecondaryTab(FeedType.League(1)),
        "theathletic://feed/?team=5" to DeeplinkDestination.FeedSecondaryTab(FeedType.Team(5)),
        "theathletic://feed/?author=83" to DeeplinkDestination.FeedSecondaryTab(FeedType.Author(83)),
        "theathletic://" to DeeplinkDestination.OpenApp,
        "theathletic://invalid" to DeeplinkDestination.None,
        "theathletic://account_settings" to DeeplinkDestination.AccountSettings,
        "theathletic://email_settings" to DeeplinkDestination.EmailSettings,
        "theathletic://notification_settings" to DeeplinkDestination.NotificationSettings,
        "theathletic://notification_settings/enable/top-sports-news" to
            DeeplinkDestination.NotificationOptIn(NotificationOption.TOP_SPORTS_NEWS, true),
        "theathletic://boxscore/123?comment_id=12" to
            DeeplinkDestination.GameDetails(gameId = "123", commentId = "12", GameDetailTab.DISCUSS),
        "theathletic://boxscore/GameId9/timeline" to
            DeeplinkDestination.GameDetails(gameId = "GameId9", commentId = "", GameDetailTab.PLAYS),
        "theathletic://boxscore/GameId9/plays" to
            DeeplinkDestination.GameDetails(gameId = "GameId9", commentId = "", GameDetailTab.PLAYS),
        "theathletic://boxscore/GameId9/stats" to
            DeeplinkDestination.GameDetails(
                gameId = "GameId9",
                commentId = "",
                GameDetailTab.PLAYER_STATS
            ),
        "theathletic://boxscore/GameId9/grades" to
            DeeplinkDestination.GameDetails(gameId = "GameId9", commentId = "", GameDetailTab.GRADES),
        "theathletic://boxscore/GameId9/live-blog" to
            DeeplinkDestination.GameDetails(gameId = "GameId9", commentId = "", GameDetailTab.LIVE_BLOG),
        "theathletic://tag/womens-world-cup" to DeeplinkDestination.TagFeed("womens-world-cup"),
    )

    private val universalLinkExpectations = listOf(
        "https://www.theathletic.com/article/0" to DeeplinkDestination.Article(0, ClickSource.DEEPLINK),
        "https://www.theathletic.com/podcast" to DeeplinkDestination.PodcastFeed,
        "https://www.theathletic.com/podcast/123-something" to DeeplinkDestination.Podcast(123),
        "https://www.theathletic.com/podcasts" to DeeplinkDestination.PodcastFeed,
        "https://www.theathletic.com/podcast" to DeeplinkDestination.PodcastFeed,
        "https://theathletic.com/podcast/68-hear-that-podcast-growlin/?episode=337" to
            DeeplinkDestination.PodcastEpisode(episodeId = -1, podcastId = "68", episodeNumber = 337, ""),
        "https://theathletic.com/podcast/68-hear-that-podcast-growlin/?episode=337#comment-9795736" to
            DeeplinkDestination.PodcastEpisode(episodeId = -1, podcastId = "68", episodeNumber = 337, "9795736"),
        "https://www.theathletic.com/discussions" to DeeplinkDestination.None,
        "https://www.theathletic.com/discussions/123-someDiscussion" to DeeplinkDestination.Comments(
            "123",
            CommentsSourceType.DISCUSSION
        ),
        "https://www.theathletic.com/livediscussions" to DeeplinkDestination.None,
        "https://www.theathletic.com/livediscussions/123-someDiscussion" to DeeplinkDestination.Comments(
            "123",
            CommentsSourceType.QANDA
        ),
        "https://www.theathletic.com/headline" to DeeplinkDestination.None,
        "https://www.theathletic.com/headline/someHeadline" to
            DeeplinkDestination.Headline(
                "someHeadline",
                ClickSource.DEEPLINK.value
            ),
        "https://www.theathletic.com/frontpage" to DeeplinkDestination.Frontpage,
        "https://www.theathletic.com/reactions" to DeeplinkDestination.None,
        "https://www.theathletic.com/gift" to DeeplinkDestination.GiftPurchase,
        "https://www.theathletic.com/share" to DeeplinkDestination.Share,
        "https://www.theathletic.com/live-blogs" to DeeplinkDestination.None,
        "https://theathletic.com/live-blogs/2021-super-bowl-live-chiefs-vs-bucs-score-updates-kickoff-time-how-to-watch/c42HGMs1Rady" to
            DeeplinkDestination.LiveBlog("c42HGMs1Rady", postId = null),
        "https://theathletic.com/live-blogs/2021-super-bowl-live-chiefs-vs-bucs-score-updates-kickoff-time-how-to-watch/c42HGMs1Rady/" to
            DeeplinkDestination.LiveBlog("c42HGMs1Rady", postId = null),
        "https://staging2.theathletic.com/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/gwUTNynbrbCF" to
            DeeplinkDestination.LiveBlog("ysNcbsczUg7b", postId = "gwUTNynbrbCF"),
        "https://staging2.theathletic.com/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/gwUTNynbrbCF/" to
            DeeplinkDestination.LiveBlog("ysNcbsczUg7b", postId = "gwUTNynbrbCF"),
        "https://theathletic.com/live-blogs/this-bit-is-the-slug/match/KThRnc8VAiqwFWPz" to
            DeeplinkDestination.MatchCentre("KThRnc8VAiqwFWPz"),
        "https://theathletic.com/live-blogs/this-bit-is-the-slug/match/KThRnc8VAiqwFWPz/" to
            DeeplinkDestination.MatchCentre("KThRnc8VAiqwFWPz"),
        "https://theathletic.com/live-rooms/lets-talk-cleveland-browns/24jI9dvaOqBN" to
            DeeplinkDestination.LiveRoom("24jI9dvaOqBN", LiveRoomEntryPoint.UNIVERSAL_LINK),
        "https://theathletic.com/live-rooms/lets-talk-cleveland-browns/24jI9dvaOqBN/" to
            DeeplinkDestination.LiveRoom("24jI9dvaOqBN", LiveRoomEntryPoint.UNIVERSAL_LINK),
        "https://www.theathletic.com/culture" to DeeplinkDestination.StandaloneFeedLeague(39),
        "https://www.theathletic.com/manage-teams" to DeeplinkDestination.ManageTopics(null),
        "https://www.theathletic.com/manage-teams?type=league&id=1" to
            DeeplinkDestination.ManageTopics(UserTopicId.League(1)),
        "https://www.theathletic.com/team/bears" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears"),
        "https://www.theathletic.com/team/bears/feed" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Home),
        "https://www.theathletic.com/team/bears/schedule" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Schedule),
        "https://www.theathletic.com/team/bears/standings" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Standings),
        "https://www.theathletic.com/team/bears/stats" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Stats),
        "https://www.theathletic.com/team/bears/roster" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Roster),
        "https://www.theathletic.com/nfl/team/bears" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears"),
        "https://www.theathletic.com/nfl/team/bears/feed" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Home),
        "https://www.theathletic.com/nfl/team/bears/schedule" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Schedule),
        "https://www.theathletic.com/nfl/team/bears/standings" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Standings),
        "https://www.theathletic.com/nfl/team/bears/stats" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Stats),
        "https://www.theathletic.com/nfl/team/bears/roster" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Roster),
        "https://www.theathletic.com/nfl" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL"),
        "https://www.theathletic.com/nfl/schedule" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL", initialTab = HubTabType.Schedule),
        "https://www.theathletic.com/nfl/standings" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL", initialTab = HubTabType.Standings),
        "https://www.theathletic.com/nfl/bracket" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL", initialTab = HubTabType.Brackets),
        "https://www.theathletic.com/football/womens-football/" to
            DeeplinkDestination.StandaloneFeedLeague(35, "UK Women\'s Football"),
        "https://www.theathletic.com/author/shayna-goldman/" to
            DeeplinkDestination.StandaloneFeedAuthor(1, "Shayna Goldman"),
        "https://theathletic.com/college-football/game/notre-dame-fighting-irish-vs-ohio-state-buckeyes/GD4Pg0wXCblxlC2J/" to
            DeeplinkDestination.MatchCentre("GD4Pg0wXCblxlC2J"),
        "https://www.theathletic.com/mlb/game/discuss/philadelphia-phillies-vs-san-francisco-giants/keygf8z1ABDsjTXl" to
            DeeplinkDestination.GameDetails("keygf8z1ABDsjTXl", "", GameDetailTab.DISCUSS),
        "https://www.theathletic.com/mlb/game/discuss/philadelphia-phillies-vs-san-francisco-giants/keygf8z1ABDsjTXl/#comment-10745066" to
            DeeplinkDestination.GameDetails("keygf8z1ABDsjTXl", "10745066", GameDetailTab.DISCUSS),
        "https://www.theathletic.com/nfl/game/plays/kansas-city-chiefs-vs-philadelphia-eagles/AMmq6Q6QZ6It7dSg/" to
            DeeplinkDestination.GameDetails("AMmq6Q6QZ6It7dSg", "", GameDetailTab.PLAYS),
        "https://www.theathletic.com/nfl/game/timeline/kansas-city-chiefs-vs-philadelphia-eagles/AMmq6Q6QZ6It7dSg/" to
            DeeplinkDestination.GameDetails("AMmq6Q6QZ6It7dSg", "", GameDetailTab.PLAYS),
        "https://theathletic.com/mlb/game/stats/kansas-city-royals-vs-texas-rangers/b36hF4ci8xHFhA8i/" to
            DeeplinkDestination.GameDetails("b36hF4ci8xHFhA8i", "", GameDetailTab.PLAYER_STATS),
        "https://theathletic.com/football/player/joao-palhinha-V3auBkD0QaPo1HqE/" to
            DeeplinkDestination.Universal("https://theathletic.com/football/player/joao-palhinha-V3auBkD0QaPo1HqE/"),
        "https://theathletic.com/tag/for-cover-7/" to DeeplinkDestination.TagFeed("for-cover-7"),
    )

    private val nytAthleticLinksExpectation = listOf(
        "http://nytimes.com/athletic" to DeeplinkDestination.Universal("http://theathletic.com"),
        "https://www.nytimes.com/athletic/another" to DeeplinkDestination.Universal("https://theathletic.com/another"),
        "https://nytimes.com/athletic/yet-another" to DeeplinkDestination.Universal("https://theathletic.com/yet-another"),
        "https://nytimes.com/athletic/article/0" to DeeplinkDestination.Article(0, ClickSource.DEEPLINK),
        "https://nytimes.com/athletic/podcast" to DeeplinkDestination.PodcastFeed,
        "https://nytimes.com/athletic/podcast/123-something" to DeeplinkDestination.Podcast(123),
        "https://nytimes.com/athletic/podcasts" to DeeplinkDestination.PodcastFeed,
        "https://nytimes.com/athletic/podcast/68-hear-that-podcast-growlin/?episode=337" to
            DeeplinkDestination.PodcastEpisode(episodeId = -1, podcastId = "68", episodeNumber = 337, ""),
        "https://nytimes.com/athletic/podcast/68-hear-that-podcast-growlin/?episode=337#comment-9795736" to
            DeeplinkDestination.PodcastEpisode(episodeId = -1, podcastId = "68", episodeNumber = 337, "9795736"),
        "https://nytimes.com/athletic/discussions" to DeeplinkDestination.None,
        "https://nytimes.com/athletic/discussions/123-someDiscussion" to DeeplinkDestination.Comments("123", CommentsSourceType.DISCUSSION),
        "https://nytimes.com/athletic/livediscussions" to DeeplinkDestination.None,
        "https://nytimes.com/athletic/livediscussions/123-someDiscussion" to DeeplinkDestination.Comments("123", CommentsSourceType.QANDA),
        "https://nytimes.com/athletic/headline" to DeeplinkDestination.None,
        "https://nytimes.com/athletic/headline/someHeadline" to
            DeeplinkDestination.Headline(
                "someHeadline",
                ClickSource.DEEPLINK.value
            ),
        "https://nytimes.com/athletic/frontpage" to DeeplinkDestination.Frontpage,
        "https://nytimes.com/athletic/reactions" to DeeplinkDestination.None,
        "https://nytimes.com/athletic/gift" to DeeplinkDestination.GiftPurchase,
        "https://nytimes.com/athletic/share" to DeeplinkDestination.Share,
        "https://nytimes.com/athletic/live-blogs" to DeeplinkDestination.None,
        "https://nytimes.com/athletic/live-blogs/2021-super-bowl-live-chiefs-vs-bucs-score-updates-kickoff-time-how-to-watch/c42HGMs1Rady" to
            DeeplinkDestination.LiveBlog("c42HGMs1Rady", postId = null),
        "https://nytimes.com/athletic/live-blogs/2021-super-bowl-live-chiefs-vs-bucs-score-updates-kickoff-time-how-to-watch/c42HGMs1Rady/" to
            DeeplinkDestination.LiveBlog("c42HGMs1Rady", postId = null),
        "https://nytimes.com/athletic/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/gwUTNynbrbCF" to
            DeeplinkDestination.LiveBlog("ysNcbsczUg7b", postId = "gwUTNynbrbCF"),
        "https://nytimes.com/athletic/live-blogs/college-football-recruiting-news-rankings-live/ysNcbsczUg7b/gwUTNynbrbCF/" to
            DeeplinkDestination.LiveBlog("ysNcbsczUg7b", postId = "gwUTNynbrbCF"),
        "https://nytimes.com/athletic/live-blogs/this-bit-is-the-slug/match/KThRnc8VAiqwFWPz" to
            DeeplinkDestination.MatchCentre("KThRnc8VAiqwFWPz"),
        "https://nytimes.com/athletic/live-blogs/this-bit-is-the-slug/match/KThRnc8VAiqwFWPz/" to
            DeeplinkDestination.MatchCentre("KThRnc8VAiqwFWPz"),
        "https://nytimes.com/athletic/live-rooms/lets-talk-cleveland-browns/24jI9dvaOqBN" to
            DeeplinkDestination.LiveRoom("24jI9dvaOqBN", LiveRoomEntryPoint.UNIVERSAL_LINK),
        "https://nytimes.com/athletic/live-rooms/lets-talk-cleveland-browns/24jI9dvaOqBN/" to
            DeeplinkDestination.LiveRoom("24jI9dvaOqBN", LiveRoomEntryPoint.UNIVERSAL_LINK),
        "https://nytimes.com/athletic/culture" to DeeplinkDestination.StandaloneFeedLeague(39),
        "https://nytimes.com/athletic/manage-teams" to DeeplinkDestination.ManageTopics(null),
        "https://nytimes.com/athletic/manage-teams?type=league&id=1" to
            DeeplinkDestination.ManageTopics(UserTopicId.League(1)),
        "https://nytimes.com/athletic/team/bears" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears"),
        "https://nytimes.com/athletic/team/bears/feed" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Home),
        "https://nytimes.com/athletic/team/bears/schedule" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Schedule),
        "https://nytimes.com/athletic/team/bears/standings" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Standings),
        "https://nytimes.com/athletic/team/bears/stats" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Stats),
        "https://nytimes.com/athletic/team/bears/roster" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Roster),
        "https://nytimes.com/athletic/nfl/team/bears/feed" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Home),
        "https://nytimes.com/athletic/nfl/team/bears/schedule" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Schedule),
        "https://nytimes.com/athletic/nfl/team/bears/standings" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Standings),
        "https://nytimes.com/athletic/nfl/team/bears/stats" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Stats),
        "https://nytimes.com/athletic/nfl/team/bears/roster" to
            DeeplinkDestination.StandaloneFeedTeam(1, "Chicago Bears", initialTab = HubTabType.Roster),
        "https://nytimes.com/athletic/nfl" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL"),
        "https://nytimes.com/athletic/nfl/schedule" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL", initialTab = HubTabType.Schedule),
        "https://nytimes.com/athletic/nfl/standings" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL", initialTab = HubTabType.Standings),
        "https://nytimes.com/athletic/nfl/bracket" to
            DeeplinkDestination.StandaloneFeedLeague(1, "NFL", initialTab = HubTabType.Brackets),
        "https://nytimes.com/athletic/football/womens-football/" to
            DeeplinkDestination.StandaloneFeedLeague(35, "UK Women\'s Football"),
        "https://nytimes.com/athletic/author/shayna-goldman/" to
            DeeplinkDestination.StandaloneFeedAuthor(1, "Shayna Goldman"),
        "https://nytimes.com/athletic/college-football/game/notre-dame-fighting-irish-vs-ohio-state-buckeyes/GD4Pg0wXCblxlC2J/" to
            DeeplinkDestination.MatchCentre("GD4Pg0wXCblxlC2J"),
        "https://nytimes.com/athletic/mlb/game/discuss/philadelphia-phillies-vs-san-francisco-giants/keygf8z1ABDsjTXl" to
            DeeplinkDestination.GameDetails("keygf8z1ABDsjTXl", "", GameDetailTab.DISCUSS),
        "https://nytimes.com/athletic/mlb/game/discuss/philadelphia-phillies-vs-san-francisco-giants/keygf8z1ABDsjTXl/#comment-10745066" to
            DeeplinkDestination.GameDetails("keygf8z1ABDsjTXl", "10745066", GameDetailTab.DISCUSS),
        "https://nytimes.com/athletic/nfl/game/plays/kansas-city-chiefs-vs-philadelphia-eagles/AMmq6Q6QZ6It7dSg/" to
            DeeplinkDestination.GameDetails("AMmq6Q6QZ6It7dSg", "", GameDetailTab.PLAYS),
        "https://nytimes.com/athletic/nfl/game/timeline/kansas-city-chiefs-vs-philadelphia-eagles/AMmq6Q6QZ6It7dSg/" to
            DeeplinkDestination.GameDetails("AMmq6Q6QZ6It7dSg", "", GameDetailTab.PLAYS),
        "https://nytimes.com/athletic/mlb/game/stats/kansas-city-royals-vs-texas-rangers/b36hF4ci8xHFhA8i/" to
            DeeplinkDestination.GameDetails("b36hF4ci8xHFhA8i", "", GameDetailTab.PLAYER_STATS),
        "https://nytimes.com/athletic/football/player/joao-palhinha-V3auBkD0QaPo1HqE/" to
            DeeplinkDestination.Universal("https://theathletic.com/football/player/joao-palhinha-V3auBkD0QaPo1HqE/"),
        "https://nytimes.com/athletic/tag/for-cover-7/" to DeeplinkDestination.TagFeed("for-cover-7"),
    )

    @Test
    fun `deep links resolve to destinations`() {
        deeplinkExpectations.map { (link, expected) ->
            runTest { validateLink(link, expected) }
        }
    }

    @Test
    fun `universal links resolve to destinations`() {
        universalLinkExpectations.map { (link, expected) ->
            runTest { validateLink(link, expected) }
        }
    }

    @Test
    fun `nyt athletic links resolve to destinations`() {
        nytAthleticLinksExpectation.map { (link, expected) ->
            runTest { validateLink(link, expected) }
        }
    }

    private suspend inline fun <reified T> validateLink(sourceLink: String, expectedDest: T) {
        val uri = Uri.parse(sourceLink)
        val destination = linkParser.parseDeepLink(uri)
        assertThat(destination as T).isEqualTo(expectedDest)
    }
}