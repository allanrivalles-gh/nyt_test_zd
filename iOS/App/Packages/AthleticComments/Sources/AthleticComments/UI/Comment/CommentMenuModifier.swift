//
//  CommentMenuModifier.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 2/25/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticUI
import SwiftUI

/// View modifier for displaying comment actions from the ellipsis

struct CommentMenuModifier: ViewModifier {
    let commentListViewModel: CommentListViewModel
    let viewModel: CommentViewModel
    @Environment(\.commentingUser) private var user

    public func body(content: Content) -> some View {
        Menu {
            CommentActionsProvider(
                commentViewModel: viewModel,
                commentListViewModel: commentListViewModel,
                userIsStaff: user?.isStaff ?? false
            )
            .menuButtons
        } label: {
            content
        }
    }
}

struct CommentAlertSheetsModifier: ViewModifier {

    @ObservedObject var commentListViewModel: CommentListViewModel
    let surface: AnalyticsCommentSpecification.Surface

    func body(content: Content) -> some View {
        content
            .confirmationDialog(
                Strings.flagComment.localized,
                isPresented: $commentListViewModel.isShowingFlagAlert,
                titleVisibility: .visible
            ) {
                Button(Strings.commentFlagReason1.localized) {
                    if let commentId = commentListViewModel.selectedCommentID {
                        Task {
                            await commentListViewModel.flagComment(
                                commentId: commentId,
                                reason: .abusive,
                                surface: surface
                            )
                        }
                    }
                }

                Button(Strings.commentFlagReason2.localized) {
                    if let commentId = commentListViewModel.selectedCommentID {
                        Task {
                            await commentListViewModel.flagComment(
                                commentId: commentId,
                                reason: .trolling,
                                surface: surface
                            )
                        }
                    }
                }

                Button(Strings.commentFlagReason3.localized) {
                    if let commentId = commentListViewModel.selectedCommentID {
                        Task {
                            await commentListViewModel.flagComment(
                                commentId: commentId,
                                reason: .spam,
                                surface: surface
                            )
                        }
                    }
                }
            } message: {
                Text(Strings.commentsReviewInfo.localized)
            }
            .alert(
                Strings.deleteComment.localized,
                isPresented: $commentListViewModel.isShowingDeleteConfirmation
            ) {
                Button(Strings.cancel.localized, role: .cancel) {}
                Button(Strings.delete.localized, role: .destructive) {
                    if let commentId = commentListViewModel.selectedCommentID {
                        Task {
                            await commentListViewModel.deleteComment(
                                commentId: commentId
                            )
                        }
                    }
                }
            } message: {
                Text(Strings.areYouSure.localized)
            }
    }
}

extension View {
    public func commentMenu(
        commentListViewModel: CommentListViewModel,
        viewModel: CommentViewModel
    ) -> some View {
        ModifiedContent(
            content: self,
            modifier: CommentMenuModifier(
                commentListViewModel: commentListViewModel,
                viewModel: viewModel
            )
        )
    }
}

extension View {
    public func commentAlertSheets(
        commentListViewModel: CommentListViewModel,
        surface: AnalyticsCommentSpecification.Surface
    ) -> some View {
        ModifiedContent(
            content: self,
            modifier: CommentAlertSheetsModifier(
                commentListViewModel: commentListViewModel,
                surface: surface
            )
        )
    }
}
