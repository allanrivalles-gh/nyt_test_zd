package com.theathletic.article.data.local

import com.theathletic.entity.local.merge.EntityMerger

object InsiderEntityMerger : EntityMerger<InsiderEntity>() {
    override fun merge(old: InsiderEntity, new: InsiderEntity): InsiderEntity {
        return new.run {
            copy(
                firstName = newerString(old) { firstName }.orEmpty(),
                lastName = newerString(old) { lastName }.orEmpty(),
                fullName = newerString(old) { fullName }.orEmpty(),
                role = newerString(old) { role }.orEmpty(),
                bio = newerString(old) { bio }.orEmpty(),
                imageUrl = newerString(old) { imageUrl }.orEmpty(),
                insiderImageUrl = newerString(old) { insiderImageUrl }.orEmpty()
            )
        }
    }
}