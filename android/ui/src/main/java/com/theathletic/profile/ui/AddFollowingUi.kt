package com.theathletic.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.followable.Followable
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.SearchTextField
import com.theathletic.ui.widgets.buttons.ToggleButtonGroup

interface AddFollowingUi {
    interface Interactor : FollowableItemUi.FollowableItem.Interactor {
        fun onBackClick()
        fun onUpdateSearchText(updatedText: String)
        fun onFilterSelected(filter: FollowingFilter)
    }
}

enum class FollowingFilter {
    All,
    Teams,
    Leagues,
    Authors
}

@Composable
fun AddFollowingScreen(
    isLoading: Boolean,
    searchText: String,
    addedItems: List<FollowableItemUi.FollowableItem>,
    suggestedItems: List<FollowableItemUi.FollowableItem>,
    interactor: AddFollowingUi.Interactor
) {
    Column(
        modifier = Modifier.background(AthTheme.colors.dark200)
    ) {
        AddFollowingToolbar(interactor = interactor)
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        } else {
            AddFollowingList(
                searchText = searchText,
                addedItems = addedItems,
                suggestedItems = suggestedItems,
                interactor = interactor
            )
        }
    }
}

@Composable
private fun AddFollowingToolbar(
    interactor: AddFollowingUi.Interactor
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(vertical = 4.dp)
    ) {
        IconButton(
            onClick = interactor::onBackClick,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                tint = AthTheme.colors.dark800,
                contentDescription = null
            )
        }

        Text(
            text = stringResource(id = R.string.global_add),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Slab.Bold.Small
        )

        TextButton(
            onClick = interactor::onBackClick,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_toolbar_done),
                color = AthTheme.colors.red,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun AddFollowingList(
    searchText: String,
    addedItems: List<FollowableItemUi.FollowableItem>,
    suggestedItems: List<FollowableItemUi.FollowableItem>,
    interactor: AddFollowingUi.Interactor
) {
    val buttons = FollowingFilter.values().toList()

    SearchTextField(
        searchText = searchText,
        placeholderRes = R.string.following_search_placeholder,
        leadingIconRes = R.drawable.ic_nav2_search,
        trailingIconRes = R.drawable.ic_x,
        onUpdateSearchText = { interactor.onUpdateSearchText(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
    )

    ToggleButtonGroup(
        buttons = buttons,
        onButtonSelected = { _, currentFilter -> interactor.onFilterSelected(currentFilter) },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp)
    )

    ItemsList(
        interactor = interactor,
        addedItems = addedItems,
        suggestedItems = suggestedItems,
        searchText = searchText
    )
}

@Composable
private fun ItemsList(
    interactor: AddFollowingUi.Interactor,
    addedItems: List<FollowableItemUi.FollowableItem>,
    suggestedItems: List<FollowableItemUi.FollowableItem>,
    searchText: String
) {
    LazyColumn {
        if (addedItems.isNotEmpty()) {
            item {
                SectionHeader(text = stringResource(id = R.string.added_header))
            }
            items(
                items = addedItems,
                key = { item -> item.id.toString() }
            ) { item ->
                FollowableItem(
                    followableItem = item,
                    interactor = interactor,
                    viewMode = ViewMode.ADD
                )
            }
        }
    }
    LazyColumn {
        if (suggestedItems.isNotEmpty()) {
            item {
                SectionHeader(
                    text = if (searchText.isEmpty()) {
                        stringResource(id = R.string.suggested_header)
                    } else {
                        stringResource(id = R.string.results_header)
                    }
                )
            }
            items(
                items = suggestedItems,
                key = { item -> item.id.toString() }
            ) { item ->
                FollowableItem(
                    followableItem = item,
                    interactor = interactor,
                    viewMode = ViewMode.ADD
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = AthTextStyle.Calibre.Utility.Medium.Large,
        color = AthTheme.colors.dark500,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Preview
@Composable
fun AddFollowingListNoFollows_LightThemePreview() {
    AthleticTheme(lightMode = true) {
        AddFollowingScreen(
            false,
            "",
            addedItems = emptyList(),
            suggestedItems = dummyData.drop(5),
            interactor = dummyInteractor
        )
    }
}

@Preview
@Composable
fun AddFollowingListNoFollows_DarkThemePreview() {
    AthleticTheme(lightMode = false) {
        AddFollowingScreen(
            false,
            "",
            addedItems = emptyList(),
            suggestedItems = dummyData.drop(5),
            interactor = dummyInteractor
        )
    }
}

@Preview
@Composable
fun AddFollowingListFull_LightThemePreview() {
    AthleticTheme(lightMode = true) {
        AddFollowingScreen(
            false,
            "",
            addedItems = dummyData.dropLast(5),
            suggestedItems = dummyData.drop(5),
            interactor = dummyInteractor
        )
    }
}

@Preview
@Composable
fun AddFollowingListFull_DarkThemePreview() {
    AthleticTheme(lightMode = false) {
        AddFollowingScreen(
            false,
            "",
            addedItems = dummyData.dropLast(5),
            suggestedItems = dummyData.drop(5),
            interactor = dummyInteractor
        )
    }
}

private val dummyData =
    listOf(
        FollowableItemUi.FollowableItem(
            id = Followable.Id("1", Followable.Type.TEAM),
            name = "Dodgers",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("2", Followable.Type.TEAM),
            name = "49ers",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("3", Followable.Type.TEAM),
            name = "Sharks",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("4", Followable.Type.TEAM),
            name = "Stars",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("5", Followable.Type.TEAM),
            name = "Wild",
            imageUrl = "",
            isLoading = false,
            isFollowing = true
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("6", Followable.Type.TEAM),
            name = "NBA",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("7", Followable.Type.TEAM),
            name = "NHL",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("8", Followable.Type.TEAM),
            name = "NFL",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("9", Followable.Type.TEAM),
            name = "Olympics",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        ),
        FollowableItemUi.FollowableItem(
            id = Followable.Id("10", Followable.Type.TEAM),
            name = "Fantasy Football",
            imageUrl = "",
            isLoading = false,
            isFollowing = false
        )
    )

private val dummyInteractor = object : AddFollowingUi.Interactor {
    override fun onBackClick() {}
    override fun onUpdateSearchText(updatedText: String) {}
    override fun onFilterSelected(filter: FollowingFilter) {}
    override fun onFollowClick(item: FollowableItemUi.FollowableItem) {}
    override fun onUnfollowClick(item: FollowableItemUi.FollowableItem) {}
}