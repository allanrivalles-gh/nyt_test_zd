//
//  LiveChatView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 10/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticAnalytics
import AthleticApolloTypes
import AthleticComments
import AthleticFoundation
import AthleticUI
import Combine
import SwiftUI

protocol LiveChatViewDelegate {
    func showPaywall(element: AnalyticsEvent.Element)
    func showProfile(viewModel: LiveChatMessageViewModel)
}

struct LiveChatView: View {
    enum ActiveAlert {
        case delete, lock
    }

    // MARK: - Properties
    @ObservedObject var viewModel: LiveRoomViewModel

    @EnvironmentObject private var entitlement: Entitlement
    @EnvironmentObject private var store: Store
    @EnvironmentObject private var compass: Compass

    @Environment(\.codeOfConductAgreement) private var agreement: CodeOfConductAgreement
    @StateObject var model: LiveChat
    @State private var inputText = ""
    @State private var idForAction: String = ""
    @State private var sending: Bool = false
    @State private var showingSuccessAlert: Bool = false
    @State private var showingActionAlert: Bool = false
    @State private var requiresCodeOfConduct: Bool = false
    @State private var activeAlert: ActiveAlert = .delete
    @State private var showingFlagAlert: Bool = false
    @State private var initialFetchComplete: Bool = false
    @State private var isFocused: Bool = false
    @State private var isProfileViewPresented: Bool = false
    @State private var isPaywallPresented: Bool = false
    @State private var scrollToBottom: Bool = false
    @State private var selectedMessage: LiveChatMessageViewModel? = nil
    @State private var visibleIds = Set<String>()
    @State private var visibilityFrame: CGRect?
    @State private var isLastRowVisible: Bool = false
    @State private var hasNewUnseenMessages: Bool = false
    @State private var anchoredRowId: String?
    @FocusState private var isCommentDrawerFocus: Bool

    let userIsModerator: Bool
    let messageFetchLimit: Int
    var logger = ATHLogger(category: .liveRooms)
    var delegate: LiveChatViewDelegate?

    private var isChatDisabled: Bool {
        viewModel.liveRoom.isChatDisabled
    }
    private var title: String {
        isChatDisabled ? Strings.roomChatStartDisabled.localized : Strings.roomChatStart.localized
    }
    private var subTitle: String {
        isChatDisabled ? Strings.roomChatFansDisabled.localized : Strings.roomChatFansHear.localized
    }

    private var actionAlertInfo: (title: String, button: some View) {
        switch activeAlert {
        case .delete:
            return (
                Strings.roomRemoveComment.localized,
                Button(Strings.roomRemoveConfirm.localized) {
                    Task {
                        await deleteComment()
                    }
                }
            )
        case .lock:
            return (
                Strings.roomLockUser.localized,
                Button(Strings.roomRemoveConfirm.localized) {
                    Task {
                        await lockUser()
                    }
                }
            )
        }
    }

