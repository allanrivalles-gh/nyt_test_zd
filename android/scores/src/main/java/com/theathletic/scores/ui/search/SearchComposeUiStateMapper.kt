package com.theathletic.scores.ui.search

import com.theathletic.followables.data.domain.FollowableSearchItem

fun List<FollowableSearchItem>.toFollowingUi() = sortedByDescending { it.followableId.type }
    .map { result -> result.toUiModel() }

private fun FollowableSearchItem.toUiModel(): ResultItem {
    return ResultItem(
        id = followableId,
        name = name,
        logo = imageUrl
    )
}