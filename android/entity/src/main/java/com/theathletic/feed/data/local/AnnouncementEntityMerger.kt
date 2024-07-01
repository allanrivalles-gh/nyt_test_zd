package com.theathletic.feed.data.local

import com.theathletic.entity.local.merge.EntityMerger

object AnnouncementEntityMerger : EntityMerger<AnnouncementEntity>() {
    override fun merge(old: AnnouncementEntity, new: AnnouncementEntity): AnnouncementEntity {
        return new.apply {
            isDismissed = old.isDismissed || new.isDismissed
        }
    }
}