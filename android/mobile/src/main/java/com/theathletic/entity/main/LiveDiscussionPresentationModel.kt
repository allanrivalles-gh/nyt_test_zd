package com.theathletic.entity.main

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.theathletic.R
import com.theathletic.utility.datetime.DateUtilityImpl
import java.util.Date

interface DiscussionPresentationModel {
    val id: Long
    val articleTitle: String
    val articleAuthorName: String
    val commentsCount: Long

    val postedDate: String
}

interface LiveDiscussionPresentationModel {
    val id: Long
    val articleTitle: String
    val authorName: String?
    val articleAuthorImage: String?
    val articleAuthorTitle: String?
    val commentCountString: String
    val showAuthorTitle: Boolean
        get() = !articleAuthorTitle.isNullOrBlank()

    val startTimeGmt: String?
    val endTimeGmt: String?

    @StringRes
    fun getStatusText() = when {
        isLive() -> R.string.community_qa_live_title
        isUpcoming() -> R.string.community_qa_upcoming_title
        else -> R.string.community_qa_past_title
    }

    @ColorRes
    fun getStatusColor() = when {
        isLive() -> R.color.ath_green
        isUpcoming() -> R.color.ath_yellow
        else -> R.color.ath_royal
    }

    fun isLive(): Boolean {
        val startTimeRemaining = DateUtilityImpl.parseDateFromGMT(startTimeGmt).time - Date().time
        val endTimeRemaining = DateUtilityImpl.parseDateFromGMT(endTimeGmt).time - Date().time
        return startTimeRemaining < 0L && endTimeRemaining >= 0L
    }

    fun isUpcoming(): Boolean {
        val startTimeRemaining = DateUtilityImpl.parseDateFromGMT(startTimeGmt).time - Date().time
        return startTimeRemaining > 0L
    }

    fun getDiscussionDate(): String = DateUtilityImpl.formatCommunityLiveDiscussionsDateV2(
        startTimeGmt ?: "",
        endTimeGmt ?: ""
    )
}