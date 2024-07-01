package com.theathletic.comments.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.entity.user.SortType
import com.theathletic.entity.user.stringResId
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.RoundedDropDownMenu

@Composable
fun CommentsFilterCountBar(
    sortOptions: List<SortType>,
    selectedOption: SortType,
    commentsCount: Int,
    onSortOptionSelected: (SortType) -> Unit
) {
    Row(
        modifier = Modifier
            .background(AthTheme.colors.dark100)
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp, top = 16.dp)
        ) {
            CommentFilterDropdown(sortOptions, selectedOption, onSortOptionSelected)
            CommentsCount(commentsCount)
        }
    }
}

@Composable
private fun CommentFilterDropdown(
    options: List<SortType>,
    selectedOption: SortType,
    onSortOptionSelected: (SortType) -> Unit,
) {
    RoundedDropDownMenu(
        options = options.map { it.asStringResource() },
        selectedOption = selectedOption.asStringResource(),
        onOptionSelected = { _, index ->
            onSortOptionSelected(SortType.getByIndex(index))
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CommentsCount(commentsCount: Int) {
    Text(
        text = pluralStringResource(id = R.plurals.plural_comments, commentsCount, commentsCount),
        color = AthTheme.colors.dark700,
        style = AthTextStyle.Calibre.Utility.Medium.Large.copy(textAlign = TextAlign.End),
    )
}

@Composable
fun SortType.asStringResource() = stringResource(id = stringResId)

@Preview
@Composable
private fun CommentsFilterDropdownCompose_LightPreview() {
    val options = SortType.values().asList()
    var selectedOption by remember { mutableStateOf(options[0]) }

    AthleticTheme(lightMode = true) {
        Column {
            Column {
                CommentsFilterCountBar(
                    sortOptions = options,
                    selectedOption = selectedOption,
                    commentsCount = 248
                ) { newOption ->
                    selectedOption = newOption
                }
            }
        }
    }
}

@Preview
@Composable
private fun CommentsFilterDropdownCompose_DarkPreview() {
    val options = SortType.values().asList()
    var selectedOption by remember { mutableStateOf(options[0]) }

    AthleticTheme(lightMode = false) {
        Column {
            CommentsFilterCountBar(
                sortOptions = options,
                selectedOption = selectedOption,
                commentsCount = 248
            ) { newOption ->
                selectedOption = newOption
            }
        }
    }
}