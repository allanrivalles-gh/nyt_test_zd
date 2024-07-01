package com.theathletic.repository.user

import com.theathletic.entity.authentication.UserData
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Future

interface IUserDataRepository {
    fun isItemRead(id: Long): Boolean

    fun isItemBookmarked(id: Long): Boolean

    fun markItemBookmarked(id: Long, isBookmarked: Boolean): Future<Unit>

    fun isItemRated(id: Long): Boolean

    fun markItemRated(id: Long, isRated: Boolean)

    fun isCommentLiked(id: Long): Boolean

    fun markCommentLiked(id: Long, isLiked: Boolean): Future<Unit>

    fun isCommentFlagged(id: Long): Boolean

    fun markCommentFlagged(id: Long, isFlagged: Boolean): Future<Unit>

    fun markItemRead(id: Long, isRead: Boolean): Future<Unit>

    fun savedArticleIds(): List<Long>

    val userDataFlow: Flow<UserData?>

    val totalReadArticleCount: Int
}