package com.theathletic.debugtools.ui.userinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

class DebugUserInfoUi {
    data class UserInfoItem(
        val label: String,
        val value: String
    )

    interface Interactor {
        fun onBackClick()
        fun onCopyToClipboard(key: String, contents: String)
    }
}

@Composable
fun DebugUserInfoScreen(
    infoList: List<DebugUserInfoUi.UserInfoItem>,
    interactor: DebugUserInfoUi.Interactor
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(AthTheme.colors.dark100)
    ) {
        Toolbar(interactor)
        DebugUserInfoList(
            infoList = infoList,
            interactor = interactor
        )
    }
}

@Composable
private fun Toolbar(interactor: DebugUserInfoUi.Interactor) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        IconButton(onClick = { interactor.onBackClick() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = AthTheme.colors.dark800
            )
        }
        Text(
            text = "User Info",
            style = AthTextStyle.Slab.Bold.Small,
            color = AthTheme.colors.dark800,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DebugUserInfoList(
    infoList: List<DebugUserInfoUi.UserInfoItem>,
    interactor: DebugUserInfoUi.Interactor
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(infoList) {
            DebugUserInfoItemRow(
                item = it,
                interactor = interactor
            )
        }
    }
}

@Composable
private fun DebugUserInfoItemRow(
    item: DebugUserInfoUi.UserInfoItem,
    interactor: DebugUserInfoUi.Interactor
) {
    Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 6.dp, top = 8.dp)
        ) {
            Text(
                text = item.label,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                color = AthTheme.colors.dark800,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            )
            IconButton(
                onClick = { interactor.onCopyToClipboard(item.label, item.value) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CopyAll,
                    contentDescription = null,
                    tint = AthTheme.colors.dark500,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = item.value,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark500,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
    }
}

@Preview
@Composable
fun DebugUserInfoPreview() {

    val infoList = listOf<DebugUserInfoUi.UserInfoItem>(
        DebugUserInfoUi.UserInfoItem(
            label = "Label 1",
            value = "Value 1"
        ),
        DebugUserInfoUi.UserInfoItem(
            label = "Label 2",
            value = "Value 2"
        ),
        DebugUserInfoUi.UserInfoItem(
            label = "Label 3",
            value = "Value 3"
        ),
        DebugUserInfoUi.UserInfoItem(
            label = "Label 4",
            value = "Value 4"
        ),
        DebugUserInfoUi.UserInfoItem(
            label = "Label 5",
            value = "Value 5"
        )
    )
    DebugUserInfoScreen(infoList, previewInteractor)
}

private val previewInteractor = object : DebugUserInfoUi.Interactor {
    override fun onBackClick() {}
    override fun onCopyToClipboard(key: String, contents: String) {}
}