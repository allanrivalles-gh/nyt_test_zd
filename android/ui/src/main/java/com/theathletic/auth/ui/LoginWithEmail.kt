package com.theathletic.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.buttons.PrimaryButtonLarge

class LoginWithEmail {
    interface Interactor {
        fun onLoginWithEmailClick(email: String, password: String)
    }
}

@Composable
fun LoginWithEmailScreen(
    navigationInteractor: AuthenticationNavigator.Interactor,
    interactor: LoginWithEmail.Interactor,
    isLoading: Boolean
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
            LoginWithEmail(navigationInteractor, interactor)
        }
        if (isLoading) {
            LoadingIndicator()
        }
    }
}

@Composable
private fun LoginWithEmail(
    navigationInteractor: AuthenticationNavigator.Interactor,
    interactor: LoginWithEmail.Interactor
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(horizontal = 21.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CreateAuthTextInput(
            input = email,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            onInputChanged = { email = it },
            placeholder = stringResource(R.string.login_hint_email),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        CreateAuthPasswordInput(
            input = password,
            modifier = Modifier.fillMaxWidth(),
            onInputChanged = { password = it },
            placeholder = stringResource(R.string.login_hint_password),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { submitLogin(interactor, focusManager, email, password) }
            )
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.login_text_forgot_password),
                color = AthTheme.colors.dark400,
                style = AthTextStyle.Calibre.Utility.Medium.Large,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { }
                    .padding(8.dp)
            )
        }
        PrimaryButtonLarge(
            text = stringResource(R.string.login_button_login),
            modifier = Modifier.fillMaxWidth(),
            onClick = { submitLogin(interactor, focusManager, email, password) }
        )
    }
}

private fun submitLogin(
    interactor: LoginWithEmail.Interactor,
    focusManager: FocusManager,
    email: String,
    password: String
) {
    focusManager.clearFocus()
    interactor.onLoginWithEmailClick(email, password)
}