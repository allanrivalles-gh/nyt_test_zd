package com.theathletic.boxscore.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModule
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString
import com.theathletic.ui.widgets.buttons.TwoItemToggleButton

@Deprecated("Use FeedModuleV2 version in ui/widgets/buttons/TwoItemToggleButtonModule.kt")
data class TwoItemToggleButtonModule(
    val id: String,
    val itemOneLabel: ResourceString,
    val itemTwoLabel: ResourceString,
    val isFirstItemSelected: Boolean
) : FeedModule {
    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(AthTheme.colors.dark200)
        ) {
            TwoItemToggleButton(
                modifier = Modifier
                    .padding(
                        vertical = 16.dp,
                        horizontal = 16.dp
                    ),
                itemOneLabel = itemOneLabel,
                itemTwoLabel = itemTwoLabel,
                isFirstItemSelected = isFirstItemSelected,
                onTwoItemToggleSelected = {
                    interactor.send(Interaction.TwoItemToggleClick(it))
                }
            )
        }
    }

    interface Interaction {
        data class TwoItemToggleClick(
            val isFirstItemSelected: Boolean
        ) : FeedInteraction
    }
}

@Composable
@Preview
fun TeamSwitchModulePreview() {
    TwoItemToggleButtonModule(
        id = "unique-id",
        itemOneLabel = "Item One".asResourceString(),
        itemTwoLabel = "Item Two".asResourceString(),
        isFirstItemSelected = true
    ).Render()
}