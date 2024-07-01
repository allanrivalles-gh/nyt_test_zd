//
//  CommentReplyView.swift
//
//
//  Created by Jason Leyrer on 11/21/22.
//

import AthleticApolloTypes
import AthleticUI
import SwiftUI

struct CommentReplyView: View {
    @Environment(\.commentingUser) private var commentingUser
    @Environment(\.codeOfConductAgreement) private var agreement: CodeOfConductAgreement
    @Environment(\.openURL) private var openUrl

    @ObservedObject var viewModel: CommentListViewModel

    @Binding var text: String
    @Binding var sending: Bool
    var isCommentDrawerFocus: FocusState<Bool>.Binding
    @Binding var isShowingTemporaryBan: Bool

    @State private var temporaryBanDaysRemaining: Int? = nil
    @State private var requiresCodeOfConduct = false

    let surface: AnalyticsCommentSpecification.Surface

    private var showsFollowingText: Bool {
        return isCommentDrawerFocus.wrappedValue && isShowingTemporaryBan == false
            && viewModel.showsCommentDrawerFlairs
    }

    private var isActive: Bool {
        isCommentDrawerFocus.wrappedValue
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {

            Divider()
                .background(Color.chalk.dark300)
                .frame(height: 1)
                .frame(maxWidth: .infinity)

            /// Show if commentListFocus is active or is showing banned or user has entered text
            if isActive || isShowingTemporaryBan || !text.isEmpty {
                HStack(spacing: 0) {

                    /// Comment label
                    commentLabel

                    Spacer(minLength: 80)

                    /// Dismiss button
                    dismissButton
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
            }

            if let temporaryBanDaysRemaining, isShowingTemporaryBan {
                VStack(spacing: 16) {
                    CommentInfoBanner(
                        type: .temporaryBan(
                            temporaryBanViewModel: .init(daysRemaining: temporaryBanDaysRemaining)
                        )
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 8)

                    Divider()
                        .edgesIgnoringSafeArea(.horizontal)
                        .background(Color.chalk.dark300)
                        .frame(height: 1)
                        .frame(maxWidth: .infinity)
                }
            }

            if showsFollowingText {
                CommentInfoBanner(type: .flair(viewModel.flairs))
                    .padding(.horizontal, 16)
                    .padding(.top, 7)
            }

            VStack(spacing: 8) {
                ExpandingTextEditor(isCommentDrawerFocus: isCommentDrawerFocus, text: $text)

                /// Shows send button
                if isActive {
                    HStack {

                        Spacer()

                        TextEditorSendButton(text: $text, sending: $sending) {
                            guard agreement.isAgreed else {
                                requiresCodeOfConduct = true
                                return
                            }

                            Task {
                                sending = true

                                if viewModel.commentingPlay != nil {
                                    await viewModel.publishPlayComment(text: text)
                                } else {
                                    await viewModel.publishComment(text: text)
                                }

                                text = ""
                                sending = false
                                isCommentDrawerFocus.wrappedValue = false
                            }
                        }
                        .codeOfConductRequired(requiresCodeOfConduct) {
                            requiresCodeOfConduct = false
                        }
                    }
                }
            }
            /// Aligns text editor with comment input label
            /// Reason for this is text editor adds insets so this take into account the insets
            .padding(.leading, isActive ? 12 : 16)
            .padding(.trailing, 16)
            .padding(.top, showsFollowingText ? 6 : 12)
            .padding(.bottom, isActive ? 12 : 20)
            .onChange(of: isCommentDrawerFocus.wrappedValue) { focused in
                /// Check to make sure comment text editor is active
                guard isActive else { return }

                /// Make sure we can get commenting user  and banning info
                guard let user = commentingUser, let temporaryBanEndDate = user.temporaryBanEndDate,
                    temporaryBanEndDate.isFuture
                else {
                    return
                }

                /// Make changes to show ban
                isCommentDrawerFocus.wrappedValue = false
                text = ""
                isShowingTemporaryBan = true
                temporaryBanDaysRemaining = temporaryBanEndDate.numberOfDaysFromNow

            }
        }
        .background(Color.chalk.dark200)
    }

    /// Responsible for showing the correct label based on the type of comment
    @ViewBuilder
    private var commentLabel: some View {
        if isShowingTemporaryBan {
            CommentInputLabel(type: .banned)
        } else if let selectedReplyComment = viewModel.selectedReplyComment {
            if selectedReplyComment.isFromTheAthletic {
                CommentInputLabel(type: .replyTheAthletic)
            } else {
                CommentInputLabel(
                    type: .reply(
                        title: selectedReplyComment.author,
                        authorInformation: .init(
                            avatarInitial: selectedReplyComment.authorInitial,
                            avatarBackgroundColorHex: selectedReplyComment.authorAvatarColor,
                            avatarSize: 16,
                            gameFlairs: selectedReplyComment.authorGameFlairs,
                            isStaff: selectedReplyComment.isStaff,
                            imageUrl: selectedReplyComment.authorAvatarImageUrl
                        )
                    )
                )
            }
        } else if let commentingPlay = viewModel.commentingPlay {
            CommentInputLabel(type: .play(play: commentingPlay))
        } else if viewModel.editingComment != nil {
            CommentInputLabel(type: .edit)
        } else {
            CommentInputLabel(
                type: .topLevel(title: viewModel.title)
            )
        }
    }

    /// Dismisses the comment draw and stops text editing
    @ViewBuilder
    private var dismissButton: some View {

        if isShowingTemporaryBan {
            TextEditorDismissButton {
                Task {
                    await viewModel.dismissCommentAnalytics(surface: surface)
                }

                isShowingTemporaryBan = false
            }
        } else {
            TextEditorDismissButton {
                Task {
                    await viewModel.dismissCommentAnalytics(surface: surface)
                }

                let dismissedText = text
                /// Reset dismissed state
                text = ""
                isCommentDrawerFocus.wrappedValue = false

                withAnimation {
                    viewModel.dismissComment(with: dismissedText)
                }
            }
        }
    }
}
