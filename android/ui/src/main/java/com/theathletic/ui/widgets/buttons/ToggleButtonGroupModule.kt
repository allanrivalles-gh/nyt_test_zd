package com.theathletic.ui.widgets.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTheme

data class ToggleButtonGroupModule(
    val id: String,
    val buttons: List<Any>,
    val selectedGroupIndex: Int
) : FeedModuleV2 {

    override val moduleId: String = "ToggleButtonGroupModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        Box(
            modifier = Modifier
                .background(AthTheme.colors.dark200)
                .padding(16.dp)
        ) {
            ToggleButtonGroup(
                buttons = buttons,
                onButtonSelected = { pos, button ->
                    interactor.send(Interaction.ToggleButtonGroup(pos, button.toString()))
                },
                selectedIndex = selectedGroupIndex,
            )
        }
    }

    interface Interaction {
        data class ToggleButtonGroup(
            val buttonTabClicked: Int,
            val title: String
        ) : FeedInteraction
    }
}