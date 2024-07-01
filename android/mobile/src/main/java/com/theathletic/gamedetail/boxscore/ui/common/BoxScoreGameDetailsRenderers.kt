package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.modules.GameDetailsModule
import com.theathletic.entity.main.Sport
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.gamedetail.data.local.SoccerOfficialType
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.ResourceString.StringWrapper
import com.theathletic.ui.asResourceString
import com.theathletic.utility.LocaleUtility
import com.theathletic.utility.orShortDash
import com.theathletic.utility.safeLet
import com.theathletic.utility.transformIfNotEmptyElseNull
import java.util.concurrent.atomic.AtomicInteger

class BoxScoreGameDetailsRenderers @AutoKoin constructor(
    private val localeUtility: LocaleUtility,
) {

    fun createGameDetailsModule(game: GameDetailLocalModel) = game.renderGameDetailModule()

    @Deprecated("Use createGameDetailsModule(game: GameDetailLocalModel)")
    fun createGameDetailsModule(
        game: GameDetailLocalModel,
        pageOrder: AtomicInteger
    ): FeedModuleV2? {
        pageOrder.getAndIncrement()
        return game.renderGameDetailModule()
    }

    private fun GameDetailLocalModel.renderGameDetailModule(): FeedModuleV2? {
        val items = listOfNotNull(
            addNetworkDetails(),
            addLocation(),
            addOpeningOdds(),
            addWeather(),
            addMatchOfficials()
        ).toMutableList()
        items.lastOrNull()?.let {
            items[items.lastIndex] = it.copy(showDivider = false)
        }
        return items.transformIfNotEmptyElseNull {
            GameDetailsModule(
                id = id,
                details = it,
                titleResId = if (sport == Sport.SOCCER) {
                    R.string.box_score_match_details_title
                } else {
                    R.string.box_score_game_details_title
                }
            )
        }
    }

    private fun GameDetailLocalModel.addNetworkDetails() = broadcastNetwork?.let { network ->
        GameDetailsModule.DetailsItem(
            label = StringWithParams(R.string.box_score_game_details_network_label),
            value = StringWrapper(network)
        )
    }

    private fun GameDetailLocalModel.addLocation() = safeLet(venue, venueCity) { venue, city ->
        GameDetailsModule.DetailsItem(
            label = StringWithParams(R.string.box_score_game_details_location_label),
            value = StringWithParams(R.string.box_score_game_details_location, venue, city)
        )
    }

    private fun GameDetailLocalModel.addOpeningOdds(): GameDetailsModule.DetailsItem? {
        if (oddsPregame.isEmpty()) return null

        val favouredTeam = oddsPregame
            .filterIsInstance<GameDetailLocalModel.GameOddsSpread>()
            .find { it.line.isNegativeLineValue() }

        val spread = oddsPregame
            .filterIsInstance<GameDetailLocalModel.GameOddsSpread>()
            .firstOrNull { it.team?.id == favouredTeam?.team?.id }

        val total = oddsPregame.filterIsInstance<GameDetailLocalModel.GameOddsTotals>().firstOrNull()

        return if (favouredTeam == null) {
            total?.let { safeTotal ->
                GameDetailsModule.DetailsItem(
                    label = StringWithParams(R.string.box_score_game_details_odds_label),
                    value = StringWithParams(
                        R.string.box_score_game_details_opening_odds_even,
                        safeTotal.line.orShortDash()
                    )
                )
            }
        } else {
            safeLet(spread, total) { safeSpread, safeTotal ->
                GameDetailsModule.DetailsItem(
                    label = StringWithParams(R.string.box_score_game_details_odds_label),
                    value = StringWithParams(
                        R.string.box_score_game_details_opening_odds_line,
                        favouredTeam.team?.alias.orShortDash(),
                        safeSpread.line.orShortDash(),
                        safeTotal.line.orShortDash()
                    )
                )
            }
        }
    }

    private fun GameDetailLocalModel.addWeather(): GameDetailsModule.DetailsItem? {
        val extras = (sportExtras as? GameDetailLocalModel.AmericanFootballExtras) ?: return null
        if (isGameInProgressOrCompleted) return null
        return extras.weather?.let { weather ->
            val (weatherFormat, value) = if (localeUtility.isUnitedStates()) {
                Pair(
                    R.string.box_score_game_details_weather_fahrenheit,
                    weather.tempFahrenheit?.toString()
                )
            } else {
                Pair(
                    R.string.box_score_game_details_weather_celsius,
                    weather.tempCelsius?.toString()
                )
            }
            value?.let {
                GameDetailsModule.DetailsItem(
                    label = StringWithParams(R.string.box_score_game_details_weather_label),
                    value = StringWithParams(
                        weatherFormat,
                        value,
                        weather.outlook.orEmpty()
                    )
                )
            }
        }
    }

    private fun GameDetailLocalModel.addMatchOfficials(): GameDetailsModule.DetailsItem? {
        val extras = (sportExtras as? GameDetailLocalModel.SoccerExtras) ?: return null
        if (extras.matchOfficials.isEmpty()) return null
        return GameDetailsModule.DetailsItem(
            label = StringWithParams(R.string.box_score_game_details_officials_label),
            value = extras.matchOfficials.toOfficials().asResourceString()
        )
    }

    private fun List<GameDetailLocalModel.SoccerOfficial>.toOfficials(): String {
        val stringBuilder = StringBuilder()
        this.forEach { official ->
            if (official.officialType == SoccerOfficialType.UNKNOWN) {
                stringBuilder.appendLine("${official.name}")
            } else {
                stringBuilder.appendLine("${official.name} (${official.officialType.label})")
            }
        }
        return stringBuilder.toString()
    }
}

private fun String?.isNegativeLineValue(): Boolean {
    val value = this?.toDoubleOrNull()
    return value != null && value < 0
}