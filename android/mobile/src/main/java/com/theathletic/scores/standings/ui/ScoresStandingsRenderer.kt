package com.theathletic.scores.standings.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.StandingsGroupTitleModule
import com.theathletic.boxscore.ui.modules.StandingsTableModule
import com.theathletic.boxscore.ui.standings.RelegationItem
import com.theathletic.entity.main.League
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.hub.game.ui.toRecentForm
import com.theathletic.scores.standings.data.local.RelegationStatus
import com.theathletic.scores.standings.data.local.Standing
import com.theathletic.scores.standings.data.local.StandingRangeClosedSegment
import com.theathletic.scores.standings.data.local.StandingRangeFromSegment
import com.theathletic.scores.standings.data.local.StandingRangeToSegment
import com.theathletic.scores.standings.data.local.StandingSegment
import com.theathletic.scores.standings.data.local.StandingsGroup
import com.theathletic.scores.standings.data.local.StandingsGrouping
import com.theathletic.scores.standings.data.local.StandingsSegmentType
import com.theathletic.themes.AthColor
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString
import com.theathletic.ui.binding.asParameterized
import com.theathletic.ui.modules.SpacingModuleV2
import com.theathletic.ui.widgets.buttons.RoundedDropDownMenuModule
import com.theathletic.ui.widgets.buttons.ToggleButtonGroupModule
import com.theathletic.ui.widgets.tabs.ScrollableTabsModule
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.orShortDash

