package com.theathletic.boxscore

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.boxscore.data.BoxScoreRepository
import com.theathletic.boxscore.data.local.Article
import com.theathletic.repository.user.IUserDataRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class ObserveBoxScoreFeedUseCase @AutoKoin constructor(
    private val boxScoreRepository: BoxScoreRepository,
    private val userDataRepository: IUserDataRepository
) {
    operator fun invoke(gameId: String) = flow {
        userDataRepository.userDataFlow.combine(boxScoreRepository.getBoxScoreFeed(gameId)) { userData, boxScore ->
            boxScore?.sections?.flatMap { it.modules }?.flatMap { it.blocks }?.forEach { block ->
                if (block is Article) {
                    val articleIdLong = block.articleId.toLong()
                    block.isRead = userData?.articlesRead?.contains(articleIdLong) == true
                    block.isBookmarked = userData?.articlesSaved?.contains(articleIdLong) == true
                }
            }
            emit(boxScore)
        }.collect()
    }
}