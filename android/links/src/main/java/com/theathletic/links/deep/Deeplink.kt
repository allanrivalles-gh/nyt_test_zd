package com.theathletic.links.deep

import androidx.core.net.toUri
import com.theathletic.links.LinkHelper
import com.theathletic.links.LinkHelper.Companion.KEY_SOURCE
import com.theathletic.links.deep.DeeplinkType.ARTICLE
import com.theathletic.links.deep.DeeplinkType.AUTHOR
import com.theathletic.links.deep.DeeplinkType.BOXSCORE
import com.theathletic.links.deep.DeeplinkType.CATEGORY
import com.theathletic.links.deep.DeeplinkType.DISCUSSIONS
import com.theathletic.links.deep.DeeplinkType.FRONTPAGE
import com.theathletic.links.deep.DeeplinkType.HEADLINE
import com.theathletic.links.deep.DeeplinkType.LIVE_BLOGS
import com.theathletic.links.deep.DeeplinkType.LIVE_DISCUSSIONS
import com.theathletic.links.deep.DeeplinkType.LIVE_ROOMS
import com.theathletic.links.deep.DeeplinkType.MANAGE_TEAMS
import com.theathletic.links.deep.DeeplinkType.PODCAST
import com.theathletic.links.deep.DeeplinkType.REACTIONS
import com.theathletic.links.deep.DeeplinkType.SCORES

class Deeplink(val value: String) {
    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_TYPE = "type"
        private const val KEY_ID = "id"

        fun article(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$ARTICLE/$id")
        fun author(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$AUTHOR/$id")

        fun category(id: String, title: String? = ""): Deeplink =
            Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$CATEGORY/$id}").addTitle(title)

        fun discussion(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$DISCUSSIONS/$id")
        fun frontpage() = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$FRONTPAGE")
        fun headline(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$HEADLINE/$id")

        fun liveBlog(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$LIVE_BLOGS/$id")
        fun liveBlogPost(blogId: String, postId: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$LIVE_BLOGS/$blogId/$postId")
        fun liveDiscussion(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$LIVE_DISCUSSIONS/$id")
        fun liveRoom(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$LIVE_ROOMS/$id")

        fun manageTeams(type: String, id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$MANAGE_TEAMS")
            .addParameter(KEY_TYPE, type)
            .addParameter(KEY_ID, id)

        fun news(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$REACTIONS/news/$id")
        fun podcast(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$PODCAST/$id")
        fun scores() = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$SCORES")
        fun boxScore(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}$BOXSCORE/$id")

        // Feeds
        fun leagueFeed(teamId: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}league/$teamId")
        fun podcastFeed() = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}podcasts")
        fun teamFeed(id: String) = Deeplink("${LinkHelper.BASE_DEEPLINK_SCHEME}team/$id")
    }

    private fun toUri() = this.value.toUri()

    fun addParameter(key: String, value: String) = Deeplink(
        toUri().buildUpon()
            .appendQueryParameter(key, value).build().toString()
    )

    fun addSource(source: String) = Deeplink(
        toUri().buildUpon()
            .appendQueryParameter(KEY_SOURCE, source).build().toString()
    )

    fun addTitle(title: String?): Deeplink {
        if (title == null) return this
        return this.addParameter(KEY_TITLE, title)
    }
}