class ScoresStandingsRenderer @AutoKoin constructor(
    private val localeUtility: LocaleUtility
) {

    fun renderStandingsModules(
        selectedGroupIndex: Int,
        selectedGroupName: String?,
        groupings: List<StandingsGrouping>,
        league: League,
        highlightedTeamId: String?,
        nonNavigableTeams: List<String>,
        isStandingsFilteringEnabled: Boolean
    ): List<FeedModuleV2> {
        if (groupings.isEmpty()) return emptyList()
        return mutableListOf<FeedModuleV2>().apply {
            if (isStandingsFilteringEnabled.not()) {
                add(renderGroupTabModule(groupings, selectedGroupIndex))
            }
            selectedGroupName?.let {
                add(
                    RoundedDropDownMenuModule(
                        id = "RoundedDropDownMenu",
                        selectedOption = selectedGroupName,
                        options = groupings.map { it.groupLabel }
                    )
                )
            }

            addAll(
                renderGroupTableModules(
                    groupings = groupings,
                    selectedGroupIndex = selectedGroupIndex,
                    league = league,
                    highlightedTeamId = highlightedTeamId,
                    nonNavigableTeams = nonNavigableTeams,
                    showHeader = shouldShowHeader(
                        isStandingsFilteringEnabled,
                        selectedGroupName,
                        selectedGroupIndex
                    )
                )
            )
        }
    }

    private fun shouldShowHeader(
        isStandingsFilteringEnabled: Boolean,
        selectedGroupName: String?,
        selectedGroupIndex: Int
    ) = when {
        isStandingsFilteringEnabled.not() -> true
        isStandingsFilteringEnabled && selectedGroupIndex == 0 -> true
        isStandingsFilteringEnabled && selectedGroupName == null -> true
        else -> false
    }

    private fun renderGroupTabModule(
        groupings: List<StandingsGrouping>,
        selectedGroupIndex: Int,
    ): FeedModuleV2 {
        val buttons = groupings.map { it.groupLabel }

        return when {
            buttons.size < 2 -> SpacingModuleV2(
                id = "ScoreStandings",
                color = SpacingModuleV2.Background.StandardForegroundColor,
                height = SpacingModuleV2.Height.Medium
            )

            buttons.size < 4 -> ToggleButtonGroupModule(
                id = "ScoreStandings",
                buttons = buttons,
                selectedGroupIndex = selectedGroupIndex
            )

            else -> ScrollableTabsModule(
                id = "ScoreStandings",
                tabLabels = buttons,
                selectedTabIndex = selectedGroupIndex,
                bottomPadding = 12.dp
            )
        }
    }

    @SuppressWarnings("NestedBlockDepth")
    private fun renderGroupTableModules(
        groupings: List<StandingsGrouping>,
        selectedGroupIndex: Int,
        league: League,
        highlightedTeamId: String?,
        nonNavigableTeams: List<String>,
        showHeader: Boolean
    ): List<FeedModuleV2> {
        if (groupings.isEmpty()) return emptyList()
        var currentTitle = ""
        val modules = mutableListOf<FeedModuleV2>()

        groupings[selectedGroupIndex].groups.forEach { group ->
            if (group.standings.isNotEmpty()) {
                val (title, module) = renderGroupTitleModule(
                    id = selectedGroupIndex.toString(),
                    proposedTitle = groupings[selectedGroupIndex].toTitle(group.id),
                    currentTitle = currentTitle
                )
                currentTitle = title // Save title for next iteration
                if (showHeader) module?.let { modules.add(it) }

                modules.add(
                    StandingsTableModule(
                        id = group.id,
                        teamsColumn = renderTeamsForAGroupModule(
                            group = group,
                            showSeeding = group.hasSeeding(league),
                            showRank = groupings[selectedGroupIndex].showRank,
                            highlightedTeamId = highlightedTeamId,
                            nonNavigableTeams = nonNavigableTeams
                        ),
                        statsColumns = renderStatisticsForAGroupModule(
                            group = group,
                            highlightedTeamId = highlightedTeamId,
                            sport = league.sport
                        )
                    )
                )
            }
        }
        return modules
    }

    private fun renderGroupTitleModule(
        id: String,
        proposedTitle: String,
        currentTitle: String
    ): Pair<String, StandingsGroupTitleModule?> {
        if (proposedTitle != currentTitle) {
            return if (proposedTitle.isNotEmpty()) {
                Pair(proposedTitle, StandingsGroupTitleModule(id, proposedTitle))
            } else {
                Pair(proposedTitle, null)
            }
        }
        return Pair(currentTitle, null)
    }

    private fun renderTeamsForAGroupModule(
        group: StandingsGroup,
        showSeeding: Boolean,
        showRank: Boolean,
        highlightedTeamId: String?,
        nonNavigableTeams: List<String>
    ): List<StandingsTableModule.TeamColumnItem> {
        return mutableListOf<StandingsTableModule.TeamColumnItem>().apply {
            add(
                StandingsTableModule.TeamColumnItem.Category(
                    label = group.name?.let { StringWrapper(it) }
                        ?: StringWithParams(R.string.scores_standings_team_header_default)
                )
            )
            addAll(
                group.standings.map { standing ->
                    StandingsTableModule.TeamColumnItem.Team(
                        id = standing.team.id,
                        alias = standing.team.alias,
                        logos = standing.team.logos,
                        ranking = standing.rank.toString(),
                        showRanking = showRank,
                        relegationColor = standing.relegationStatus.toColor,
                        seeding = standing.team.currentRanking?.toString().orEmpty(),
                        showSeeding = showSeeding,
                        highlighted = standing.showAsStandingsHighlightedTeam(highlightedTeamId),
                        dividerType = group.toDividerType(standing.rank),
                        isFollowable = nonNavigableTeams.any { it == standing.team.id }.not()
                    )
                }
            )
        }
    }

    private fun renderStatisticsForAGroupModule(
        group: StandingsGroup,
        highlightedTeamId: String?,
        sport: Sport,
    ): List<List<StandingsTableModule.StatsColumnItem>> {

        val table = mutableListOf<List<StandingsTableModule.StatsColumnItem>>()

        group.columns.entries.forEach { entry ->
            table.add(
                mutableListOf<StandingsTableModule.StatsColumnItem>().apply {
                    add(StandingsTableModule.StatsColumnItem.Label(entry.value))
                    group.standings.forEach { standing ->
                        when (val valueType = standing.toValueType(entry.key)) {
                            StandingsTableModule.StatsColumnItem.ValueType.RecentForm -> {
                                val isReversed = localeUtility.isUnitedStatesOrCanada().not()
                                add(
                                    StandingsTableModule.StatsColumnItem.RecentForm(
                                        lastSix = standing.lastSix?.toRecentForm(isReversed) ?: emptyList(),
                                        isReversed = isReversed,
                                        highlighted = standing.showAsStandingsHighlightedTeam(highlightedTeamId),
                                        dividerType = group.toDividerType(standing.rank)
                                    )
                                )
                            }
                            else -> add(
                                StandingsTableModule.StatsColumnItem.Statistic(
                                    value = standing.toValue(entry.key),
                                    highlighted = standing.showAsStandingsHighlightedTeam(highlightedTeamId),
                                    dividerType = group.toDividerType(standing.rank),
                                    valueType = valueType
                                )
                            )
                        }
                    }
                }
            )
        }

        if (sport == Sport.SOCCER) {
            val fromIndex = findIndexOfLabel(table, "PTS")
            val toIndex = findIndexOfLabel(table, "GP").plus(1)
            if (fromIndex > 1) {
                table.moveItem(fromIndex, toIndex)
            }
        }
        return table
    }

    fun renderRelegationLegendItemsV2(
        league: League,
        groupings: List<StandingsGrouping>
    ): List<RelegationItem> {
        return groupings.flatMap { group ->
            group.groups.flatMap { it.standings.map { standing -> standing.relegationStatus } }
        }
            .distinct()
            .filterNot { it == RelegationStatus.UNKNOWN }
            .map { relegationStatus ->
                RelegationItem(
                    label = StringWithParams(relegationStatus.label),
                    color = relegationStatus.toColor,
                )
            }
    }

    private fun StandingsGroup.toDividerType(ranking: Int): StandingsTableModule.DividerType {
        // current XML Standings adds the divider to the top of the row where the new compose
        // version adds to the bottom. Need to correct for that until this old XML can be removed
        // todo (Mark): Correct ranking by removing the +1 when going 100% compose. Also correct in needsDivider()
        return when {
            showSolidPlayoffDivider(ranking + 1) -> StandingsTableModule.DividerType.SolidPlayoff
            showDottedPlayoffDivider(ranking + 1) -> StandingsTableModule.DividerType.DottedPlayOff
            else -> StandingsTableModule.DividerType.Standard
        }
    }

    private fun Standing.toValue(label: String): String {
        return when (label) {
            "points" -> points.orShortDash()
            "played" -> played.orShortDash()
            "won" -> won.orShortDash()
            "lost" -> lost.orShortDash()
            "drawn" -> drawn.orShortDash()
            "for" -> pointsFor.orShortDash()
            "against" -> pointsAgainst.orShortDash()
            "difference" -> difference.orShortDash()
            "win_pct" -> winPct.orShortDash()
            "div_record" -> divRecord.orShortDash()
            "conf_record" -> confRecord.orShortDash()
            "streak" -> streak.orShortDash()
            "lost_overtime" -> lostOvertime.orShortDash()
            "away_record" -> awayRecord.orShortDash()
            "home_record" -> homeRecord.orShortDash()
            "last_ten_record" -> lastTenRecord.orShortDash()
            "games_behind" -> gamesBehind.orShortDash()
            "elimination_number" -> eliminationNumber.orShortDash()
            else -> "--"
        }
    }

    private fun Standing.toValueType(label: String): StandingsTableModule.StatsColumnItem.ValueType {
        return when (label) {
            "streak" -> {
                val value = streak.orShortDash()
                when {
                    value.startsWith("W") -> StandingsTableModule.StatsColumnItem.ValueType.Win
                    value.startsWith("L") -> StandingsTableModule.StatsColumnItem.ValueType.Loss
                    else -> StandingsTableModule.StatsColumnItem.ValueType.Default
                }
            }
            "difference" -> {
                difference?.toIntOrNull()?.let {
                    when {
                        it > 0 -> StandingsTableModule.StatsColumnItem.ValueType.GreaterThan
                        it < 0 -> StandingsTableModule.StatsColumnItem.ValueType.LessThan
                        else -> StandingsTableModule.StatsColumnItem.ValueType.Default
                    }
                } ?: StandingsTableModule.StatsColumnItem.ValueType.Default
            }
            "form" -> StandingsTableModule.StatsColumnItem.ValueType.RecentForm
            else -> StandingsTableModule.StatsColumnItem.ValueType.Default
        }
    }

    private fun List<StandingsGrouping>.filterCollegeStandings(
        teamId: String,
    ) = filter { it.isTeamInGrouping(teamId) }

    private fun StandingsGrouping.isTeamInGrouping(teamId: String): Boolean {
        return groups.firstOrNull { group ->
            group.standings.firstOrNull{ it.team.id == teamId } != null
        } != null
    }

    private var lastHeader = String()

    fun renderGroupingsList(
        groupings: List<StandingsGrouping>,
        selectedGroupIndex: Int
    ): List<ScoresStandingsGroupTitleUiModel> = when (groupings.size) {
        0 -> emptyList()
        else -> renderGroups(groupings, selectedGroupIndex)
    }

    private fun renderGroups(
        groupings: List<StandingsGrouping>,
        selectedGroupIndex: Int
    ) = groupings.mapIndexed { index, group ->
        ScoresStandingsGroupTitleUiModel(
            id = group.id,
            title = group.groupLabel.asParameterized(),
            selected = index == selectedGroupIndex,
            index = index
        )
    }

    fun renderStandingsGroup(
        groupings: List<StandingsGrouping>,
        selectedGroupIndex: Int,
        league: League,
        highlightedTeamId: String?
    ): List<ScoresStandingsGroupUiModel> {
        if (groupings.isEmpty()) return emptyList()
        var currentTitle = ""
        return groupings[selectedGroupIndex].groups.mapNotNull {
            if (it.standings.isEmpty()) {
                null
            } else {
                val title = with(groupings[selectedGroupIndex].toTitle(it.id)) {
                    if (this == currentTitle) {
                        ""
                    } else {
                        currentTitle = this
                        currentTitle
                    }
                }
                ScoresStandingsGroupUiModel(
                    id = it.id,
                    title = title,
                    rankAndTeamsList = renderRankAndTeamsForAGroup(
                        group = it,
                        showSeeding = it.hasSeeding(league),
                        showRank = groupings[selectedGroupIndex].showRank,
                        showRelegation = groupings[selectedGroupIndex].showRank,
                        highlightedTeamId = highlightedTeamId
                    ),
                    standingsList = renderStandingsRowsForAGroup(it, highlightedTeamId)
                )
            }
        }
    }

    private fun renderRankAndTeamsForAGroup(
        group: StandingsGroup,
        showSeeding: Boolean,
        showRank: Boolean,
        showRelegation: Boolean,
        highlightedTeamId: String?
    ): List<UiModel> {
        val models = mutableListOf<UiModel>()
        models.add(
            ScoresStandingsRankAndTeamHeaderUiModel(
                id = group.id,
                label = group.name?.let { ParameterizedString(it) } ?: ParameterizedString(R.string.scores_standings_team_header_default)
            )
        )
        models.addAll(
            group.standings.mapIndexed { index, standing ->
                ScoresStandingsRankAndTeamUiModel(
                    id = standing.id,
                    rank = standing.rank.toString(),
                    relegationColor = standing.relegationStatus.color,
                    teamId = standing.team.id,
                    teamDisplayName = standing.team.displayName,
                    teamName = standing.team.alias,
                    logoUrlList = standing.team.logos,
                    ncaaRanking = standing.team.currentRanking?.toString(),
                    showRank = showRank,
                    showRelegation = showRelegation,
                    showNcaaRanking = showSeeding,
                    showHighlighted = standing.showAsStandingsHighlightedTeam(highlightedTeamId),
                    showDottedPlayoffDivider = group.showDottedPlayoffDivider(standing.rank),
                    showSolidPlayoffDivider = group.showSolidPlayoffDivider(standing.rank),
                    analyticsPayload = standing.toAnalytics(group.id, index)
                )
            }
        )
        return models
    }

    private fun Standing.showAsStandingsHighlightedTeam(highlightedTeamId: String?) = team.id == highlightedTeamId

    private fun Standing.toAnalytics(groupId: String, index: Int) = ScoresStandingsRowUiModel.AnalyticsPayload(
        teamId = team.id,
        pageId = groupId,
        index = index
    )

    private fun StandingsGroup.showSolidPlayoffDivider(currentRank: Int): Boolean {
        if (segments.isEmpty()) return false
        segments.forEach { segment ->
            when {
                segment.type != StandingsSegmentType.PLAY_IN_QUALIFICATION &&
                    segment.type != StandingsSegmentType.PLAYOFF_WILDCARD -> return@forEach
                segment.needsDivider(currentRank) -> return true
            }
        }
        return false
    }

    private fun StandingsGroup.showDottedPlayoffDivider(currentRank: Int): Boolean {
        if (segments.isEmpty()) return false
        segments.forEach { segment ->
            when {
                segment.type != StandingsSegmentType.PLAYOFF_QUALIFICATION -> return@forEach
                segment.needsDivider(currentRank) -> return true
            }
        }
        return false
    }

    /**
     As the divider is draw at the top of the cell, need to adjust the position when
     it is displayed so it appears at the bottom for that team
     **/
    private fun StandingSegment.needsDivider(currentRank: Int) =
        when (this){
            is StandingRangeClosedSegment -> toRank == currentRank - 1
            is StandingRangeFromSegment -> fromRank == currentRank
            is StandingRangeToSegment -> toRank == currentRank - 1
            else -> false
        }

    private fun renderStandingsRowsForAGroup(group: StandingsGroup, highlightedTeamId: String?): List<UiModel> {
        val models = mutableListOf<UiModel>()

        // set extra col width for specific stat values
        val extraColWidth = group.columns.map {
            labelsWithExtraColWidth.contains(it.key)
        }

        models.add(
            ScoreStandingsStatsHeaderUiModel(
                id = group.id,
                labels = group.columns.map { it.value },
                extraColWidth = extraColWidth
            )
        )
        models.addAll(
            group.standings.mapIndexed { index, standing ->
                ScoreStandingsStatsRowUiModel(
                    id = standing.id,
                    labels = group.columns.map { convertLabelToValue(it.key, standing) },
                    teamId = standing.team.id,
                    teamDisplayName = standing.team.displayName,
                    showHighlighted = standing.showAsStandingsHighlightedTeam(highlightedTeamId),
                    showDottedPlayoffDivider = group.showDottedPlayoffDivider(standing.rank),
                    showSolidPlayoffDivider = group.showSolidPlayoffDivider(standing.rank),
                    extraColWidth = extraColWidth,
                    analyticsPayload = standing.toAnalytics(group.id, index)
                )
            }
        )
        return models
    }

    private val labelsWithExtraColWidth = listOf(
        "away_record",
        "home_record"
    )

    fun renderRelegationLegendItems(
        groupings: List<StandingsGrouping>
    ): List<ScoresStandingsRelegationLegendUiModel> {
        return groupings.flatMap { group ->
            group.groups.flatMap { it.standings.map { standing -> standing.relegationStatus } }
        }
            .distinct()
            .filterNot { it == RelegationStatus.UNKNOWN }
            .map {
                ScoresStandingsRelegationLegendUiModel(
                    itemColorRes = it.color,
                    itemLabel = ParameterizedString(it.label)
                )
            }
    }

    private fun convertLabelToValue(label: String, standing: Standing): StandingsLabelModel {
        val labelValue = when (label) {
            "points" -> standing.points.orShortDash()
            "played" -> standing.played.orShortDash()
            "won" -> standing.won.orShortDash()
            "lost" -> standing.lost.orShortDash()
            "drawn" -> standing.drawn.orShortDash()
            "for" -> standing.pointsFor.orShortDash()
            "against" -> standing.pointsAgainst.orShortDash()
            "difference" -> standing.difference.orShortDash()
            "win_pct" -> standing.winPct.orShortDash()
            "div_record" -> standing.divRecord.orShortDash()
            "conf_record" -> standing.confRecord.orShortDash()
            "streak" -> standing.streak.orShortDash()
            "lost_overtime" -> standing.lostOvertime.orShortDash()
            "away_record" -> standing.awayRecord.orShortDash()
            "home_record" -> standing.homeRecord.orShortDash()
            "last_ten_record" -> standing.lastTenRecord.orShortDash()
            "games_behind" -> standing.gamesBehind.orShortDash()
            "elimination_number" -> standing.eliminationNumber.orShortDash()
            "last_six" -> standing.lastSix.orShortDash()
            else -> "TBD"
        }

        val labelColour = when (label) {
            "streak" -> {
                when {
                    labelValue.startsWith("W") -> R.color.ath_bright_green
                    labelValue.startsWith("L") -> R.color.ath_red
                    else -> R.color.ath_grey_10
                }
            }
            "difference" -> {
                labelValue.toIntOrNull()?.let {
                    when {
                        it > 0 -> R.color.ath_bright_green
                        it < 0 -> R.color.ath_red
                        else -> R.color.ath_grey_10
                    }
                }
            }
            else -> R.color.ath_grey_10
        }
        return StandingsLabelModel(labelValue, labelColour ?: R.color.ath_grey_10)
    }

    private val RelegationStatus.toColor: Color
        get() = when (this) {
            RelegationStatus.RELEGATION -> AthColor.RedUser
            RelegationStatus.UEFA_CHAMPIONS_LEAGUE -> AthColor.BlueUser
            RelegationStatus.UEFA_EUROPA_LEAGUE -> AthColor.YellowUser
            RelegationStatus.R16 -> AthColor.Green800
            RelegationStatus.PROMOTION -> AthColor.Gray500
            RelegationStatus.PROMOTION_PLAYOFF -> AthColor.NavyUser
            RelegationStatus.RELEGATION_PLAYOFF -> AthColor.TurquoiseUser
            RelegationStatus.UEFA_CONFERENCE_LEAGUE_QUALIFIERS -> AthColor.PurpleUser
            RelegationStatus.FINALS,
            RelegationStatus.FINAL_PLAYOFFS -> AthColor.Gray200
            else -> Color.Transparent
        }

    private val RelegationStatus.color: Int
        get() = when (this) {
            RelegationStatus.RELEGATION -> R.color.ath_red
            RelegationStatus.UEFA_CHAMPIONS_LEAGUE -> R.color.ath_royal
            RelegationStatus.UEFA_EUROPA_LEAGUE -> R.color.ath_yellow
            RelegationStatus.R16 -> R.color.ath_bright_green
            RelegationStatus.PROMOTION -> R.color.ath_grey_45
            RelegationStatus.PROMOTION_PLAYOFF -> R.color.ath_navy
            RelegationStatus.RELEGATION_PLAYOFF -> R.color.ath_turquoise
            RelegationStatus.UEFA_CONFERENCE_LEAGUE_QUALIFIERS -> R.color.ath_purple
            RelegationStatus.FINALS,
            RelegationStatus.FINAL_PLAYOFFS -> R.color.ath_grey_70
            else -> android.R.color.transparent
        }

    private val RelegationStatus.label: Int
        get() = when (this) {
            RelegationStatus.RELEGATION -> R.string.scores_standing_relegation_relegation_label
            RelegationStatus.UEFA_CHAMPIONS_LEAGUE -> R.string.scores_standing_relegation_champions_league_label
            RelegationStatus.UEFA_EUROPA_LEAGUE -> R.string.scores_standing_relegation_europa_league_label
            RelegationStatus.R16 -> R.string.scores_standing_relegation_r_16_label
            RelegationStatus.FINALS -> R.string.scores_standing_relegation_finals_label
            RelegationStatus.FINAL_PLAYOFFS -> R.string.scores_standing_relegation_finals_playoffs_label
            RelegationStatus.PROMOTION -> R.string.scores_standing_relegation_promotion_label
            RelegationStatus.PROMOTION_PLAYOFF -> R.string.scores_standing_relegation_promotion_playoffs_label
            RelegationStatus.RELEGATION_PLAYOFF -> R.string.scores_standing_relegation_relegation_playoff_label
            RelegationStatus.UEFA_CONFERENCE_LEAGUE_QUALIFIERS -> R.string.scores_standing_relegation_europa_conference_league_qualifying_label
            else -> R.string.box_score_unknown
        }

    private fun StandingsGrouping.toTitle(id: String): String {
        val titleHeader = headers?.find { it?.groupIds?.contains(id) == true }
        if ((titleHeader?.groupIds?.size ?: 0) > 1) {
            if (lastHeader != titleHeader?.headerName) {
                titleHeader?.headerName?.let { name ->
                    lastHeader = name
                    return name
                }
            }
        } else {
            return titleHeader?.headerName.orEmpty()
        }
        return String()
    }

    private fun StandingsGroup.hasSeeding(league: League) =
        if (league == League.NFL) {
            false
        } else {
            standings.any { it.team.currentRanking != null }
        }

    private fun findIndexOfLabel(
        table: MutableList<List<StandingsTableModule.StatsColumnItem>>,
        label: String
    ) = table.indexOfFirst { standings ->
        standings.any { item ->
            (item as? StandingsTableModule.StatsColumnItem.Label)?.text == label
        }
    }

    private fun <T> MutableList<T>.moveItem(fromIndex: Int, toIndex: Int) {
        val item = removeAt(fromIndex)
        add(toIndex, item)
    }
}