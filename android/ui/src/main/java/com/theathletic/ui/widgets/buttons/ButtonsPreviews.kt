package com.theathletic.ui.widgets.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R

@Preview(
    name = "Branded Button",
    group = "Dark"
)
@Composable
private fun BrandedButton_Dark_Preview() {
    AthleticTheme(lightMode = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BrandedButtonLarge(
                text = "Subscribe",
                isEnabled = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            BrandedButtonLarge(text = "Subscribe", isEnabled = false, onClick = {})
            BrandedButtonSmall(text = "Subscribe", isEnabled = true, onClick = {})
        }
    }
}

@Preview(
    name = "Branded Button",
    group = "Light"
)
@Composable
private fun BrandedButton_Light_Preview() {
    AthleticTheme(lightMode = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BrandedButtonLarge(
                text = "Subscribe",
                isEnabled = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            BrandedButtonLarge(text = "Subscribe", isEnabled = false, onClick = {})
            BrandedButtonSmall(text = "Subscribe", isEnabled = true, onClick = {})
        }
    }
}

@Preview(
    name = "Primary & Secondary Buttons",
    group = "Dark"
)
@Composable
private fun PrimarySecondaryButton_Dark_Preview() {
    AthleticTheme(lightMode = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButtonLarge(
                text = "Primary",
                isEnabled = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            SecondaryButtonLarge(
                text = "Secondary",
                isEnabled = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            PrimaryButtonLarge(text = "Primary", isEnabled = true, onClick = {})
            SecondaryButtonLarge(text = "Secondary", isEnabled = true, onClick = {})
            PrimaryButtonLarge(text = "Primary", isEnabled = false, onClick = {})
            SecondaryButtonLarge(text = "Secondary", isEnabled = false, onClick = {})
            PrimaryButtonSmall(text = "Primary", isEnabled = true, onClick = {})
            SecondaryButtonSmall(text = "Secondary", isEnabled = true, onClick = {})
        }
    }
}

@Preview(
    name = "Primary & Secondary Buttons",
    group = "Light"
)
@Composable
private fun PrimarySecondaryButton_Light_Preview() {
    AthleticTheme(lightMode = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButtonLarge(
                text = "Primary",
                isEnabled = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            SecondaryButtonLarge(
                text = "Secondary",
                isEnabled = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            PrimaryButtonLarge(text = "Primary", isEnabled = true, onClick = {})
            SecondaryButtonLarge(text = "Secondary", isEnabled = true, onClick = {})
            PrimaryButtonLarge(text = "Primary", isEnabled = false, onClick = {})
            SecondaryButtonLarge(text = "Secondary", isEnabled = false, onClick = {})
            PrimaryButtonSmall(text = "Primary", isEnabled = true, onClick = {})
            SecondaryButtonSmall(text = "Secondary", isEnabled = true, onClick = {})
        }
    }
}

@Composable
@Preview(
    name = "Social Button",
    group = "Dark"
)
private fun SocialButton_Preview() {
    AthleticTheme(lightMode = false) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
            )
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
                enabled = false
            )
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
                fillWidth = false
            )
        }
    }
}

@Composable
@Preview(
    name = "Social Button",
    group = "Light"
)
private fun SocialButton_Preview_Light() {
    AthleticTheme(lightMode = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
            )
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
                enabled = false
            )
            SocialButton(
                textRes = R.string.auth_options_continue_google,
                iconRes = R.drawable.ic_auth_google,
                fillWidth = false
            )
        }
    }
}