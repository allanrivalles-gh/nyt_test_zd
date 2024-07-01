package com.theathletic.ui.widgets.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTheme
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asResourceString

data class TwoItemToggleButtonModule(
    val id: String,
    val itemOneLabel: ResourceString,
    val itemTwoLabel: ResourceString,
    val isFirstItemSelected: Boolean,
    val includeTopDivider: Boolean = true
) : FeedModuleV2 {

    override val moduleId: String = "TwoItemToggleButtonModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        if (includeTopDivider) {
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box(
            modifier = Modifier.background(AthTheme.colors.dark200)
        ) {
            TwoItemToggleButton(
                modifier = Modifier.padding(16.dp),
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