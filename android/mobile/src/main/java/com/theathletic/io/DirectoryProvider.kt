package com.theathletic.io

import android.content.Context
import android.os.Environment
import java.io.File

class DirectoryProvider(
    context: Context
) {
    private val podcastsExternalDirectory by lazy {
        context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS)
    }

    fun downloadedPodcastDirectory(): File? {
        return podcastsExternalDirectory?.let {
            File(it.toString() + File.separator + "AthleticPodcasts" + File.separator)
        }
    }
}