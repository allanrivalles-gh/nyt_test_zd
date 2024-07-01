package com.theathletic.boxscore.ui.modules

import androidx.compose.runtime.Composable
import com.theathletic.boxscore.ui.TicketsUi
import com.theathletic.boxscore.ui.TicketsUiModel
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor

data class TicketsModule(
    val id: String,
    val ticket: TicketsUiModel
) : FeedModuleV2 {

    interface Interaction {
        data class TicketLinkClick(val url: String) : FeedInteraction
    }

    override val moduleId: String = "TicketModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        TicketsUi(
            vendorImageDark = ticket.vendorImageDark,
            vendorImageLight = ticket.vendorImageLight,
            title = ticket.title,
            ticketUrlLink = ticket.ticketUrlLink
        ) { ticketLink ->
            interactor.send(Interaction.TicketLinkClick(ticketLink))
        }
    }
}