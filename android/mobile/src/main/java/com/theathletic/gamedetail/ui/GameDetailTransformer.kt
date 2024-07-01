package com.theathletic.gamedetail.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ScrollToModule
import com.theathletic.boxscore.ui.GameDetailUi
import com.theathletic.boxscore.ui.TabModule
import com.theathletic.comments.analytics.CommentsAnalyticsPayload
import com.theathletic.comments.analytics.CommentsLaunchAction
import com.theathletic.entity.main.Sport
import com.theathletic.featureswitches.FeatureSwitch
import com.theathletic.featureswitches.FeatureSwitches
import com.theathletic.gamedetail.boxscore.ui.common.gradingIsActive
import com.theathletic.gamedetail.data.local.CoverageDataType
import com.theathletic.gamedetail.data.local.GameSummaryLocalModel
import com.theathletic.gamedetail.playergrades.ui.SportsWithPlayerGrades
import com.theathletic.scores.GameDetailTab
import com.theathletic.scores.GameDetailTabParamKey
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.Transformer
import com.theathletic.ui.asResourceString
import com.theathletic.ui.orShortDash
import com.theathletic.user.IUserManager
import com.theathletic.utility.orShortDash

class GameDetailTransformer @AutoKoin constructor(
    private val gameSummaryTeamRenderer: GameSummaryTeamRenderer,
    private val gameSummaryGameStatusRenderer: GameSummaryGameStatusRenderer,
    private val gameSummaryGameInfoRenderer: GameSummaryGameInfoRenderer,
    private val sportsWithPlayerGrades: SportsWithPlayerGrades,
    private val featureSwitches: FeatureSwitches,
    private val userManager: IUserManager
) :
    Transformer<GameDetailComposeState, GameDetailContract.ViewState> {

    override fun transform(data: GameDetailComposeState): GameDetailContract.ViewState {
        val gameDetailTabs = data.gameSummary.toTabs()
        return GameDetailContract.ViewState(
            toolbarLabel = data.gameSummary.formatToolbarLabel(),
            firstTeam = gameSummaryTeamRenderer.getTeamSummary(data.gameSummary, true, data.isLoaded, data.isFirstTeamNavigable),
            secondTeam = gameSummaryTeamRenderer.getTeamSummary(data.gameSummary, false, data.isLoaded, data.isSecondTeamNavigable),
            firstTeamStatus = gameSummaryTeamRenderer.getTeamStatus(data.gameSummary, true),
            secondTeamStatus = gameSummaryTeamRenderer.getTeamStatus(data.gameSummary, false),
            gameStatus = gameSummaryGameStatusRenderer.getGameStatus(data.gameSummary),
            gameInfo = gameSummaryGameInfoRenderer.getGameInfo(data.gameSummary),
            tabItems = gameDetailTabs,
            tabModules = gameDetailTabs.toTabModules(
                data.gameSummary,
                data.selectedTabExtras,
                data.scrollToModule,
                data.discussLaunchAction
            ),
            gameTitle = data.gameSummary?.toGameTitle(),
            shareLink = data.gameSummary?.permalink.orEmpty(),
            showShareLink = data.gameSummary?.permalink != null,
            selectedTab = setSelectedTab(data.selectedTab, gameDetailTabs),
        )
    }

    private fun GameSummaryLocalModel?.formatToolbarLabel(): ResourceString {
        return this?.sport?.let { sport ->
            when {
                isFirstTeamTbd && isSecondTeamTbd -> StringWithParams(
                    if (sport == Sport.SOCCER) {
                        R.string.game_detail_toolbar_soccer_with_both_team_tbc_label
                    } else {
                        R.string.game_details_toolbar_non_soccer_both_teams_tbc_label
                    }
                )
                isFirstTeamTbd -> StringWithParams(
                    if (sport == Sport.SOCCER) {
                        R.string.game_detail_toolbar_soccer_with_first_team_tbc_label
                    } else {
                        R.string.game_details_toolbar_non_soccer_first_team_tbc_label
                    },
                    secondTeam?.alias.orShortDash()
                )
                isSecondTeamTbd -> StringWithParams(
                    if (sport == Sport.SOCCER) {
                        R.string.game_detail_toolbar_soccer_with_second_team_tbc_label
                    } else {
                        R.string.game_details_toolbar_non_soccer_second_team_tbc_label
                    },
                    firstTeam?.alias.orShortDash()
                )
                else -> StringWithParams(
                    if (sport == Sport.SOCCER) {
                        R.string.game_detail_toolbar_soccer_label
                    } else {
                        R.string.game_details_toolbar_american_football_label
                    },
                    firstTeam?.alias.orShortDash(),
                    secondTeam?.alias.orShortDash()
                )
            }
        }.orShortDash()
    }

    private fun List<GameDetailUi.Tab>.toTabModules(
        gameSummary: GameSummaryLocalModel?,
        tabExtra: Map<GameDetailTabParamKey, String?>,
        scrollToModule: ScrollToModule,
        discussLaunchAction: CommentsLaunchAction?
    ): List<TabModule> {
        if (gameSummary == null) return emptyList()
        return mapNotNull { tab ->
            when (tab.type) {
                GameDetailTab.GAME -> GameTabModule(
                    gameId = gameSummary.id,
                    sport = gameSummary.sport,
                    scrollToModule = scrollToModule
                )
                GameDetailTab.PLAYER_STATS -> PlayerStatsTabModule(
                    gameId = gameSummary.id,
                    sport = gameSummary.sport,
                    isPostGame = gameSummary.isGameCompleted
                )
                GameDetailTab.PLAYS -> PlaysTabModule(
                    gameId = gameSummary.id,
                    sport = gameSummary.sport,
                    leagueId = gameSummary.league.id
                )
                GameDetailTab.DISCUSS -> DiscussTabModule(
                    gameId = gameSummary.id,
                    title = gameSummary.toGameTitle() ?: gameSummary.fallbackGameTitle,
                    commentsAnalyticsPayload = CommentsAnalyticsPayload(
                        leagueId = gameSummary.league.id,
                        gameStatusView = gameSummary.status.discussAnalyticsView
                    ),
                    hasTeamSpecificComments = gameSummary.areTeamSpecificCommentsEnabled(),
                    launchAction = discussLaunchAction
                )
                GameDetailTab.LIVE_BLOG -> LiveBlogTabModule(
                    gameId = gameSummary.id,
                    liveBlogId = gameSummary.liveBlog?.id,
                    initialPostId = tabExtra[GameDetailTabParamKey.PostId],
                    status = gameSummary.status,
                    leagueId = gameSummary.league.id,
                )
                GameDetailTab.GRADES -> PlayerGradeTabModule(
                    gameId = gameSummary.id,
                    sport = gameSummary.sport,
                    leagueId = gameSummary.league.id,
                    isGameInProgress = gameSummary.isGameInProgress
                )
                else -> null
            }
        }
    }

    private fun GameSummaryLocalModel?.toTabs() = when {
        this == null -> emptyList()
        sport == Sport.SOCCER -> toSoccerTabs(isGameInProgressOrCompleted)
        else -> toNonSoccerTabs(isGameInProgressOrCompleted)
    }

    private fun GameSummaryLocalModel.toSoccerTabs(gameLiveOrCompleted: Boolean): List<GameDetailUi.Tab> {
        return listOfNotNull(
            GameDetailUi.Tab(
                type = GameDetailTab.GAME,
                label = StringWithParams(R.string.game_detail_tab_match),
                showIndicator = false
            ),
            GameDetailUi.Tab(
                type = GameDetailTab.LIVE_BLOG,
                label = StringWithParams(R.string.game_detail_tab_box_score_live_blog),
                showIndicator = false
            ).takeIf { liveBlog != null },
            GameDetailUi.Tab(
                type = GameDetailTab.DISCUSS,
                label = StringWithParams(R.string.game_detail_tab_box_score_discuss),
                showIndicator = areCommentsDiscoverable,
            ).takeIf { shouldShowDiscussTab() },
            GameDetailUi.Tab(
                type = GameDetailTab.GRADES,
                label = StringWithParams(R.string.game_detail_tab_box_score_grades),
                showIndicator = false
            ).takeIf {
                sportsWithPlayerGrades.isSupported(Sport.SOCCER) && gradeStatus.gradingIsActive()
            },
            GameDetailUi.Tab(
                type = GameDetailTab.PLAYS,
                label = StringWithParams(
                    R.string.box_score_soccer_timeline_title_displayed
                ),
                showIndicator = false
            ).takeIf { coverage.supportsCoverageType(CoverageDataType.PLAYS) && gameLiveOrCompleted }
        )
    }

    private fun GameSummaryLocalModel.toNonSoccerTabs(gameLiveOrCompleted: Boolean): List<GameDetailUi.Tab> {
        return listOfNotNull(
            GameDetailUi.Tab(
                type = GameDetailTab.GAME,
                label = StringWithParams(R.string.game_detail_tab_box_score),
                showIndicator = false
            ),
            GameDetailUi.Tab(
                type = GameDetailTab.LIVE_BLOG,
                label = StringWithParams(R.string.game_detail_tab_box_score_live_blog),
                showIndicator = false
            ).takeIf { liveBlog != null },
            GameDetailUi.Tab(
                type = GameDetailTab.DISCUSS,
                label = StringWithParams(R.string.game_detail_tab_box_score_discuss),
                showIndicator = areCommentsDiscoverable
            ).takeIf { shouldShowDiscussTab() },
            GameDetailUi.Tab(
                type = GameDetailTab.GRADES,
                label = StringWithParams(R.string.game_detail_tab_box_score_grades),
                showIndicator = false
            ).takeIf {
                sportsWithPlayerGrades.isSupported(sport) && gradeStatus.gradingIsActive()
            },
            GameDetailUi.Tab(
                type = GameDetailTab.PLAYER_STATS,
                label = StringWithParams(R.string.game_detail_tab_box_score_stats),
                showIndicator = false
            ).takeIf {
                coverage.supportsCoverageType(CoverageDataType.PLAYER_STATS) && gameLiveOrCompleted
            },
            GameDetailUi.Tab(
                type = GameDetailTab.PLAYS,
                label = StringWithParams(
                    R.string.game_detail_tab_plays
                ),
                showIndicator = false
            ).takeIf { coverage.supportsCoverageType(CoverageDataType.PLAYS) && gameLiveOrCompleted }
        )
    }

    private fun List<CoverageDataType>.supportsCoverageType(type: CoverageDataType) =
        contains(type) || contains(CoverageDataType.ALL)

    private fun GameSummaryLocalModel.toGameTitle(): ResourceString? {
        return if (sport == Sport.SOCCER) {
            if (gameTitle == null) {
                StringWrapper(league.displayName)
            } else {
                StringWithParams(
                    R.string.game_detail_header_soccer_game_title,
                    league.displayName,
                    gameTitle.orEmpty()
                )
            }
        } else {
            gameTitle?.asResourceString()
        }
    }

    private val GameSummaryLocalModel.hasTeamSpecificNavigation: Boolean
        get() = coverage.contains(CoverageDataType.COMMENTS_NAVIGATION)

    private val isTeamSpecificCommentsEnabled = featureSwitches.isFeatureEnabled(FeatureSwitch.TEAM_SPECIFIC_COMMENTS)

    private fun GameSummaryLocalModel.areTeamSpecificCommentsEnabled(): Boolean {
        val useTeamSpecificComments = isTeamSpecificCommentsEnabled || hasTeamSpecificNavigation
        return useTeamSpecificComments && coverage.contains(CoverageDataType.TEAM_SPECIFIC_COMMENTS)
    }

    private fun GameSummaryLocalModel.shouldShowDiscussTab(): Boolean {
        val isBoxScoresDiscussionEnabled = featureSwitches.isFeatureEnabled(FeatureSwitch.BOX_SCORE_DISCUSS_TAB_ENABLED)
        val hasDiscussTab = coverage.contains(CoverageDataType.COMMENTS_NAVIGATION) ||
            (isTeamSpecificCommentsEnabled.not() && coverage.contains(CoverageDataType.TEAM_SPECIFIC_COMMENTS))

        return isBoxScoresDiscussionEnabled && userManager.isUserSubscribed() && hasDiscussTab
    }

    // Provides a fallback tab if the requested tab does not exist,
    // most likely when requested by another surface
    private fun setSelectedTab(
        selectedTab: GameDetailTab,
        tabList: List<GameDetailUi.Tab>
    ) = tabList.find { it.type == selectedTab }?.let { selectedTab } ?: GameDetailTab.GAME
}