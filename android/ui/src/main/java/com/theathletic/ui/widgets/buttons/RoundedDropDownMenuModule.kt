package com.theathletic.ui.widgets.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.RoundedDropDownMenu

data class RoundedDropDownMenuModule(
    val id: String,
    val options: List<String>,
    val selectedOption: String,
) : FeedModuleV2 {

    interface Interaction {
        data class OnOptionSelected(val option: String, val index: Int) : FeedInteraction
    }

    override val moduleId: String = "RoundedDropDownMenuModule:$id"

    @Composable
    override fun Render() {
        val interactor = LocalFeedInteractor.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AthTheme.colors.dark200)
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 4.dp
                )
        ) {
            RoundedDropDownMenu(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { option, index ->
                    interactor.send(
                        Interaction.OnOptionSelected(option, index)
                    )
                }
            )
        }
    }
}