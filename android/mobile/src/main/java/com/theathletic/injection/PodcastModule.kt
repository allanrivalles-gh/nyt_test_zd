package com.theathletic.injection

import android.os.Bundle
import com.theathletic.main.ui.listen.ListenTabEventConsumer
import com.theathletic.main.ui.listen.ListenTabEventProducer
import com.theathletic.manager.IPodcastManager
import com.theathletic.manager.PodcastManager
import com.theathletic.podcast.analytics.PodcastAnalyticsContext
import com.theathletic.podcast.browse.BrowsePodcastViewModel
import com.theathletic.podcast.download.PodcastDownloadStateStore
import com.theathletic.podcast.ui.PodcastFeedEpisodeItemPresenter
import com.theathletic.viewmodel.main.PodcastSleepTimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val podcastModule = module {
    single { PodcastManager as IPodcastManager }
    viewModel { (extras: Bundle?) -> PodcastSleepTimerViewModel(extras) }

    viewModel { (extras: Bundle) ->
        BrowsePodcastViewModel(extras)
    }

    single { ListenTabEventProducer() }
    single { ListenTabEventConsumer(get()) }

    single { PodcastDownloadStateStore() }

    factory {
        PodcastFeedEpisodeItemPresenter(
            get(),
            get()
        )
    }

    single { PodcastAnalyticsContext() }
}