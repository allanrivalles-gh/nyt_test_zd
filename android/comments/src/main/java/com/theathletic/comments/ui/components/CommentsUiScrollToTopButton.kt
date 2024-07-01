package com.theathletic.comments.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun ScrollToTop(
    scope: CoroutineScope,
    listState: LazyListState,
) {
    val showButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    AnimatedVisibility(
        visible = showButton,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp, end = 10.dp)
        ) {
            ScrollButton(onClick = {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            })
        }
    }
}

@Composable
fun BoxScope.ScrollButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AthTheme.colors.dark300,
            contentColor = AthTheme.colors.dark500
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .size(40.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .clip(CircleShape)
            .align(Alignment.BottomEnd)
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun CommentsScrollToTopButton_Light() {
    AthleticTheme(lightMode = true) {
        ScrollToTopPreview()
    }
}

@Preview
@Composable
private fun CommentsScrollToTopButton_Dark() {
    AthleticTheme(lightMode = false) {
        ScrollToTopPreview()
    }
}

@Composable
private fun ScrollToTopPreview() {
    val listState = rememberLazyListState()
    val list by remember { mutableStateOf(List(50) { it }) }
    Column(modifier = Modifier.background(AthTheme.colors.dark200)) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = false),
            onRefresh = { }
        ) {
            LazyColumn(
                state = listState
            ) {
                items(list) { item ->
                    Text(
                        text = "Item $item",
                        color = AthTheme.colors.dark800,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }
            }
            ScrollToTop(
                scope = rememberCoroutineScope(),
                listState = listState,
            )
        }
    }
}