package com.theathletic.followables

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.followable.FollowableId
import com.theathletic.followables.data.FollowableRepository
import com.theathletic.followables.data.UserFollowingRepository

class FollowItemUseCase @AutoKoin constructor(
    private val followableRepository: FollowableRepository,
    private val userFollowingRepository: UserFollowingRepository,
) {
    suspend operator fun invoke(id: FollowableId): Result<com.theathletic.followable.Followable?> {
        userFollowingRepository.followItem(id)?.userFollowingItems?.let { items ->
            if (items.firstOrNull { it.id == id } != null) {
                return Result.success(followableRepository.getFollowable(id))
            }
        }
        return Result.failure(Throwable("Follow failed for: $id"))
    }
}