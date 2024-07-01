package com.theathletic.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.buttons.SocialButton

class LoginOptions {
    interface Interactor {
        fun onDebugToolsClick()
    }
}

@Composable
fun LoginOptionsScreen(
    navigationInteractor: AuthenticationNavigator.Interactor,
    interactor: LoginOptions.Interactor,
    isLoading: Boolean,
    showDebugTools: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AthTheme.colors.dark200)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AuthenticationToolbar(
                title = R.string.auth_options_login_title,
                onBackClick = navigationInteractor::onBackClick
            )
            LoginOptions(navigationInteractor, interactor)
        }
        if (isLoading) {
            LoadingIndicator()
        }
        if (showDebugTools) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.BottomCenter),
                backgroundColor = AthTheme.colors.red,
                onClick = interactor::onDebugToolsClick
            ) {
                ResourceIcon(
                    resourceId = R.drawable.ic_gear,
                    tint = AthColor.Gray800
                )
            }
        }
    }
}

@Composable
private fun LoginOptions(
    navigationInteractor: AuthenticationNavigator.Interactor,
    interactor: LoginOptions.Interactor
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 21.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SocialLoginOptions(interactor)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(color = AthTheme.colors.dark500)
                    .weight(1f, fill = true)
            )
            Text(
                text = stringResource(R.string.auth_options_or),
                modifier = Modifier.padding(horizontal = 20.dp),
                color = AthTheme.colors.dark500,
                style = AthTextStyle.Calibre.Utility.Medium.Large
            )
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(color = AthTheme.colors.dark500)
                    .weight(1f, fill = true)
            )
        }
        SocialButton(
            textRes = R.string.auth_options_login_email,
            iconRes = R.drawable.ic_auth_email,
            onClick = navigationInteractor::onLoginOptionsEmailClick
        )
        CreateAnAccount(interactor)
    }
}

@Composable
private fun CreateAnAccount(interactor: LoginOptions.Interactor) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
    ) {
        Text(
            text = stringResource(R.string.auth_dont_have_account),
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Regular.Large
        )
        Text(
            text = stringResource(R.string.auth_sign_up),
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            modifier = Modifier
                .clickable { }
                .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
        )
    }
}

@Composable
private fun SocialLoginOptions(interactor: LoginOptions.Interactor) {
    SocialButton(
        textRes = R.string.auth_options_continue_google,
        modifier = Modifier.padding(top = 64.dp),
        iconRes = R.drawable.ic_auth_google
    )
    SocialButton(
        textRes = R.string.auth_options_continue_fb,
        iconRes = R.drawable.ic_auth_facebook
    )
    SocialButton(
        textRes = R.string.auth_options_continue_apple,
        icon = {
            ResourceIcon(
                resourceId = R.drawable.ic_auth_apple,
                tint = AthColor.Gray100
            )
        }
    )
}