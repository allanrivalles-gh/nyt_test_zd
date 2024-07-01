//
//  DismissedComment.swift
//
//
//  Created by kevin fremgen on 7/13/23.
//

import Foundation

public enum DismissedComment {
    case topLevel(text: String)
    case reply(replyComment: CommentViewModel, text: String)
    case play(commentingPlay: CommentingPlay, text: String)
}
