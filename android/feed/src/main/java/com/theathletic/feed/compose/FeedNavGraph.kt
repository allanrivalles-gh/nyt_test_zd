package com.theathletic.feed.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.theathletic.feed.compose.data.FeedRequest
import com.theathletic.feed.compose.data.FeedType
import com.theathletic.feed.compose.ui.FeedScreen
import com.theathletic.feed.compose.ui.FeedViewModel
import com.theathletic.feed.compose.ui.ads.screenSizeForAds
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FeedGraph(feed: FeedGraphFeed, ads: FeedGraphAdsConfig, route: String) {
    val resources = LocalContext.current.resources
    FeedScreen(
        viewModel = koinViewModel {
            parametersOf(
                FeedViewModel.Params(
                    request = feed.request(),
                    route = route,
                    ads = FeedViewModel.Params.Ads(
                        screenSize = resources.screenSizeForAds,
                        shouldImproveImpressions = ads.shouldImproveImpressions,
                        experiments = ads.experiments,
                        appVersionName = ads.appVersionName,
                    )
                )
            )
        }
    )
}

data class FeedGraphAdsConfig(
    val shouldImproveImpressions: Boolean,
    val experiments: List<String>,
    val appVersionName: String,
)

enum class FeedGraphFeed {
    FOLLOWING,
    DISCOVER
}

private fun FeedGraphFeed.request(): FeedRequest {
    return when (this) {
        FeedGraphFeed.FOLLOWING -> FeedRequest(feedType = FeedType.FOLLOWING)
        FeedGraphFeed.DISCOVER -> FeedRequest(feedType = FeedType.DISCOVER)
    }
}