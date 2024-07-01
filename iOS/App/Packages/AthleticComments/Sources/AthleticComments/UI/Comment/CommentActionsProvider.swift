//
//  CommentActionsProvider.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/25/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

public struct CommentActionsProvider {
    public let commentViewModel: CommentViewModel
    public let commentListViewModel: CommentListViewModel
    public let userIsStaff: Bool

    enum Action: Int, Identifiable {
        case flag
        case edit
        case delete
        case share

        var id: Int {
            rawValue
        }

        var title: String {
            switch self {
            case .flag:
                return Strings.flag.localized
            case .edit:
                return Strings.edit.localized
            case .delete:
                return Strings.delete.localized
            case .share:
                return Strings.share.localized
            }
        }

        var menuIconTitle: String {
            switch self {
            case .flag:
                return "flag"
            case .edit:
                return "pencil"
            case .delete:
                return "trash"
            case .share:
                return "square.and.arrow.up"
            }
        }
    }

    private func menuButton(type: Action) -> some View {
        Group {
            switch type {
            case .share:
                if let url = commentViewModel.permalink {
                    ShareLink(item: url) {
                        Label(type.title, systemImage: type.menuIconTitle)
                    }
                }
            default:
                Button(
                    role: type == .delete ? .destructive : nil,
                    action: buttonAction(type: type)
                ) {
                    Label(type.title, systemImage: type.menuIconTitle)
                }
            }
        }
        .foregroundColor(.chalk.dark800)
    }

    private var availableActions: [Action] {
        let canDelete = commentViewModel.canDelete(userIsStaff: userIsStaff)
        let canEdit = commentViewModel.isOwner && commentViewModel.tweetUrl == nil
        let canShare = commentViewModel.permalink != nil

        switch (canEdit, canDelete, canShare) {
        case (true, true, true):
            return [.edit, .delete, .share]
        case (true, true, false):
            return [.edit, .delete]
        case (false, true, true):
            return [.delete, .share]
        case (false, false, true):
            return [.flag, .share]
        default:
            return [.flag]
        }
    }

    private func buttonAction(type: Action) -> VoidClosure {
        switch type {
        case .edit:
            return {
                commentListViewModel.editingComment = commentViewModel
            }

        case .delete:
            return {
                commentListViewModel.selectedCommentID = commentViewModel.id
                commentListViewModel.isShowingDeleteConfirmation = true
            }
        case .flag:
            guard !commentViewModel.isStaff else { return {} }

            return {
                commentListViewModel.selectedCommentID = commentViewModel.id
                commentListViewModel.isShowingFlagAlert = true
            }
        case .share:
            return {}
        }
    }

    @ViewBuilder
    public var menuButtons: some View {
        let _ = DuplicateIDLogger.logDuplicates(in: availableActions)
        ForEach(availableActions) { menuButton(type: $0) }
    }
}
