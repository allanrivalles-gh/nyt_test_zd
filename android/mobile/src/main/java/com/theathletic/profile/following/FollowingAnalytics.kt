package com.theathletic.profile.following

import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Exposes
import com.theathletic.followable.Followable
import com.theathletic.followable.analyticsIdType
import com.theathletic.followable.analyticsObjectType
import com.theathletic.followable.isAuthor
import com.theathletic.followable.isLeague
import com.theathletic.followable.isTeam
import com.theathletic.followable.typeName

interface FollowingAnalytics {
    var view: String

    fun trackView()
    fun trackClickAddFollow()
    fun trackClickEdit()
    fun trackReorder()
    fun trackFollow(followable: Followable)
    fun trackUnfollow(followable: Followable)
}

@Exposes(FollowingAnalytics::class)
class FollowingAnalyticsImpl @AutoKoin constructor(
    private val analytics: Analytics
) : FollowingAnalytics {

    override var view = ""

    override fun trackView() {
        analytics.track(Event.ManageFollowing.View(view))
    }

    override fun trackClickAddFollow() {
        analytics.track(Event.ManageFollowing.ClickAddFollows(view))
    }

    override fun trackClickEdit() {
        analytics.track(Event.ManageFollowing.ClickEditFollowing(view))
    }

    override fun trackReorder() {
        analytics.track(Event.ManageFollowing.ReorderFollowing(view))
    }

    override fun trackFollow(followable: Followable) {
        analytics.track(
            Event.ManageFollowing.Follow(
                view,
                element = followable.typeName(),
                object_type = followable.analyticsObjectType(),
                object_id = followable.analyticsIdType(),
                team_id = if (followable.isTeam()) followable.id.id else "",
                league_id = if (followable.isLeague()) followable.id.id else "",
                author_id = if (followable.isAuthor()) followable.id.id else ""
            )
        )
    }

    override fun trackUnfollow(followable: Followable) {
        analytics.track(
            Event.ManageFollowing.Unfollow(
                view,
                element = followable.typeName(),
                object_type = followable.analyticsObjectType(isUnfollow = true),
                object_id = followable.analyticsIdType(),
                team_id = if (followable.isTeam()) followable.id.id else "",
                league_id = if (followable.isLeague()) followable.id.id else "",
                author_id = if (followable.isAuthor()) followable.id.id else ""
            )
        )
    }
}