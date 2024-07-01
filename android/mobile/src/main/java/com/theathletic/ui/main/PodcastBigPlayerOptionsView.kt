package com.theathletic.ui.main

import com.theathletic.ui.BaseView

interface PodcastBigPlayerOptionsView : BaseView {
    fun onCloseClick()
    fun onDownloadClick()
    fun onGoToEpisodeClick()
    fun onGoToPodcastClick()
    fun onFollowPodcastClick()
    fun onShareClick()
}