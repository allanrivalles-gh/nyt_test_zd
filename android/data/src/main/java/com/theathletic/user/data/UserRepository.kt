package com.theathletic.user.data

import com.apollographql.apollo3.api.ApolloResponse
import com.theathletic.MeQuery
import com.theathletic.UserByHashIdQuery
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.annotation.autokoin.Scope
import com.theathletic.auth.remote.toUserEntity
import com.theathletic.comments.v2.data.local.CommentsSourceType
import com.theathletic.data.EmptyParams
import com.theathletic.entity.user.CommentsSortType
import com.theathletic.entity.user.SortType
import com.theathletic.entity.user.UserEntity
import com.theathletic.repository.CoroutineRepository
import com.theathletic.repository.safeApiRequest
import com.theathletic.user.IUserManager
import com.theathletic.user.data.remote.AcceptChatCodeOfConductMutator
import com.theathletic.user.data.remote.UserApi
import com.theathletic.user.data.remote.UserRestApi
import com.theathletic.utility.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class UserRepository @AutoKoin(Scope.SINGLE) constructor(
    dispatcherProvider: DispatcherProvider,
    private val userRestApi: UserRestApi,
    private val userApi: UserApi,
    private val userManager: IUserManager,
    private val acceptChatCodeOfConductMutator: AcceptChatCodeOfConductMutator,
) : CoroutineRepository {

    override val repositoryScope = CoroutineScope(dispatcherProvider.io + SupervisorJob())

    class UserOperationException(message: String) : Exception(message)

    suspend fun fetchUser(id: Long? = null): UserEntity {
        val safeId = id ?: userManager.getCurrentUserId()
        val restUser = userRestApi.getUser(safeId)

        return try {
            // Eventually we will switch over to using GraphQL for all of our User data but
            // for now lets just use the new fields we need that aren't provided by REST
            val graphqlUser = userApi.getUser(safeId).data?.userByHashId ?: return restUser
            val graphqlUserData = userApi.getMe().toUserDetails()
            graphqlUser.populateEntity(restUser, graphqlUserData)
        } catch (e: Exception) {
            Timber.e(e)
            restUser
        }
    }

    suspend fun acceptTermsAndPrivacy(
        privacy: Boolean = true,
        terms: Boolean = true
    ) {
        userRestApi.acceptTermsAndPrivacy(privacy, terms)
    }

    suspend fun editUser(
        userId: Long,
        firstName: String,
        lastName: String,
        email: String
    ) = safeApiRequest {
        userRestApi.editUser(userId, firstName, lastName, email)
    }

    fun acceptChatCodeOfConduct() = repositoryScope.launch {
        acceptChatCodeOfConductMutator.fetchRemote(EmptyParams)
    }

    suspend fun updateUserSortPreferences(
        commentsSourceType: CommentsSourceType,
        sortType: SortType
    ) = try {
        val response = userApi.updateUserSortPreference(
            commentsSourceType,
            sortType
        )
        if (response.hasErrors()) {
            throw UserOperationException("Error on updating user Sort Preferences")
        }
        userManager.updateCommentsSortType(commentsSourceType, sortType)
    } catch (e: Exception) {
        throw UserOperationException("Error on updating user Sort Preferences")
    }
}

fun UserByHashIdQuery.UserByHashId.populateEntity(entity: UserEntity, graphUserData: UserEntity?): UserEntity {
    return entity.apply {
        canHostLiveRoom = asStaff?.can_host_live_rooms ?: false
        isCodeOfConductAccepted = asStaff?.code_of_conduct_2022 ?: asCustomer?.code_of_conduct_2022 ?: false
        commentsSortType = comment_sort_preference.toCommentsSortTypeModel()
        topSportsNewsNotification = graphUserData?.topSportsNewsNotification ?: false
    }
}

internal fun UserByHashIdQuery.Comment_sort_preference2.toCommentsSortTypeModel() = CommentsSortType(
    article = SortType.getByValue(post.rawValue),
    discussion = SortType.getByValue(discussion.rawValue),
    headline = SortType.getByValue(headline.rawValue),
    game = SortType.getByValue(game_v2.rawValue),
    podcast = SortType.getByValue(podcast_episode.rawValue),
    qanda = SortType.getByValue(qanda.rawValue),
)

internal fun ApolloResponse<MeQuery.Data>.toUserDetails() = data?.customer?.asCustomer?.fragments?.customerDetail?.toUserEntity()