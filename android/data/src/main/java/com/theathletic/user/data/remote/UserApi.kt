package com.theathletic.user.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Input
import com.theathletic.AcceptChatCodeOfConductMutation
import com.theathletic.MeQuery
import com.theathletic.UpdateUserSortPreferenceMutation
import com.theathletic.UserByHashIdQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.entity.user.SortType
import com.theathletic.type.CommentSortBy
import com.theathletic.type.ContentType
import com.theathletic.utility.IDHasher

// This param is just for versioning the code of conduct field
private const val CODE_OF_CONDUCT_YEAR = 2022

class UserApi @AutoKoin(Scope.SINGLE) constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun getUser(userId: Long): ApolloResponse<UserByHashIdQuery.Data> {
        val hashedId = IDHasher.forUser().encode(userId)
        return apolloClient.query(
            UserByHashIdQuery(id = hashedId)
        ).execute()
    }

    suspend fun getMe() = apolloClient.query(MeQuery()).execute()

    suspend fun acceptChatCodeOfConduct(): ApolloResponse<AcceptChatCodeOfConductMutation.Data> {
        return apolloClient.mutation(
            AcceptChatCodeOfConductMutation(Input.optional(CODE_OF_CONDUCT_YEAR))
        ).execute()
    }

    suspend fun updateUserSortPreference(
        commentsSourceType: CommentsSourceType,
        sortType: SortType
    ): ApolloResponse<UpdateUserSortPreferenceMutation.Data> {
        val contentType = commentsSourceType.contentType
        val sortBy = Input.optional(CommentSortBy.safeValueOf(sortType.value))
        return apolloClient.mutation(
            UpdateUserSortPreferenceMutation(
                contentType,
                sortBy
            )
        ).execute()
    }

    private val CommentsSourceType.contentType: ContentType
        get() = when (this) {
            CommentsSourceType.PODCAST_EPISODE -> ContentType.podcast_episode
            CommentsSourceType.ARTICLE -> ContentType.post
            CommentsSourceType.DISCUSSION -> ContentType.discussion
            CommentsSourceType.QANDA -> ContentType.qanda
            CommentsSourceType.HEADLINE -> ContentType.headline
            CommentsSourceType.GAME -> ContentType.game_v2
            CommentsSourceType.TEAM_SPECIFIC_THREAD -> ContentType.game_v2
        }
}