package com.theathletic.comments.v2.data.local

enum class CommentsSourceType {
    HEADLINE,
    ARTICLE,
    PODCAST_EPISODE,
    DISCUSSION,
    GAME,
    TEAM_SPECIFIC_THREAD,
    QANDA;

    fun isDiscussion() = this == DISCUSSION
    fun isQanda() = this == QANDA
    fun isGame() = this == GAME || this == TEAM_SPECIFIC_THREAD
}