package com.theathletic.auth.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

class AuthenticationNavigator {
    interface Interactor {
        fun onDebugToolsClick()
        fun onLoginOptionsEmailClick()
        fun onBackClick()
    }
}

@Composable
fun AuthenticationToolbar(
    @StringRes title: Int,
    onBackClick: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        onBackClick?.let {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { it() }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = AthTheme.colors.dark800
                )
            }
        }

        Text(
            text = stringResource(id = title),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            style = AthTextStyle.Slab.Bold.Small,
            color = AthTheme.colors.dark800
        )
    }
}

@Composable
fun BoxScope.LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.align(Alignment.Center)
    ) {
        CircularProgressIndicator(
            color = AthTheme.colors.dark700,
            strokeWidth = 2.dp
        )
    }
}