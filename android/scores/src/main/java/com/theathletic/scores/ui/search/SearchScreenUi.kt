package com.theathletic.scores.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.followable.Followable
import com.theathletic.hub.HubTabType
import com.theathletic.scores.R
import com.theathletic.scores.navigation.ScoresFeedNavigator
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.animation.ViewSlideAnimation
import com.theathletic.ui.collectWithLifecycle
import com.theathletic.ui.utility.rememberKoin
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon
import kotlinx.coroutines.delay

data class ResultItem(
    val id: Followable.Id,
    val name: String,
    val logo: String
)

@Composable
fun SearchScreen(
    viewModel: SearchComposeViewModel,
    onCancelClick: () -> Unit
) {
    val navigator = rememberKoin<ScoresFeedNavigator>(LocalContext.current)

    viewModel.eventConsumer.collectWithLifecycle { event ->
        when (event) {
            is Event.NavigateToHub -> navigator.navigateToHubActivity(event.feedType, HubTabType.Schedule)
        }
    }

    val state by viewModel.uiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthTheme.colors.dark200)
    ) {
        // Search bar slide in from top first, once its animation is completed
        // then the rest of the list appears. We using LaunchedEffect to add the delay
        // for showing the list

        ViewSlideAnimation(
            view =
            {
                SearchBar(state, viewModel, onCancelClick)
            },
            slideDown = false
        )

        var isListVisible by remember { mutableStateOf(false) }

        if (isListVisible) {
            SearchResults(
                results = if (state.searchText.isEmpty()) state.following else state.results,
                searchText = state.searchText,
                onItemsClicked = { id: Followable.Id, index: Int ->
                    viewModel.onEvent(SearchComposeEvent.OnSearchResultClicked(id, index))
                }
            )
        }

        LaunchedEffect(Unit) {
            delay(500)
            isListVisible = true
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    state: SearchComposeUiState,
    viewModel: SearchComposeViewModel,
    onCancelClick: () -> Unit
) {
    var showKeyboard by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(focusRequester) {
        if (showKeyboard) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        SearchTextField(
            searchText = state.searchText,
            isEnable = true,
            onUpdateSearchText = { viewModel.onEvent(SearchComposeEvent.OnSearchTextUpdate(it)) },
            onClick = {},
            modifier = Modifier
                .weight(0.9f)
                .onFocusChanged { showKeyboard = it.hasFocus.not() }
                .focusRequester(focusRequester)
        )

        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        Text(
            text = stringResource(id = R.string.global_action_cancel),
            color = AthTheme.colors.dark700,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = AthTextStyle.Calibre.Utility.Medium.Small,
            modifier = Modifier
                .background(color = Color.Transparent)
                .padding(end = 16.dp)
                .clickable(
                    onClick = {
                        onCancelClick()
                    },
                    interactionSource = MutableInteractionSource(),
                    indication = null
                )
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun SearchResults(
    results: List<ResultItem>,
    searchText: String,
    onItemsClicked: (Followable.Id, Int) -> Unit
) {
    if (searchText.isNotEmpty()) {
        Text(
            text = pluralStringResource(
                id = com.theathletic.ui.R.plurals.search_results,
                count = results.size,
                results.size
            ),
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(results) { index, item ->
            SearchedItem(
                teamLogos = item.logo,
                name = item.name,
                onItemClicked = { onItemsClicked(item.id, index) }
            )
            Divider(color = AthTheme.colors.dark300, thickness = 1.dp)
        }
    }
}

@Composable
fun SearchedItem(
    teamLogos: String,
    name: String,
    onItemClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(color = AthTheme.colors.dark200)
            .fillMaxWidth()
            .clickable { onItemClicked() }
            .padding(vertical = 14.dp)
    ) {

        RemoteImageAsync(
            url = teamLogos,
            modifier = Modifier
                .size(30.dp)
                .align(alignment = Alignment.CenterStart)
        )

        Text(
            text = name,
            color = AthTheme.colors.dark700,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            modifier = Modifier
                .padding(start = 42.dp)
                .align(alignment = Alignment.CenterStart)
        )

        ResourceIcon(
            resourceId = R.drawable.search_chevron,
            tint = AthTheme.colors.dark800,
            modifier = Modifier.align(alignment = Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Suppress("LongMethod")
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    searchText: String,
    onUpdateSearchText: (String) -> Unit,
    isEnable: Boolean = true,
    onClick: () -> Unit,
) {
    BasicTextField(
        value = searchText,
        textStyle = AthTextStyle.Calibre.Utility.Regular.Large.copy(color = AthTheme.colors.dark800),
        onValueChange = { onUpdateSearchText(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp)
            .clickable(
                onClick = { onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(
                color = AthTheme.colors.dark300,
                shape = RoundedCornerShape(4.dp)
            )
            .then(modifier),
        interactionSource = remember { MutableInteractionSource() },
        enabled = isEnable,
        singleLine = true,
        cursorBrush = SolidColor(AthTheme.colors.dark800)
    ) {
        TextFieldDefaults.TextFieldDecorationBox(
            value = searchText,
            innerTextField = it,
            singleLine = true,
            enabled = true,
            visualTransformation = VisualTransformation.None,

            leadingIcon = {
                ResourceIcon(
                    resourceId = R.drawable.search_textfield,
                    tint = AthTheme.colors.dark800
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { onUpdateSearchText("") }) {
                        ResourceIcon(
                            resourceId = R.drawable.search_cancel,
                            tint = AthTheme.colors.dark700
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_teams_leagues_placeholder),
                    style = AthTextStyle.Calibre.Utility.Regular.Large.copy(color = AthTheme.colors.dark500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            interactionSource = remember { MutableInteractionSource() },
            contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
                top = 0.dp, bottom = 0.dp
            )
        )
    }
}