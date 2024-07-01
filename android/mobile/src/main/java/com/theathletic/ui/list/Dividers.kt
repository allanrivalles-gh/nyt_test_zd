package com.theathletic.ui.list

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.theathletic.R
import com.theathletic.ui.UiModel
import java.util.concurrent.atomic.AtomicInteger

data class Divider(
    val seed: String? = null,
    @DimenRes val verticalPadding: Int = R.dimen.global_spacing_0,
    @DimenRes val horizontalPadding: Int = R.dimen.global_spacing_0
) : UiModel {

    constructor(
        seed: Int,
        @DimenRes verticalPadding: Int = R.dimen.global_spacing_0,
        @DimenRes horizontalPadding: Int = R.dimen.global_spacing_0
    ) : this(seed.toString(), verticalPadding, horizontalPadding)

    companion object {
        var uniqueId = AtomicInteger(1)
    }

    override val stableId = "DIVIDER:${seed ?: uniqueId.getAndIncrement().toString()}"
}

data class FeedItemDivider(
    val seed: String? = null,
    @DimenRes val verticalPadding: Int = R.dimen.global_spacing_0,
    @DimenRes val horizontalPadding: Int = R.dimen.global_spacing_0
) : UiModel {

    constructor(
        seed: Int,
        @DimenRes verticalPadding: Int = R.dimen.global_spacing_0,
        @DimenRes horizontalPadding: Int = R.dimen.global_spacing_0
    ) : this(seed.toString(), verticalPadding, horizontalPadding)

    companion object {
        var uniqueId = AtomicInteger(1)
    }

    override val stableId = "FEED_ITEM_DIVIDER:${seed ?: uniqueId.getAndIncrement().toString()}"
}

class FeedItemVerticalPadding(
    seed: String? = null,
    @DimenRes val paddingRes: Int
) : UiModel {

    constructor(
        seed: Int,
        @DimenRes paddingRes: Int = R.dimen.global_spacing_0
    ) : this(seed.toString(), paddingRes)

    companion object {
        var uniqueId = AtomicInteger(1)
    }
    override val stableId = "FEED_ITEM_VERTICAL_PADDING:${seed ?: uniqueId.getAndIncrement().toString()}"
}

class FeedItemVerticalPaddingColor(
    @DimenRes val paddingRes: Int,
    @ColorRes val colorRes: Int = R.color.ath_grey_80,
    seed: String? = null
) : UiModel {

    constructor(
        @DimenRes paddingRes: Int = R.dimen.global_spacing_0,
        @ColorRes colorRes: Int = R.color.ath_grey_80,
        seed: Int,
    ) : this(paddingRes, colorRes, seed.toString())

    companion object {
        var uniqueId = AtomicInteger(1)
    }
    override val stableId = "FEED_ITEM_VERTICAL_PADDING:${seed ?: uniqueId.getAndIncrement().toString()}"
}