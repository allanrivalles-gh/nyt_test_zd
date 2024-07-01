package com.theathletic.hub.team.ui

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.datetime.DateUtility
import com.theathletic.datetime.Datetime
import com.theathletic.datetime.formatter.DisplayFormat
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.FeedUiV2
import com.theathletic.hub.team.data.local.TeamHubRosterLocalModel
import com.theathletic.hub.team.ui.modules.TeamHubRosterTableModule
import com.theathletic.hub.team.ui.modules.TeamHubRosterTableSpacerModule
import com.theathletic.hub.ui.SortablePlayerValuesTableUi
import com.theathletic.themes.AthColor
import com.theathletic.ui.LoadingState
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.Transformer
import com.theathletic.ui.asResourceString
import com.theathletic.ui.orShortDash
import com.theathletic.ui.utility.parseHexColor
import com.theathletic.utility.orShortDash
import java.util.Calendar

class TeamHubRosterTransformer @AutoKoin constructor(
    private val dateUtility: DateUtility
) :
    Transformer<TeamHubRosterState, TeamHubRosterContract.ViewState> {

    override fun transform(data: TeamHubRosterState): TeamHubRosterContract.ViewState {

        return TeamHubRosterContract.ViewState(
            showSpinner = data.loadingState.isFreshLoadingState,
            showEmptyState = data.isRosterAvailable.not(),
            feedUiModel = FeedUiV2(createRosterModules(data))
        )
    }

    private fun createRosterModules(data: TeamHubRosterState) =
        if (data.teamDetails != null && data.rosters.isNotEmpty()) {
            mutableListOf<FeedModuleV2>().apply {
                add(
                    TeamHubRosterTableSpacerModule(
                        data.teamDetails.id
                    )
                )
                addAll(
                    data.rosters.map {
                        createRosterFeedModule(
                            category = it,
                            teamDetails = data.teamDetails,
                        )
                    }
                )
            }
        } else {
            emptyList()
        }

    private fun createRosterFeedModule(
        category: TeamHubRosterState.Category,
        teamDetails: TeamHubRosterLocalModel.TeamDetails,
    ) = TeamHubRosterTableModule(
        id = teamDetails.id,
        playerTable = SortablePlayerValuesTableUi(
            playerColumn = createPlayerColumn(teamDetails, category.roster),
            valueColumns = createValuesColumns(category, teamDetails.sport),
        ),
        headingResId = if (teamDetails.sport.containsCategories) category.type.labelResId else R.string.empty_string,
        showHeading = teamDetails.sport.containsCategories
    )

    private fun createPlayerColumn(
        teamDetails: TeamHubRosterLocalModel.TeamDetails,
        roster: List<TeamHubRosterLocalModel.PlayerDetails>,
    ): List<SortablePlayerValuesTableUi.PlayerColumnItem> {
        val showHeadshots = roster.containsHeadshots
        return mutableListOf<SortablePlayerValuesTableUi.PlayerColumnItem>().apply {
            add(
                SortablePlayerValuesTableUi.PlayerColumnItem.HeaderCell(
                    R.string.team_hub_player_stats_column_title
                )
            )
            addAll(
                roster.map { player ->
                    SortablePlayerValuesTableUi.PlayerColumnItem.PlayerCell(
                        name = player.displayName.orShortDash(),
                        jerseyNumber = player.jerseyNumber?.let { jerseyNumber ->
                            StringWithParams(
                                R.string.team_hub_player_jersey_number_formatter,
                                jerseyNumber
                            )
                        }.orShortDash(),
                        headshots = player.headshots,
                        teamLogos = teamDetails.logos,
                        teamColor = teamDetails.teamColor.parseHexColor(AthColor.Gray500),
                        showHeadshot = showHeadshots
                    )
                }
            )
        }
    }

    private fun createValuesColumns(
        category: TeamHubRosterState.Category,
        sport: Sport,
    ): List<List<SortablePlayerValuesTableUi.ValueColumnItem>> {
        return listOfNotNull(
            createPositionColumn(category),
            createHeightColumn(category, sport),
            createWeightColumn(category, sport),
            if (category.roster.containsDobValues) createDOBColumn(category) else null,
            if (category.roster.containsDobValues) createAgeColumn(category) else null,
        )
    }

    private fun TeamHubRosterState.SortType.toId(type: TeamHubRosterState.CategoryType) =
        SortablePlayerValuesTableUi.CellId(type.name, name)

    private fun createValueColumn(
        id: SortablePlayerValuesTableUi.CellId,
        title: ResourceString,
        values: List<ResourceString>,
        order: SortablePlayerValuesTableUi.ColumnOrder,
        highlighted: Boolean
    ): List<SortablePlayerValuesTableUi.ValueColumnItem> {
        return mutableListOf<SortablePlayerValuesTableUi.ValueColumnItem>().apply {
            add(
                SortablePlayerValuesTableUi.ValueColumnItem.HeaderCell(
                    id = id,
                    title = title,
                    order = order,
                    highlighted = highlighted,
                )
            )
            addAll(
                values.map { value ->
                    SortablePlayerValuesTableUi.ValueColumnItem.ValueCell(
                        value = value,
                        highlighted = highlighted,
                    )
                }
            )
        }
    }

    private fun createPositionColumn(category: TeamHubRosterState.Category) =
        createValueColumn(
            id = TeamHubRosterState.SortType.Position.toId(category.type),
            title = StringWithParams(R.string.team_hub_roster_column_heading_position),
            values = category.roster.map { player -> player.position.alias.asResourceString() },
            order = category.toColumnOrder(TeamHubRosterState.SortType.Position),
            highlighted = category.sortType == TeamHubRosterState.SortType.Position
        )

    private fun createHeightColumn(
        category: TeamHubRosterState.Category,
        sport: Sport,
    ) = createValueColumn(
        id = TeamHubRosterState.SortType.Height.toId(category.type),
        title = StringWithParams(R.string.team_hub_roster_column_heading_height),
        values = category.roster.map { player -> player.height?.formatHeight(sport).orShortDash() },
        order = category.toColumnOrder(TeamHubRosterState.SortType.Height),
        highlighted = category.sortType == TeamHubRosterState.SortType.Height
    )

    private fun createWeightColumn(
        category: TeamHubRosterState.Category,
        sport: Sport,
    ) = createValueColumn(
        id = TeamHubRosterState.SortType.Weight.toId(category.type),
        title = StringWithParams(R.string.team_hub_roster_column_heading_weight),
        values = category.roster.map { player -> player.weight?.formatWeight(sport).orShortDash() },
        order = category.toColumnOrder(TeamHubRosterState.SortType.Weight),
        highlighted = category.sortType == TeamHubRosterState.SortType.Weight
    )

    private fun createDOBColumn(category: TeamHubRosterState.Category) =
        createValueColumn(
            id = TeamHubRosterState.SortType.DateOfBirth.toId(category.type),
            title = StringWithParams(R.string.team_hub_roster_column_heading_dob),
            values = category.roster.map { player -> player.dateOfBirth?.toLocalizedDOB().orShortDash() },
            order = category.toColumnOrder(TeamHubRosterState.SortType.DateOfBirth),
            highlighted = category.sortType == TeamHubRosterState.SortType.DateOfBirth
        )

    private fun createAgeColumn(category: TeamHubRosterState.Category) =
        createValueColumn(
            id = TeamHubRosterState.SortType.Age.toId(category.type),
            title = StringWithParams(R.string.team_hub_roster_column_heading_age),
            values = category.roster.map { player -> player.dateOfBirth?.toAge().orShortDash() },
            order = category.toColumnOrder(TeamHubRosterState.SortType.Age),
            highlighted = category.sortType == TeamHubRosterState.SortType.Age
        )

    private fun Int.formatWeight(sport: Sport) = if (sport == Sport.SOCCER) {
        StringWithParams(R.string.team_hub_roster_weight_kilograms, this)
    } else {
        StringWithParams(R.string.team_hub_roster_weight_pounds, this)
    }

    private fun Int.formatHeight(sport: Sport): ResourceString = if (sport == Sport.SOCCER) {
        StringWithParams(R.string.team_hub_roster_height_meters, this / 100f)
    } else {
        val (feet, inches) = inchesToFeet
        StringWithParams(R.string.team_hub_roster_height_feet, feet, inches)
    }

    private val Int.inchesToFeet: Pair<Int, Int>
        get() = Pair(this / 12, this % 12)

    private fun String.toLocalizedDOB(): ResourceString {
        val gmtDOB = Datetime(dateUtility.parseDateFromGMT(this).time)
        return dateUtility.formatGMTDate(gmtDOB, DisplayFormat.LOCALIZED_DATE).asResourceString()
    }

    private fun String.toAge(): ResourceString {
        val today = Calendar.getInstance()
        val dob = Calendar.getInstance()
        dob.time = dateUtility.parseDateFromGMT(this)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) --age
        return age.toString().asResourceString()
    }

    private val TeamHubRosterState.CategoryType.labelResId: Int
        get() = when (this) {
            TeamHubRosterState.CategoryType.Offense -> R.string.team_hub_roster_category_label_offense
            TeamHubRosterState.CategoryType.Defense -> R.string.team_hub_roster_category_label_defense
            TeamHubRosterState.CategoryType.SpecialTeams -> R.string.team_hub_roster_category_label_special_teams
            TeamHubRosterState.CategoryType.GoalKeepers -> R.string.team_hub_roster_category_label_goal_keeper
            TeamHubRosterState.CategoryType.OutfieldPlayers -> R.string.team_hub_roster_category_label_outfield_players
            TeamHubRosterState.CategoryType.Centers -> R.string.team_hub_roster_category_label_centers
            TeamHubRosterState.CategoryType.LeftWings -> R.string.team_hub_roster_category_label_left_wings
            TeamHubRosterState.CategoryType.RightWings -> R.string.team_hub_roster_category_label_right_wings
            TeamHubRosterState.CategoryType.Goalies -> R.string.team_hub_roster_category_label_goalies
            TeamHubRosterState.CategoryType.Pitchers -> R.string.team_hub_roster_category_label_pitchers
            TeamHubRosterState.CategoryType.Catchers -> R.string.team_hub_roster_category_label_catchers
            TeamHubRosterState.CategoryType.Infielders -> R.string.team_hub_roster_category_label_infielders
            TeamHubRosterState.CategoryType.Outfielders -> R.string.team_hub_roster_category_label_outfielders
            TeamHubRosterState.CategoryType.DesignatedHitter -> R.string.team_hub_roster_category_label_designated_hitter
            TeamHubRosterState.CategoryType.NoCategories -> R.string.empty_string
        }

    private val Sport.containsCategories: Boolean
        get() = this != Sport.BASKETBALL

    private val TeamHubRosterState.isRosterAvailable: Boolean
        get() = loadingState != LoadingState.FINISHED || (teamDetails != null && rosters.isNotEmpty())

    private val List<TeamHubRosterLocalModel.PlayerDetails>.containsDobValues: Boolean
        get() = firstOrNull{ it.dateOfBirth != null } != null

    private val List<TeamHubRosterLocalModel.PlayerDetails>.containsHeadshots: Boolean
        get() = firstOrNull{ it.headshots.isNotEmpty() } != null

    private fun TeamHubRosterState.Category.toColumnOrder(sortType: TeamHubRosterState.SortType) =
        if (this.sortType == sortType) order else SortablePlayerValuesTableUi.ColumnOrder.None
}