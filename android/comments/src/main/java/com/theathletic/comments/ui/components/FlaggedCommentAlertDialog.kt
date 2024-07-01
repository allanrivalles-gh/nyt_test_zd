package com.theathletic.comments.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theathletic.comments.FlagReason
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R

@Composable
fun FlaggedCommentAlertDialog(
    selectedOption: FlagReason,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onSelectedClick: (FlagReason) -> Unit
) {
    val flagReasons = listOf(
        FlagReason.ABUSIVE_OR_HARMFUL,
        FlagReason.TROLLING_OR_BAITING,
        FlagReason.SPAM,
        FlagReason.USER
    )
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(id = R.string.comments_flag_dialog_title),
                style = AthTextStyle.Slab.Bold.Small.copy(fontWeight = FontWeight.Bold),
                color = AthTheme.colors.dark800,
            )
        },
        text = {
            Column {
                flagReasons.forEachIndexed { index, flagReason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (flagReason == selectedOption),
                                onClick = { onSelectedClick(flagReason) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (flagReason == selectedOption),
                            onClick = { onSelectedClick(flagReason) },
                            colors = RadioButtonDefaults.colors(selectedColor = AthTheme.colors.red)
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringArrayResource(id = R.array.news_comments_flag_confirm_options)[index],
                            color = AthTheme.colors.dark700,
                            style = AthTextStyle.Calibre.Utility.Regular.Large,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick,
                enabled = selectedOption != FlagReason.NONE
            ) {
                Text(
                    text = stringResource(id = R.string.comments_flag_dialog_confirm),
                    color = AthTheme.colors.red
                )
            }
        },
    )
}