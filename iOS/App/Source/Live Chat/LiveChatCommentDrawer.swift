//
//  LiveChatCommentDrawer.swift
//  theathletic-ios
//
//  Created by kevin fremgen on 7/13/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import AthleticComments
import AthleticFoundation
import AthleticUI
import SwiftUI

struct LiveChatCommentDrawer: View {
    @ObservedObject var viewModel: LiveRoomViewModel
    @ObservedObject var liveRoom: LiveRoom
    @ObservedObject var agoraManager: AgoraManager
    @Binding var text: String
    @Binding var sending: Bool
    @State var isActive: Bool = false
    var isCommentDrawerFocus: FocusState<Bool>.Binding
    var sendMessage: VoidClosure

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {

            Divider()
                .background(Color.chalk.dark300)
                .frame(height: 1)
                .frame(maxWidth: .infinity)

            commentDrawerControlls
                .padding(.vertical, 8)
                .padding(.horizontal, 16)

            if !viewModel.liveRoom.isChatDisabled {
                baseCommentDrawer
            }
        }
        .background(Color.chalk.dark200)

    }

    var commentDrawerControlls: some View {
        HStack(spacing: 6) {
            if viewModel.onStage {
                LiveRoomControlMuteButton(
                    viewModel: viewModel
                )
            } else {
                LiveRoomControlAskButton(
                    viewModel: viewModel,
                    agoraManager: viewModel.agoraManager
                )
            }

            if agoraManager.canGoLive {
                LiveRoomControlLiveButton(
                    viewModel: viewModel
                )
            }

            if agoraManager.canModerate {
                LiveRoomControlQueueButton(
                    viewModel: viewModel,
                    liveRoom: liveRoom
                )
            }

            if agoraManager.state == .promotedAudience {
                LiveRoomLeaveStageButton(
                    viewModel: viewModel,
                    agoraManager: viewModel.agoraManager
                )
            }
        }
    }

    var baseCommentDrawer: some View {
        VStack(spacing: 0) {
            Divider()
                .background(Color.chalk.dark300)
                .frame(height: 1)
                .frame(maxWidth: .infinity)

            /// Show if commentListFocus is active  or user has entered text
            if isActive || !text.isEmpty {
                HStack(spacing: 0) {

                    /// Comment label
                    CommentInputLabel(
                        type: .topLevel(title: viewModel.title)
                    )

                    Spacer(minLength: 80)

                    /// Dismiss button
                    TextEditorDismissButton {

                        Task {
                            await viewModel.dismissCommentAnalytics()
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
                .padding(.horizontal, 16)
                .padding(.top, 8)
            }

            VStack(spacing: 8) {
                ExpandingTextEditor(isCommentDrawerFocus: isCommentDrawerFocus, text: $text)

                /// Shows send button
                if isActive {
                    HStack {

                        Spacer()

                        TextEditorSendButton(text: $text, sending: $sending) {
                            sendMessage()
                        }
                    }
                }
            }
            .padding(.leading, isActive ? 12 : 16)
            .padding(.trailing, 16)
            .padding(.top, 12)
        }
        .padding(.bottom, isActive ? 12 : 0)
        .onChange(of: isCommentDrawerFocus.wrappedValue) { newValue in
            if newValue {
                /// Add delay updating `isActive` so  view can update after keyboard appears
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
                    self.isActive = newValue
                }
            } else {
                self.isActive = newValue
            }
        }
    }
}
