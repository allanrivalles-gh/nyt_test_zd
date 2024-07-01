package com.theathletic.article.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.R
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.widgets.buttons.SecondaryButtonSmall

@Composable
fun ArticleCommentsNotLoadedNotice(
    isLoading: Boolean,
    onTapToReloadComments: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 28.dp),
    ) {
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier
                .padding(bottom = 28.dp)
                .height(1.dp),
        )
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(if (isLoading) 0.0F else 1.0F)
            ) {
                Text(
                    stringResource(R.string.article_comments_preview_failed_to_load_message),
                    style = AthTextStyle.Calibre.Utility.Medium.Small,
                    color = AthTheme.colors.dark700,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                SecondaryButtonSmall(
                    text = stringResource(R.string.article_reload_comments_preview_button),
                    onClick = onTapToReloadComments
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    color = AthTheme.colors.dark800,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        Divider(
            color = AthTheme.colors.dark300,
            modifier = Modifier
                .padding(top = 28.dp)
                .height(1.dp),
        )
    }
}

@Preview
@Composable
fun ArticleCommentsNotLoadedNoticePreview() {
    Column(
        modifier = Modifier
            .background(AthTheme.colors.dark100)
            .padding(top = 16.dp, bottom = 16.dp),
    ) {
        ArticleCommentsNotLoadedNotice(
            onTapToReloadComments = { },
            isLoading = false,
        )
        Box(modifier = Modifier.height(28.dp))
        ArticleCommentsNotLoadedNotice(
            onTapToReloadComments = { },
            isLoading = true,
        )
    }
}