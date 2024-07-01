package com.theathletic.comments.analytics

import java.io.Serializable

data class CommentsAnalyticsPayload(
    val additionalIdRef: String? = null,
    val vIndex: Int? = null,
    val leagueId: String? = null,
    val gameStatusView: String? = null
) : Serializable