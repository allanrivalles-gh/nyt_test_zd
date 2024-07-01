package com.theathletic.podcast.browse

import androidx.annotation.StringRes
import com.theathletic.R

enum class BrowsePodcastSection(
    @StringRes val titleId: Int
) {
    NATIONAL(R.string.podcast_league_feed_national),
    TEAMS(R.string.podcast_league_feed_team_podcasts),
    CHANNEL(R.string.podcast_league_feed_national)
}