package com.theathletic.rooms.remote

import com.theathletic.UserByHashIdQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.data.RemoteToLocalFetcher
import com.theathletic.followable.Followable
import com.theathletic.fragment.UserFollowingFragment
import com.theathletic.rooms.local.LiveAudioRoomUserDetails
import com.theathletic.rooms.local.LiveAudioRoomUserDetailsDataSource
import com.theathletic.rooms.local.LiveAudioRoomUserFollowingDataSource
import com.theathletic.user.data.remote.UserApi
import com.theathletic.utility.LogoUtility
import com.theathletic.utility.coroutines.DispatcherProvider

class LiveRoomUserFollowingFetcher @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userApi: UserApi,
    private val userDetailsDataSource: LiveAudioRoomUserDetailsDataSource,
    private val followingDataSource: LiveAudioRoomUserFollowingDataSource,
) : RemoteToLocalFetcher<
    LiveRoomUserFollowingFetcher.Params,
    UserByHashIdQuery.Data,
    LiveRoomUserFollowingFetcher.LocalModels
    >(dispatcherProvider) {

    data class Params(
        val roomId: String,
        val userId: String,
    )

    data class LocalModels(
        val user: LiveAudioRoomUserDetails,
        val followedItems: List<LiveAudioRoomUserDetails.FollowableItem>,
    )

    override suspend fun makeRemoteRequest(
        params: Params
    ) = userApi.getUser(params.userId.toLong()).data

    override fun mapToLocalModel(
        params: Params,
        remoteModel: UserByHashIdQuery.Data
    ) = LocalModels(
        user = remoteModel.userByHashId.toLocal(),
        followedItems = remoteModel.userByHashId.getFollowedItems(),
    )

    override suspend fun saveLocally(params: Params, dbModel: LocalModels) {
        followingDataSource.put(params.userId, dbModel.followedItems)
        userDetailsDataSource.update(params.roomId, listOf(dbModel.user))
    }
}

private fun UserByHashIdQuery.UserByHashId.getFollowedItems() =
    asCustomer?.following?.fragments?.userFollowingFragment?.toLocal()
        ?: asStaff?.following?.fragments?.userFollowingFragment?.toLocal()
        ?: emptyList()

private fun UserByHashIdQuery.UserByHashId.toLocal(): LiveAudioRoomUserDetails {
    return LiveAudioRoomUserDetails(
        id = id,
        firstname = first_name,
        lastname = last_name,
        name = name,
        staffInfo = asStaff?.run {
            LiveAudioRoomUserDetails.StaffInfo(
                bio = bio.orEmpty(),
                imageUrl = avatar_uri,
                twitterHandle = twitter.orEmpty().removePrefix("@"),
                description = description.orEmpty(),
                verified = role.showAsLiveRoomVerified,
            )
        }
    )
}

private fun UserFollowingFragment.toLocal(): List<LiveAudioRoomUserDetails.FollowableItem> {
    val teams = teams.map { team ->
        LiveAudioRoomUserDetails.FollowableItem(
            followableId = Followable.Id(team.id, Followable.Type.TEAM),
            name = team.name.orEmpty(),
            imageUrl = team.logos.firstOrNull()?.uri.orEmpty(),
        )
    }

    val authors = authors.map { author ->
        LiveAudioRoomUserDetails.FollowableItem(
            followableId = Followable.Id(author.id, Followable.Type.AUTHOR),
            name = author.name.orEmpty(),
            imageUrl = author.image_url.orEmpty(),
        )
    }

    val leagues = leagues.map { league ->
        LiveAudioRoomUserDetails.FollowableItem(
            followableId = Followable.Id(league.id, Followable.Type.LEAGUE),
            name = league.name.orEmpty(),
            imageUrl = LogoUtility.getColoredLeagueLogoPath(league.id.toLong()),
        )
    }

    return teams + authors + leagues
}