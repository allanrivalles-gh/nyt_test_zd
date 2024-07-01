package com.theathletic.gamedetail.boxscore.ui.hockey

import com.google.common.truth.Truth
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.gamedetail.boxscore.ui.AMERICAN_FOOTBALL_SCORING_SUMMARY_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.BoxScoreFixture
import com.theathletic.gamedetail.boxscore.ui.GAME_DETAILS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.GAME_ODDS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.HOCKEY_RECENT_PLAYS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.HOCKEY_SCORING_SUMMARY_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.INJURY_REPORT_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.PLAYER_GRADES_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.RECENT_GAMES_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.RELATED_STORIES_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.SCORING_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.SEASON_STATS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.TEAM_LEADERS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.TEAM_STATS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.TICKETS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.TOP_COMMENTS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.TOP_PERFORMERS_MODULE_ID
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreGameDetailsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreGameOddsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreInjuryReportRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreLeadersRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScorePlayerGradeRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreRecentGamesRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreRelatedStoriesRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreScoringRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreSeasonStatsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreSlideStoriesRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTeamStatsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTicketsRenderers
import com.theathletic.gamedetail.boxscore.ui.common.BoxScoreTopCommentsRenderer
import com.theathletic.gamedetail.data.local.GameStatus
import com.theathletic.scores.data.SupportedLeagues
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class BoxScoreHockeyRendererTest {

    private lateinit var renderer: BoxScoreHockeyRenderer
    private var closeable: AutoCloseable? = null

    private val mockGameOddsRenderers: BoxScoreGameOddsRenderers = mock()
    private val mockRecentGamesRenderers: BoxScoreRecentGamesRenderers = mock()
    private val mockScoringRenderers: BoxScoreScoringRenderers = mock()
    private val mockGameDetailsRenderers: BoxScoreGameDetailsRenderers = mock()
    private val mockStatsRenderers: BoxScoreTeamStatsRenderers = mock()
    private val mockRelatedStoriesRenderers: BoxScoreRelatedStoriesRenderers = mock()
    private val mockLeadersRenderers: BoxScoreLeadersRenderers = mock()
    private val mockInjuryReportRenderers: BoxScoreInjuryReportRenderers = mock()
    private val mockSeasonStatsRenderers: BoxScoreSeasonStatsRenderers = mock()
    private val mockHockeyScoringPlaysRenderer: HockeyPlayByPlayRenderers = mock()
    private val mockPlayerGradeRenderers: BoxScorePlayerGradeRenderers = mock()
    private val mockTicketsRenderers: BoxScoreTicketsRenderers = mock()
    private val mockTopCommentsRenderer: BoxScoreTopCommentsRenderer = mock()
    private val mockSlideStoriesRenderers: BoxScoreSlideStoriesRenderers = mock()
    private val mockSupportedLeagues: SupportedLeagues = mock()
    private val mockFeatureSwitches: FeatureSwitches = mock()

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)

        renderer = BoxScoreHockeyRenderer(
            mockGameOddsRenderers,
            mockRecentGamesRenderers,
            mockRelatedStoriesRenderers,
            mockGameDetailsRenderers,
            mockScoringRenderers,
            mockStatsRenderers,
            mockLeadersRenderers,
            mockInjuryReportRenderers,
            mockSeasonStatsRenderers,
            mockHockeyScoringPlaysRenderer,
            mockPlayerGradeRenderers,
            mockTicketsRenderers,
            mockTopCommentsRenderer,
            mockSlideStoriesRenderers,
            mockSupportedLeagues,
            mockFeatureSwitches
        )

        whenever(mockFeatureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_TOP_COMMENTS)).thenReturn(true)

        whenever(mockSupportedLeagues.isCollegeLeague(any())).thenReturn(false)

        whenever(mockGameOddsRenderers.createGameOddsModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(GAME_ODDS_MODULE_ID))
        whenever(mockRecentGamesRenderers.createRecentGamesModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(RECENT_GAMES_MODULE_ID))
        whenever(mockScoringRenderers.createScoreTableModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(SCORING_MODULE_ID))
        whenever(mockGameDetailsRenderers.createGameDetailsModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(GAME_DETAILS_MODULE_ID))
        whenever(mockStatsRenderers.createTeamStatsModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(TEAM_STATS_MODULE_ID))
        whenever(mockRelatedStoriesRenderers.createRelatedStoriesModule(any(), any(), any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(RELATED_STORIES_MODULE_ID))
        whenever(mockLeadersRenderers.createTopPerformersModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(TOP_PERFORMERS_MODULE_ID))
        whenever(mockLeadersRenderers.createTeamLeadersModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(TEAM_LEADERS_MODULE_ID))
        whenever(mockInjuryReportRenderers.createInjuryReportModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(INJURY_REPORT_MODULE_ID))
        whenever(mockSeasonStatsRenderers.createSeasonStatsModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(SEASON_STATS_MODULE_ID))
        whenever(mockScoringRenderers.createAmericanFootballScoringSummaryModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(AMERICAN_FOOTBALL_SCORING_SUMMARY_MODULE_ID))
        whenever(mockHockeyScoringPlaysRenderer.createHockeyRecentPlaysModule(any(), any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(HOCKEY_RECENT_PLAYS_MODULE_ID))
        whenever(mockScoringRenderers.createHockeyScoringSummaryModule(any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(HOCKEY_SCORING_SUMMARY_MODULE_ID))
        whenever(mockPlayerGradeRenderers.createPlayerGradeCarousel(any(), any(), any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(PLAYER_GRADES_MODULE_ID))
        whenever(mockTicketsRenderers.createTicketsModule(any(), any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(TICKETS_MODULE_ID))
        whenever(mockTopCommentsRenderer.createTopCommentsModule(any(), any()))
            .thenReturn(BoxScoreFixture.TestFeedModule(TOP_COMMENTS_MODULE_ID))
    }

    @After
    fun tearDown() {
        closeable?.close()
    }

    @Test
    fun `when a game is scheduled, only this game's state feed modules are created and in the correct order`() {
        val list = renderer.renderModules(BoxScoreFixture.boxScoreStateFixture(GameStatus.SCHEDULED, Sport.HOCKEY))

        Truth.assertThat(list.map { it.moduleId }).isEqualTo(
            listOf(
                TICKETS_MODULE_ID,
                GAME_ODDS_MODULE_ID,
                TEAM_LEADERS_MODULE_ID,
                TOP_COMMENTS_MODULE_ID,
                SEASON_STATS_MODULE_ID,
                RECENT_GAMES_MODULE_ID,
                INJURY_REPORT_MODULE_ID,
                GAME_DETAILS_MODULE_ID,
                RELATED_STORIES_MODULE_ID,
            )
        )
    }

    @Test
    fun `when a game is in progress, only this game's state feed modules are created and in the correct order`() {
        val list = renderer.renderModules(BoxScoreFixture.boxScoreStateFixture(GameStatus.IN_PROGRESS, Sport.HOCKEY))

        Truth.assertThat(list.map { it.moduleId }).isEqualTo(
            listOf(
                SCORING_MODULE_ID,
                HOCKEY_RECENT_PLAYS_MODULE_ID,
                HOCKEY_SCORING_SUMMARY_MODULE_ID,
                TOP_COMMENTS_MODULE_ID,
                TOP_PERFORMERS_MODULE_ID,
                TEAM_STATS_MODULE_ID,
                INJURY_REPORT_MODULE_ID,
                GAME_DETAILS_MODULE_ID,
                RELATED_STORIES_MODULE_ID,
            )
        )
    }

    @Test
    fun `when a game is completed, only this game's state feed modules are created and in the correct order`() {
        val list = renderer.renderModules(BoxScoreFixture.boxScoreStateFixture(GameStatus.FINAL, Sport.HOCKEY))

        Truth.assertThat(list.map { it.moduleId }).isEqualTo(
            listOf(
                SCORING_MODULE_ID,
                HOCKEY_SCORING_SUMMARY_MODULE_ID,
                TOP_COMMENTS_MODULE_ID,
                TOP_PERFORMERS_MODULE_ID,
                TEAM_STATS_MODULE_ID,
                GAME_DETAILS_MODULE_ID,
                RELATED_STORIES_MODULE_ID,
            )
        )
    }
}