package com.theathletic.feed.search.ui

import androidx.annotation.DrawableRes
import com.theathletic.profile.manage.UserTopicId
import com.theathletic.profile.manage.UserTopicType
import com.theathletic.ui.UiModel

data class UserTopicListItem(
    @Deprecated("Use topicId")
    val id: Long,
    @Deprecated("Use topicId")
    val topicType: UserTopicType,
    val topicId: UserTopicId,
    val name: String,
    val logoUri: String?,
    @DrawableRes val logoPlaceholder: Int? = null,
    val circularLogo: Boolean = false,
    @DrawableRes val selectedIcon: Int?,
    val showDivider: Boolean = true
) : UiModel {
    override val stableId = "$name:$id"

    interface Interactor {
        fun onTopicItemClicked(item: UserTopicListItem)
    }
}