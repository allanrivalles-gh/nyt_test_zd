package com.theathletic.media.androidauto

object AndroidAuto {
    const val PACKAGE_NAME = "com.google.android.projection.gearhead"
    const val EMULATOR_PACKAGE_NAME = "com.example.android.media"

    object Extras {
        const val ID = "extras_id"
        const val PODCAST_ID = "extras_podcast_id"
        const val DOWNLOADED_SECTION = "extras_downloaded_section"
    }

    object Section {
        const val EMPTY_ROOT = "empty_root_id"
        const val ROOT = "media_root_id"

        const val FOLLOWING_ROOT = "following_root"
        const val DOWNLOADED_ROOT = "downloaded_root"
        const val FOLLOWING_ITEM_EPISODES = "following_podcast_item"
    }

    object Items {
        const val FOLLOWING_EPISODE = "following_episode_item"
        const val DOWNLOADED_EPISODE = "downloaded_item"
    }

    object ContentStyle {
        const val SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
        const val BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
        const val GRID_ITEM_HINT_VALUE = 2
    }
}