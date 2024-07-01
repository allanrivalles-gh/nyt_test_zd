package com.theathletic.rooms.create.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.rooms.ui.modifiers.horizontalListScrim
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun CreateRoomChipContainer(
    chips: List<String>,
    hint: String,
    onAddClick: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .border(
                width = 1.dp,
                color = AthTheme.colors.dark400,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {

        if (chips.isEmpty()) {
            Text(
                text = hint,
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        } else {
            val scrollState = rememberScrollState()

            Row(
                horizontalArrangement = Arrangement.Absolute.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp)
                    .horizontalListScrim(scrollState = scrollState)
                    .horizontalScroll(state = scrollState),
            ) {
                chips.forEach { tag -> Chip(text = tag) }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        ResourceIcon(
            resourceId = R.drawable.ic_plus_circle,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterEnd)
                .clickable(onClick = onAddClick)
        )
    }
}

@Composable
private fun Chip(text: String) {
    Text(
        text = text,
        style = AthTextStyle.Calibre.Utility.Regular.Large,
        color = AthTheme.colors.dark800,
        modifier = Modifier
            .background(AthTheme.colors.dark300, RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}