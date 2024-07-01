package com.theathletic.podcast.analytics

enum class PodcastNavigationSource(val analyticsElement: String) {
    UNKNOWN("unknown"),
    HOME("podcast"),
    FOLLOWING("following"),
    DISCOVER("discover"),
    PLAYER("podcast_player"),
    FRONTPAGE("front_page"),
    BOX_SCORE("box_score"),
    DOWNLOADED("downloaded");
}

class PodcastAnalyticsContext {
    var source: PodcastNavigationSource = PodcastNavigationSource.UNKNOWN
}