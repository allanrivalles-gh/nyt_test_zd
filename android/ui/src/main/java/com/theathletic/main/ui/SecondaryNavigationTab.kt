package com.theathletic.main.ui

import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.theathletic.themes.AthTextStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SecondaryNavigationTab(
    pageIndex: Int,
    text: String,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    Tab(
        text = {
            Text(
                text = text,
                style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            )
        },
        selected = pagerState.currentPage == pageIndex,
        onClick = {
            coroutineScope.launch {
                pagerState.scrollToPage(pageIndex)
            }
            onTabSelected(pageIndex)
        },
    )
}