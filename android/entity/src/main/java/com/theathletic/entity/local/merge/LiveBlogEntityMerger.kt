package com.theathletic.entity.local.merge

import com.theathletic.liveblog.data.local.LiveBlogEntity

object LiveBlogEntityMerger : EntityMerger<LiveBlogEntity>() {

    override fun merge(old: LiveBlogEntity, new: LiveBlogEntity): LiveBlogEntity {
        return new.copy(
            imageUrl = new.newerString(old) { imageUrl },
            lastActivityAt = if (new.lastActivityAt.timeMillis > 0) new.lastActivityAt else old.lastActivityAt,
        )
    }
}