    var body: some View {
        GeometryReader { proxy in
            VStack(alignment: .leading, spacing: 0) {
                if model.chatMessages.isEmpty {
                    Color.clear
                        .overlay(
                            VStack(spacing: 0) {
                                Spacer(minLength: 0)

                                Text(title)
                                    .fontStyle(.slab.m.bold)
                                    .foregroundColor(.chalk.dark800)
                                    .background(Color.clear)
                                    .frame(maxWidth: .infinity, alignment: .center)
                                    .padding([.leading, .trailing], 16)
                                    .padding(.bottom, 8)

                                Text(subTitle)
                                    .fontStyle(.calibreUtility.l.regular)
                                    .foregroundColor(.chalk.dark500)
                                    .background(Color.clear)
                                    .frame(maxWidth: .infinity, alignment: .center)
                                    .padding([.leading, .trailing], 16)
                                Spacer(minLength: 0)
                            }
                            .onSimultaneousTapGesture {
                                isCommentDrawerFocus = false
                            }
                        )
                } else {
                    ScrollViewReader { value in
                        ZStack(alignment: .bottom) {
                            ScrollView(.vertical, showsIndicators: true) {
                                LazyVStack {
                                    let _ = DuplicateIDLogger.logDuplicates(
                                        in: Array(model.chatMessages),
                                        id: \.id
                                    )
                                    ForEach(model.chatMessages, id: \.id) { messageViewModel in
                                        Button(action: {
                                            selectedMessage = messageViewModel
                                            isProfileViewPresented.toggle()
                                        }) {
                                            ZStack {
                                                Rectangle()
                                                    .fill(
                                                        messageViewModel.isHighlighted
                                                            ? Color.chalk.dark200 : .clear
                                                    )
                                                LiveChatRow(viewModel: messageViewModel)
                                                    .onAppear {
                                                        onRowVisibityChanged(
                                                            messageViewModel,
                                                            isVisible: true
                                                        )
                                                    }
                                                    .onDisappear {
                                                        onRowVisibityChanged(
                                                            messageViewModel,
                                                            isVisible: false
                                                        )
                                                    }
                                                    .visibilityTracker(
                                                        frame: visibilityFrame,
                                                        id: messageViewModel.id,
                                                        allVisible: $visibleIds
                                                    )
                                                    .padding(
                                                        EdgeInsets(
                                                            top: 8,
                                                            leading: 16,
                                                            bottom: 8,
                                                            trailing: 16
                                                        )
                                                    )
                                            }
                                        }
                                        .contextMenu {
                                            if model.userIsStaff || userIsModerator {
                                                Button {
                                                    idForAction = messageViewModel.id
                                                    activeAlert = .delete
                                                    showingActionAlert = true
                                                } label: {
                                                    Label(
                                                        Strings.roomRemoveComment.localized,
                                                        systemImage: "trash.fill"
                                                    )
                                                    .foregroundColor(.chalk.dark800)
                                                }
                                                Button {
                                                    idForAction = messageViewModel.userId
                                                    activeAlert = .lock
                                                    showingActionAlert = true
                                                } label: {
                                                    Label(
                                                        Strings.roomLockUser.localized,
                                                        systemImage: "lock.fill"
                                                    )
                                                    .foregroundColor(.chalk.dark800)
                                                }
                                            } else {
                                                Button {
                                                    idForAction = messageViewModel.id
                                                    showingFlagAlert = true
                                                } label: {
                                                    Label(
                                                        Strings.roomFlagComment.localized,
                                                        systemImage: "flag.fill"
                                                    )
                                                    .foregroundColor(.chalk.dark800)
                                                }
                                            }
                                        }
                                        .alert(
                                            actionAlertInfo.title,
                                            isPresented: $showingActionAlert
                                        ) {
                                            Button(Strings.cancel.localized, role: .cancel) {
                                                idForAction = ""
                                            }

                                            actionAlertInfo.button
                                        }
                                        .confirmationDialog(
                                            Strings.roomFlagComment.localized,
                                            isPresented: $showingFlagAlert,
                                            titleVisibility: .visible
                                        ) {
                                            Button(Strings.commentFlagReason1.localized) {
                                                Task {
                                                    await flagComment(reason: .abusive)
                                                }
                                            }
                                            Button(Strings.commentFlagReason2.localized) {
                                                Task {
                                                    await flagComment(reason: .trolling)
                                                }
                                            }
                                            Button(Strings.commentFlagReason3.localized) {
                                                Task {
                                                    await flagComment(reason: .spam)
                                                }
                                            }
                                            Button(Strings.cancel.localized, role: .cancel) {
                                                idForAction = ""
                                            }
                                        } message: {
                                            Text(
                                                "\(Strings.commentsReviewInfo.localized)?"
                                            )
                                        }
                                    }
                                    .onAppear {
                                        scrollToBottom = true
                                    }
                                    .onChange(of: scrollToBottom) { scroll in
                                        if scroll, let id = model.chatMessages.last?.id {
                                            withAnimation {
                                                value.scrollTo(id, anchor: .bottom)
                                            }
                                            scrollToBottom = false
                                        }
                                    }
                                }
                                .padding([.top, .bottom], 8)
                            }
                            .visibilityFrameProvider(frame: $visibilityFrame)
                            .onSimultaneousTapGesture {
                                isCommentDrawerFocus = false
                            }
                            .onSimultaneousDragGesture(onChanged: { _ in
                                isCommentDrawerFocus = false
                            })
                            .buttonStyle(PlainButtonStyle())
                            .bottomSheet(
                                isPresented: $isProfileViewPresented,
                                detents: [.medium]
                            ) {
                                if let message = selectedMessage,
                                    let user = viewModel.getUserFromChatMessage(
                                        viewModel: message
                                    )
                                {
                                    LiveRoomProfileView(user: user, roomId: viewModel.id)
                                        .globalEnvironment()
                                }
                            }

                            if !isLastRowVisible {
                                Button {
                                    scrollToBottom = true
                                } label: {
                                    HStack(spacing: 0) {
                                        Spacer()

                                        HStack(alignment: .center) {
                                            if hasNewUnseenMessages {
                                                Text(Strings.roomNewMessagesButton.localized)
                                                    .fontStyle(.calibreUtility.s.medium)
                                            }

                                            Image(systemName: "arrow.down")
                                                .frame(width: 8, height: 8)
                                                .darkScheme(isEnabled: !hasNewUnseenMessages)
                                        }
                                        .foregroundColor(.chalk.dark800)
                                        .padding(.vertical, 12)
                                        .padding(.horizontal, hasNewUnseenMessages ? 16 : 12)
                                        .background(
                                            ZStack {
                                                Color.chalk.dark300
                                                if !hasNewUnseenMessages {
                                                    LinearGradient.liveRoomGradient
                                                }
                                            }
                                        )
                                        .clipShape(Capsule())
                                        .padding(.trailing, hasNewUnseenMessages ? 0 : 24)
                                        .padding(.bottom, 24)

                                        if hasNewUnseenMessages {
                                            Spacer()
                                        }
                                    }
                                }
                            }
                        }
                        .onReceive(
                            NotificationCenter.default.publisher(
                                for: UIResponder.keyboardWillShowNotification
                            )
                        ) { _ in
                            onKeyboardWillAppear()
                        }
                        .onReceive(
                            NotificationCenter.default.publisher(
                                for: UIResponder.keyboardDidShowNotification
                            )
                        ) { _ in
                            onKeyboardDidAppear(scroll: value)
                        }
                    }
                }

                LiveChatCommentDrawer(
                    viewModel: viewModel,
                    liveRoom: viewModel.liveRoom,
                    agoraManager: viewModel.agoraManager,
                    text: $inputText,
                    sending: $sending,
                    isCommentDrawerFocus: $isCommentDrawerFocus,
                    sendMessage: {
                        Task {
                            await sendMessageHandler()
                        }
                    }
                )
                .frame(maxHeight: proxy.size.height)
                .fixedSize(horizontal: false, vertical: true)
            }
            .commentUndoToast(
                dismissedComment: $viewModel.dismissedComment,
                text: $inputText,
                isCommentDrawerFocus: $isCommentDrawerFocus
            ) {
                Task {
                    await viewModel.undoDismissCommentAnalytics()
                }

                return viewModel.undoDismissComment()
            }
            .overlay(
                AlertIconView(
                    show: $showingSuccessAlert,
                    iconName: "checkmark"
                )
                .padding([.top], -100)
            )
            .deeplinkListeningSheet(isPresented: $isPaywallPresented) {
                SubscriptionsNavigationView(
                    viewModel: SubscriptionsNavigationViewModel(
                        store: store,
                        entitlement: entitlement,
                        source: .liveRoom(
                            liveRoomId: viewModel.id,
                            action: .sendMessage
                        )
                    )
                )
            }
            .onReceive(model.newMessageSubject) { _ in
                if !isLastRowVisible && !hasNewUnseenMessages {
                    withAnimation {
                        hasNewUnseenMessages = true
                    }
                }
            }
            .codeOfConductRequired(requiresCodeOfConduct) {
                requiresCodeOfConduct = false
            }
            .task {
                guard !initialFetchComplete else {
                    return
                }
                if await model.getLatestMessages(limit: messageFetchLimit) {
                    initialFetchComplete = true
                }
            }
        }
    }

