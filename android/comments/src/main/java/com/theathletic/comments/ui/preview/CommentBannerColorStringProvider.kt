package com.theathletic.comments.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class CommentBannerColorStringProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        "000000", // Gray100
        "FFFFFF", // Gray800
        "403C5C", // PurpleUser
        "6DA4E4", // Blue800
        "987FF7", // Purple800
        "3C5634", // GreenUser
        "F89A1E", // YellowUser
    )
}