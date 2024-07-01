package com.theathletic.rooms.create.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.asString

data class LiveRoomCategoriesUi(
    val categories: List<Category>,
) {

    data class Category(
        val slug: String,
        val title: ResourceString,
        val isSelected: Boolean,
    )

    interface Interactor {
        fun onCategoryClicked(value: String)
        fun onCloseClicked()
    }
}

@Composable
fun LiveRoomCategoriesScreen(
    uiModel: LiveRoomCategoriesUi,
    interactor: LiveRoomCategoriesUi.Interactor,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
    ) {
        Toolbar(interactor = interactor)
        CategoriesList(uiModel = uiModel, interactor = interactor)
    }
}

@Composable
private fun Toolbar(
    interactor: LiveRoomCategoriesUi.Interactor,
) {
    Box(
        modifier = Modifier.height(56.dp)
            .background(AthTheme.colors.dark200)
    ) {
        Text(
            text = stringResource(R.string.rooms_create_type_title),
            textAlign = TextAlign.Center,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Slab.Bold.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
        )

        Icon(
            imageVector = Icons.Default.Close,
            tint = AthTheme.colors.dark800,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .clickable(onClick = interactor::onCloseClicked)
        )
    }
}
@Composable
private fun CategoriesList(
    uiModel: LiveRoomCategoriesUi,
    interactor: LiveRoomCategoriesUi.Interactor,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.global_select_all_that_apply),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark800,
            modifier = Modifier.padding(16.dp),
        )

        Divider(color = AthTheme.colors.dark300)
        uiModel.categories.forEach { category ->
            Category(
                title = category.title.asString(),
                isSelected = category.isSelected,
                onClick = { interactor.onCategoryClicked(category.slug) },
            )
            Divider(color = AthTheme.colors.dark300)
        }
    }
}

@Composable
private fun Category(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthTheme.colors.dark800,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                tint = AthTheme.colors.dark800,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .padding(start = 4.dp)
            )
        }
    }
}