package com.theathletic.onboarding.paywall.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.asString
import com.theathletic.ui.widgets.ResourceIcon
import com.theathletic.ui.widgets.SimpleShrinkToFitText
import com.theathletic.ui.widgets.buttons.BrandedButtonLarge

class OnboardingPaywallUi {

    interface Interactor {
        fun onSkipClick()
        fun onPurchaseClick()
        fun onTermsClick()
        fun onPrivacyClick()
    }
}

@Composable
fun OnboardingPaywallScreen(
    @StringRes titleRes: Int,
    @StringRes ctaRes: Int,
    finePrint: ResourceString,
    showVATInfo: Boolean,
    isCTAEnabled: Boolean,
    interactor: OnboardingPaywallUi.Interactor
) {
    Image(
        painter = painterResource(id = R.drawable.bg_onboarding_subscribe),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column {
        SkipButton(interactor = interactor)
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxSize()
                .padding(32.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Logo()
            TitleAndSubTitle(titleRes = titleRes)
            BillingItems(
                ctaRes = ctaRes,
                finePrint = finePrint,
                showVATInfo = showVATInfo,
                isCTAEnabled = isCTAEnabled,
                interactor = interactor
            )
            TermsAndPrivacy(interactor = interactor)
        }
    }
}

@Composable
private fun ColumnScope.SkipButton(
    interactor: OnboardingPaywallUi.Interactor
) {
    TextButton(
        onClick = interactor::onSkipClick,
        modifier = Modifier.align(Alignment.End)
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_toolbar_skip),
            style = AthTextStyle.Calibre.Utility.Medium.Large,
            color = AthColor.Gray600,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun Logo() {
    ResourceIcon(
        resourceId = R.drawable.ic_athletic_text,
    )
}

@Composable
private fun TitleAndSubTitle(
    @StringRes titleRes: Int,
) {
    Column {
        SimpleShrinkToFitText(
            text = stringResource(id = titleRes),
            style = AthTextStyle.Slab.Bold.Large.copy(color = AthColor.Gray800, fontSize = 64.sp),
            maxLines = 2
        )

        Text(
            text = stringResource(id = R.string.onboarding_subscribe_note),

            style = AthTextStyle.TiemposHeadline.Regular.Small,
            color = AthColor.Gray600
        )
    }
}

@Composable
private fun BillingItems(
    @StringRes ctaRes: Int,
    finePrint: ResourceString,
    showVATInfo: Boolean,
    isCTAEnabled: Boolean,
    interactor: OnboardingPaywallUi.Interactor
) {
    Column {
        BrandedButtonLarge(
            text = stringResource(id = ctaRes),
            modifier = Modifier.fillMaxWidth(),
            isEnabled = isCTAEnabled,
            onClick = interactor::onPurchaseClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AthColor.Gray800,
                contentColor = AthColor.Gray200,
                disabledBackgroundColor = AthColor.Gray400,
                disabledContentColor = AthColor.Gray200
            )
        )

        Text(
            text = finePrint.asString(),
            style = AthTextStyle.Calibre.Utility.Regular.Large,
            color = AthColor.Gray600,
            modifier = Modifier.padding(top = 16.dp)
        )

        if (showVATInfo) {
            Text(
                text = stringResource(id = R.string.onboarding_subscribe_vat),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
                color = AthColor.Gray700,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun TermsAndPrivacy(
    interactor: OnboardingPaywallUi.Interactor
) {
    Row(
        horizontalArrangement = spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.dialog_gdpr_terms_title),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthColor.Gray600,
            modifier = Modifier
                .clickable { interactor.onTermsClick() }
                .padding(vertical = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.dialog_gdpr_privacy_title),
            style = AthTextStyle.Calibre.Utility.Regular.ExtraSmall,
            color = AthColor.Gray600,
            modifier = Modifier
                .clickable { interactor.onPrivacyClick() }
                .padding(vertical = 8.dp)
        )
    }
}

@Preview(device = Devices.NEXUS_5)
@Composable
private fun Preview_SubscribeScreen() {
    AthleticTheme(lightMode = false) {
        OnboardingPaywallScreen(
            titleRes = R.string.onboarding_subscribe_title_trial,
            ctaRes = R.string.onboarding_subscribe_button_text_trial,
            finePrint = StringWithParams(
                R.string.onboarding_subscribe_price_text_trial,
                "$71.99"
            ),
            showVATInfo = true,
            isCTAEnabled = true,
            interactor = PreviewInteractor
        )
    }
}

@Preview
@Composable
private fun Preview_SubscribeScreen_ErrorState() {
    AthleticTheme(lightMode = false) {
        OnboardingPaywallScreen(
            titleRes = R.string.onboarding_subscribe_title_trial,
            ctaRes = R.string.onboarding_subscribe_button_text_trial,
            finePrint = StringWithParams(R.string.global_billing_error_init_failed),
            showVATInfo = false,
            isCTAEnabled = false,
            interactor = PreviewInteractor
        )
    }
}

private object PreviewInteractor : OnboardingPaywallUi.Interactor {
    override fun onSkipClick() {}
    override fun onPurchaseClick() {}
    override fun onTermsClick() {}
    override fun onPrivacyClick() {}
}