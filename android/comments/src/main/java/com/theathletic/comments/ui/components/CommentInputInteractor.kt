package com.theathletic.comments.ui.components

import com.theathletic.comments.ui.CommentsInputUiState

interface CommentInputInteractor {
    fun onCommentInputClick() {}
    fun onKeyboardOpenChanged(newState: Boolean) {}
    fun onTextChanged(newText: String) {}
    fun onSendClick(onFinished: () -> Unit) {}
    fun onCancelInput(inputHeaderData: InputHeaderData) {}
    fun onCodeOfConductClick() {}
    fun onUndoCancel(priorState: CommentsInputUiState) {}
    fun onUndoNoLongerAvailable() {}
}