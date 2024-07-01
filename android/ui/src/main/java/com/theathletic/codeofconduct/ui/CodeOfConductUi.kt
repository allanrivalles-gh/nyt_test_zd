package com.theathletic.codeofconduct.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R

private const val FAQ_TAG = "FAQ"
private const val CONTACT_SUPPORT_TAG = "Support"

data class CodeOfConductUi(
    @StringRes
    val titleRes: Int,
    @StringRes
    val introRes: Int,
    @StringRes
    val firstSubtitleRes: Int,
    @StringRes
    val firstTextRes: Int,
    @StringRes
    val secondSubtitleRes: Int,
    @StringRes
    val secondTextRes: Int,
    @StringRes
    val thirdSubtitleRes: Int,
    @StringRes
    val thirdTextRes: Int,
    @StringRes
    val fourthSubtitleRes: Int,
    @StringRes
    val fourthTextRes: Int,
    @StringRes
    val epilogueRes: Int,
    @StringRes
    val agreeRes: Int,
    @StringRes
    val disagreeRes: Int,
) {
    interface Interactor {
        fun onFAQClicked()
        fun onContactSupportClicked()
        fun onAgreeClicked()
        fun onDisagreeClicked()
    }
}

@Composable
fun CodeOfConductScreen(
    codeOfConductUi: CodeOfConductUi,
    onFAQClicked: () -> Unit,
    onContactSupportClicked: () -> Unit,
    onAgreeClicked: () -> Unit,
    onDisagreeClicked: () -> Unit,
) {
    val underlinedStyle = SpanStyle(
        color = AthTheme.colors.blue,
        fontWeight = FontWeight.SemiBold,
        textDecoration = TextDecoration.Underline,
    )

    val epilogueText = stringResource(id = codeOfConductUi.epilogueRes)

    val annotatedText = buildAnnotatedString {
        withStyle(
            style = AthTextStyle.TiemposBody.Regular.Small.copy(color = AthTheme.colors.dark500)
                .toParagraphStyle()
        ) {
            append(epilogueText)
        }
        pushStringAnnotation(
            tag = FAQ_TAG,
            annotation = "",
        )
        withStyle(style = underlinedStyle) {
            append(stringResource(id = R.string.comments_code_of_conduct_faq_span))
        }
        pop()

        append(stringResource(id = R.string.comments_check_conduct_or_with_spacers))

        pushStringAnnotation(
            tag = CONTACT_SUPPORT_TAG,
            annotation = "",
        )
        withStyle(style = underlinedStyle) {
            append(stringResource(id = R.string.comments_code_of_conduct_reach_out_span))
        }
        pop()

        append(".")
    }

    Surface(modifier = Modifier.background(AthTheme.colors.dark100)) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .statusBarsPadding(),
        ) {
            with(codeOfConductUi) {
                Text(
                    text = stringResource(id = titleRes),
                    style = AthTextStyle.TiemposHeadline.Regular.Large.copy(color = AthTheme.colors.dark800)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = introRes),
                    style = AthTextStyle.TiemposBody.Regular.Small
                )
                Spacer(modifier = Modifier.height(12.dp))

                SubtitleAndText(subtitle = firstSubtitleRes, text = firstTextRes)
                SubtitleAndText(subtitle = secondSubtitleRes, text = secondTextRes)
                SubtitleAndText(subtitle = thirdSubtitleRes, text = thirdTextRes)
                SubtitleAndText(subtitle = fourthSubtitleRes, text = fourthTextRes)

                ClickableText(
                    text = annotatedText,
                    style = AthTextStyle.TiemposBody.Regular.Small.copy(color = AthTheme.colors.dark500),
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(
                            tag = FAQ_TAG,
                            start = offset,
                            end = offset
                        ).firstOrNull()?.item?.let {
                            onFAQClicked()
                        }
                        annotatedText.getStringAnnotations(
                            tag = CONTACT_SUPPORT_TAG,
                            start = offset,
                            end = offset
                        ).firstOrNull()?.item?.let {
                            onContactSupportClicked()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onAgreeClicked) {
                    Text(
                        text = stringResource(id = agreeRes),
                        style = AthTextStyle.TiemposHeadline.Regular.Medium.copy(color = AthTheme.colors.dark800)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onDisagreeClicked) {
                    Text(
                        text = stringResource(id = disagreeRes),
                        style = AthTextStyle.TiemposHeadline.Regular.Medium.copy(color = AthTheme.colors.dark800)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubtitleAndText(subtitle: Int, text: Int) {
    Text(
        text = stringResource(id = subtitle),
        style = AthTextStyle.TiemposBody.Medium.Medium.copy(color = AthTheme.colors.dark800)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = stringResource(id = text),
        style = AthTextStyle.TiemposBody.Regular.Small
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Preview
@Composable
fun CodeOfConductPreview() {
    CodeOfConductScreen(
        codeOfConductUi = codeOfConductUi,
        onFAQClicked = {},
        onContactSupportClicked = {},
        onAgreeClicked = {},
        onDisagreeClicked = {},
    )
}