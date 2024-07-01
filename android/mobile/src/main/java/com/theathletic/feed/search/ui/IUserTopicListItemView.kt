package com.theathletic.feed.search.ui

import com.theathletic.followable.FollowableId

interface IUserTopicListItemView {
    fun onTopicItemClicked(followableId: FollowableId)
}