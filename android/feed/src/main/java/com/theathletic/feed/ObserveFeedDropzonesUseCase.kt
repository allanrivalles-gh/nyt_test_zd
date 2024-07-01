package com.theathletic.feed

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.feed.compose.data.DropzoneLayout
import com.theathletic.feed.compose.data.Feed
import com.theathletic.feed.compose.data.FeedRepository
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.Layout
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

// This use case has an important difference when compared to the `ObserveFeedUseCase`.
// It only emits once for each feed update that is caused by a network response changing the local cache.
// This is a requirement for the ads implementation as we don't want to re-fetch ads if other interactions resulted
// in the local data being updated. The `ObserveFeedUseCase` for example, also emits when metadata changes.
internal class ObserveFeedDropzonesUseCase @AutoKoin constructor(
    private val feedRepository: FeedRepository
) {
    operator fun invoke(feedRequest: FeedRequest) =
        feedRepository.observeFeed(feedRequest)
            .map { it.findDropzones() }
            .catch { error -> Timber.e(error) }
}

internal fun Feed.findDropzones() = layouts
    // We need to verify the type and then create a `DropzoneLayout` from it,
    // because currently we are only creating the concrete types in `ObserveFeedUseCase.updateLayouts`.
    .filter { it.type == Layout.Type.DROPZONE }
    .flatMap { DropzoneLayout(it).items }