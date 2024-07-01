package com.theathletic.gamedetail.boxscore.ui.common

import com.theathletic.R
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.ui.TicketsUiModel
import com.theathletic.boxscore.ui.modules.TicketsModule
import com.theathletic.entity.settings.UserContentEdition
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.gamedetail.data.local.GameDetailLocalModel
import com.theathletic.ui.ResourceString

class BoxScoreTicketsRenderers @AutoKoin constructor() {

    fun createTicketsModule(game: GameDetailLocalModel, contentRegion: UserContentEdition): FeedModuleV2? {
        return game.renderTicketsModule(contentRegion)
    }
}

private fun GameDetailLocalModel.renderTicketsModule(contentRegion: UserContentEdition): FeedModuleV2? {
    return gameTicket?.let { ticket ->
        TicketsModule(
            id = id,
            ticket = TicketsUiModel(
                vendorImageDark = ticket.logoDarkMode,
                vendorImageLight = ticket.logoLightMode,
                title = ticket.toTitle(ticket.minPrice, contentRegion),
                ticketUrlLink = ticket.url
            )
        )
    }
}

private fun GameDetailLocalModel.GameTicket.toTitle(
    minPrice: List<GameDetailLocalModel.GameTicketPrice>,
    contentRegion: UserContentEdition
): ResourceString {
    val cheapestPrice = minPrice.minByOrNull { it.amount }
    return if (cheapestPrice != null) {
        val priceToShow = if (contentRegion == UserContentEdition.US) {
            "${cheapestPrice.currency.localSymbol}${cheapestPrice.amount.toInt()}"
        } else {
            "${cheapestPrice.amount.toInt()} ${cheapestPrice.currency.internationalSymbol} "
        }
        ResourceString.StringWithParams(
            R.string.box_score_game_details_tickets,
            priceToShow
        )
    } else {
        ResourceString.StringWithParams(R.string.box_score_game_details_buy_tickets)
    }
}