package com.theathletic.comments.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.preview.DayNightPreview
import com.theathletic.ui.widgets.FormattedText
import com.theathletic.ui.widgets.FormattedTextWithArgs
import com.theathletic.ui.widgets.ResourceIcon
import kotlin.math.max

sealed interface InputHeaderData {
    data class TempBannedHeaderData constructor(
        val numberOfDays: Int
    ) : InputHeaderData {
        val numberOfDaysToDisplay = max(1, numberOfDays)
    }

    data class TopLevelCommentHeaderData(
        val sourceTitle: String
    ) : InputHeaderData

    data class ReplyHeaderData constructor(
        val author: String
    ) : InputHeaderData {
        val replyingToText @Composable get() = stringResource(id = R.string.comments_replying_to, author)
    }

    data class EditHeaderData(val isCommentDrawerFeatureEnabled: Boolean) : InputHeaderData {
        val editingText
            @Composable get() = stringResource(
                id =
                if (isCommentDrawerFeatureEnabled) {
                    R.string.comments_editing
                } else {
                    R.string.comments_editing_legacy
                }
            )
    }

    object EmptyHeaderData : InputHeaderData

    val isEditing get() = this is EditHeaderData
    val shouldShow get() = (this is EmptyHeaderData).not()
}

@Composable
fun InputHeader(
    inputHeader: InputHeaderData,
    onCancel: (InputHeaderData) -> Unit = {},
    onCodeOfConductClick: () -> Unit = {}
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, top = 5.dp)
        ) {
            val weightModifier = Modifier.weight(1f)
            when (inputHeader) {
                is InputHeaderData.TempBannedHeaderData -> InputHeaderTempBan(
                    modifier = weightModifier
                )
                is InputHeaderData.TopLevelCommentHeaderData -> InputHeaderTopLevelComment(
                    sourceTitle = inputHeader.sourceTitle,
                    modifier = weightModifier
                )
                is InputHeaderData.ReplyHeaderData -> InputHeaderReply(
                    author = inputHeader.author,
                    modifier = weightModifier
                )
                is InputHeaderData.EditHeaderData -> InputHeaderEdit(inputHeader.editingText, weightModifier)
                is InputHeaderData.EmptyHeaderData -> {}
            }
            if ((inputHeader is InputHeaderData.EmptyHeaderData).not()) {
                InputHeaderCancelBtn { onCancel(inputHeader) }
            }
        }
        if (inputHeader is InputHeaderData.TempBannedHeaderData) {
            InputSubHeaderTempBan(
                numberOfDays = inputHeader.numberOfDaysToDisplay,
                onCodeOfConductClick = { onCodeOfConductClick() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InputHeaderTempBan(
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ResourceIcon(
            modifier = Modifier.padding(end = 8.dp),
            resourceId = R.drawable.ic_alert_red
        )
        Text(
            text = stringResource(R.string.comments_commenting_disabled),
            style = AthTextStyle.Calibre.Utility.Regular.Small.copy(
                color = AthTheme.colors.red
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun InputSubHeaderTempBan(
    numberOfDays: Int,
    onCodeOfConductClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FormattedText(
            id = R.string.comments_temp_ban_notification_part_1,
            style = AthTextStyle.Calibre.Utility.Medium.Large.copy(color = AthTheme.colors.dark800),
            clickHandler = { onCodeOfConductClick() }
        )
        FormattedTextWithArgs(
            id = R.string.comments_temp_ban_notification_part_2,
            style = AthTextStyle.Calibre.Utility.Medium.Large.copy(color = AthTheme.colors.dark800),
            modifier = Modifier,
            formatArgs = arrayOf(
                pluralStringResource(
                    id = R.plurals.plural_days,
                    count = numberOfDays,
                    numberOfDays
                )
            )
        )
    }
}

@Composable
fun InputHeaderTopLevelComment(sourceTitle: String, modifier: Modifier = Modifier) {
    FormattedTextWithArgs(
        id = R.string.comments_commenting_on,
        style = AthTextStyle.Calibre.Utility.Regular.Small,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        formatArgs = arrayOf(sourceTitle)
    )
}

@Composable
fun InputHeaderReply(author: String, modifier: Modifier = Modifier) {
    FormattedTextWithArgs(
        id = R.string.comments_replying_to,
        style = AthTextStyle.Calibre.Utility.Regular.Small,
        modifier = modifier,
        maxLines = 1,
        formatArgs = arrayOf(author)
    )
}

@Composable
fun InputHeaderEdit(headerText: String, modifier: Modifier = Modifier) {
    Text(
        text = headerText,
        style = AthTextStyle.Calibre.Utility.Regular.Small.copy(
            color = AthTheme.colors.dark500
        ),
        modifier = modifier
    )
}

@Composable
private fun InputHeaderCancelBtn(modifier: Modifier = Modifier, onCancel: () -> Unit) {
    IconButton(
        onClick = onCancel,
        modifier = modifier
            .padding(start = 5.dp, end = 3.dp)
            .size(20.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Cancel,
            contentDescription = null,
            tint = AthTheme.colors.dark800
        )
    }
}

// region Previews
@DayNightPreview
@Composable
private fun TopLevelCommentPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Box(modifier = Modifier.background(AthTheme.colors.dark200)) {
            InputHeader(
                InputHeaderData.TopLevelCommentHeaderData("NBA In-Season Tournament: Bucks, Lakers earn top seeds")
            )
        }
    }
}

@DayNightPreview
@Composable
private fun TempBanPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Box(modifier = Modifier.background(AthTheme.colors.dark200)) {
            InputHeader(InputHeaderData.TempBannedHeaderData(numberOfDays = 8))
        }
    }
}

@DayNightPreview
@Composable
private fun ReplyPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Box(modifier = Modifier.background(AthTheme.colors.dark200)) {
            InputHeader(
                InputHeaderData.ReplyHeaderData("Jason M.")
            )
        }
    }
}

@DayNightPreview
@Composable
private fun EditPreview() {
    AthleticTheme(lightMode = isSystemInDarkTheme().not()) {
        Box(modifier = Modifier.background(AthTheme.colors.dark200)) {
            InputHeader(
                InputHeaderData.EditHeaderData(true)
            )
        }
    }
}
// endregion Previews