package com.theathletic.gamedetail.ui

import com.theathletic.utility.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class GameDetailEventProducer(
    private val mutableSharedFlow: MutableSharedFlow<GameDetailEvent> = MutableSharedFlow()
) : MutableSharedFlow<GameDetailEvent> by mutableSharedFlow

class GameDetailEventConsumer(
    private val producer: GameDetailEventProducer
) : Flow<GameDetailEvent> by producer

sealed class GameDetailEvent : Event() {
    data class SelectCommentInDiscussionTab(val commentId: String) : GameDetailEvent()
    data class ReplyToCommentInDiscussionTab(val commentId: String, val parentId: String) : GameDetailEvent()
    object SelectPlayByPlayTab : GameDetailEvent()
    object SelectGradesTab : GameDetailEvent()
    object SelectDiscussionTab : GameDetailEvent()
    object PlayerGraded : GameDetailEvent()
    object Refresh : GameDetailEvent()
}