    private func sendMessageHandler() async {
        guard !sending else {
            return
        }
        guard model.userIsSubscriber else {
            isPaywallPresented = true
            return
        }
        guard agreement.isAgreed else {
            requiresCodeOfConduct = true
            return
        }
        guard
            model.status == .active,
            model.userIsSubscriber,
            !inputText.trimmingCharacters(
                in: .whitespacesAndNewlines
            ).isEmpty
        else {
            return
        }
        sending = true

        let success = await model.postMessage(message: inputText)
        sending = false
        if success {
            inputText = ""
            isCommentDrawerFocus = false
            scrollToBottom = true
        }
    }

    private func deleteComment() async {
        let success = await model.deleteMessage(messageId: idForAction)
        idForAction = ""
        if success {
            showingSuccessAlert = true
        }
    }

    private func flagComment(reason: GQL.ReportedReason) async {
        let success = await model.reportMessage(messageId: idForAction, reason: reason)
        idForAction = ""
        if success {
            showingSuccessAlert = true
        }
    }

    private func lockUser() async {
        guard model.userId != idForAction
        else {
            return
        }
        let success = await model.lockUser(userId: idForAction)
        idForAction = ""
        if success {
            showingSuccessAlert = true
        }
    }

    private func onKeyboardWillAppear() {
        let lastFullyVisibleRow = model.chatMessages
            .last { visibleIds.contains($0.id) }
        if let lastFullyVisibleRow = lastFullyVisibleRow {
            anchoredRowId = lastFullyVisibleRow.id
        }
    }

    private func onKeyboardDidAppear(scroll: ScrollViewProxy) {
        if let anchoredRowId = anchoredRowId {
            self.anchoredRowId = nil
            withAnimation {
                scroll.scrollTo(anchoredRowId, anchor: .bottom)
            }
        }
    }

    private func onRowVisibityChanged(_ row: LiveChatMessageViewModel, isVisible: Bool) {
        let isLast = row.id == model.chatMessages.last?.id
        if isLast {
            isLastRowVisible = isVisible
            if isVisible && hasNewUnseenMessages {
                hasNewUnseenMessages = false
            }
        }
    }
}
