package com.theathletic.liveblog.ui

import com.theathletic.liveblog.data.local.NativeLiveBlog
import com.theathletic.liveblog.data.local.NativeLiveBlogAdTargets
import com.theathletic.liveblog.data.local.NativeLiveBlogTags

fun nativeLiveBlogFixture(
    liveBlogId: String,
    gameId: String,
    isGame: Boolean = false,
) = NativeLiveBlog(
    id = liveBlogId,
    gameId = gameId,
    tags = listOf(nativeLiveBlogTagsFixture(isGame)),
    adTargets = NativeLiveBlogAdTargets(emptyList(), emptyList(), emptyList(), emptyList())
)

fun nativeLiveBlogTagsFixture(
    isGame: Boolean
) = NativeLiveBlogTags(id = "1", type = if (isGame) "game" else "other", name = "")