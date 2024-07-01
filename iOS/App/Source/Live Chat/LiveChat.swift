//
//  LiveChat.swift
//  theathletic-ios
//
//  Created by Charles Huang on 10/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import Combine
import Foundation
import OrderedCollections
import UIKit

final class LiveChat: Hashable, ObservableObject {
    private struct Constants {
        static let messageLimit: Int = 1000
    }

    static func == (
        lhs: LiveChat,
        rhs: LiveChat
    ) -> Bool {
        return lhs.id == rhs.id
            && lhs.chatId == rhs.chatId
            && lhs.roomId == rhs.roomId
            && lhs.hostIds == rhs.hostIds
            && lhs.moderatorIds == rhs.moderatorIds
            && lhs.title == rhs.title
            && lhs.status == rhs.status
            && lhs.createdAt == rhs.createdAt
            && lhs.chatMessages == rhs.chatMessages
            && lhs.lockedUsers == rhs.lockedUsers
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(chatId)
        hasher.combine(roomId)
        hasher.combine(hostIds)
        hasher.combine(moderatorIds)
        hasher.combine(title)
        hasher.combine(status)
        hasher.combine(createdAt)
        hasher.combine(chatMessages)
        hasher.combine(lockedUsers)
    }

    let id: String
    private(set) var chatId: String?
    private(set) var roomId: String?
    private(set) var hostIds: [String] = []
    private(set) var moderatorIds: [String] = []
    private(set) var status: GQL.ChatRoomStatus
    private(set) var title: String?
    private(set) var createdAt: Date

    @Published var chatMessages = OrderedSet<LiveChatMessageViewModel>()
    @Published var lockedUsers: [String] = []

    private let network: LiveChatNetworking
    private let user: UserModel
    private let entitlement: Entitlement

    private var cancellables = Cancellables()
    private var liveChatUpdates: AnyCancellable?
    let newMessageSubject = PassthroughSubject<Void, Never>()

    lazy var logger = ATHLogger(category: .liveRooms)

    var setupComplete: Bool {
        chatId != nil
    }

    var userId: String {
        user.current?.id ?? ""
    }

    var userIsStaff: Bool {
        user.current?.isStaff == true
    }

    var userIsSubscriber: Bool {
        entitlement.hasAccessToContent
    }

    init(
        network: LiveChatNetworking = AppEnvironment.shared.network,
        user: UserModel = AppEnvironment.shared.user,
        entitlement: Entitlement = AppEnvironment.shared.entitlement
    ) {
        self.network = network
        self.user = user
        self.entitlement = entitlement

        id = UUID().uuidString
        status = .closed
        createdAt = Date()
        self.syncMessages([])

        $lockedUsers
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                self?.updateMessagesLockStatus()
            }
            .store(in: &cancellables)
    }

    func setupRoom(
        roomId: String,
        hostIds: [String],
        moderatorIds: [String],
        lockedUsers: [String],
        details: GQL.ChatRoomDetails
    ) {
        chatId = details.id
        self.roomId = roomId
        self.hostIds = hostIds
        self.moderatorIds = moderatorIds
        status = details.status
        title = details.title
        createdAt = details.createdAt
        setMessages(messages: details.messages)
        self.lockedUsers = lockedUsers
    }

    func updateStatus(status: GQL.ChatRoomStatus) {
        self.status = status
    }

    private func isHostMessage(messageDetails: GQL.ChatMessageDetails) -> Bool {
        guard
            let userId = messageDetails.createdBy.asStaff?.fragments.liveRoomStaff.id
                ?? messageDetails.createdBy.asCustomer?.id,
            hostIds.contains(userId)
        else {
            return false
        }
        return true
    }

    private func isModeratorMessage(messageDetails: GQL.ChatMessageDetails) -> Bool {
        guard
            let userId = messageDetails.createdBy.asStaff?.fragments.liveRoomStaff.id,
            moderatorIds.contains(userId)
        else {
            return false
        }
        return true
    }

    private func isShadowBannedMessage(messageDetails: GQL.ChatMessageDetails) -> Bool {
        /// If the current user is banned, let them see their own shadow banned messages
        /// If the current user is not banned, check to see if message was created by
        /// a shadow banned user. If yes, then we wil mark the message as isShadowBanned
        /// and not show this message
        guard let banned = user.current?.isShadowBan, !banned else {
            return user.current?.id == messageDetails.createdBy.asCustomer?.id
                ? false
                : true
        }
        return messageDetails.createdBy.asCustomer?.isShadowBan ?? false
    }

    private func setMessages(messages: [GQL.ChatRoomDetails.Message]) {
        chatMessages = OrderedSet(
            messages.compactMap { message in
                LiveChatMessageViewModel(
                    chatMessage: message.fragments.chatMessageDetails,
                    isHost: isHostMessage(messageDetails: message.fragments.chatMessageDetails),
                    isModerator: isModeratorMessage(
                        messageDetails: message.fragments.chatMessageDetails
                    ),
                    isLocked: lockedUsers.contains(where: {
                        $0
                            == message.fragments.chatMessageDetails.createdBy.asStaff?.fragments
                            .liveRoomStaff.id
                            ?? message.fragments.chatMessageDetails.createdBy.asCustomer?.id
                            ?? ""
                    }),
                    isShadowBanned: isShadowBannedMessage(
                        messageDetails: message.fragments.chatMessageDetails
                    )
                )
            }.filter({
                !$0.isShadowBanned
            }).sorted(by: {
                $0.createdAt < $1.createdAt
            })
        )
    }

    private func syncMessages(_ messages: [GQL.ChatMessageDetails], isNew: Bool = false) {
        messages.forEach { message in
            if let userId = message.createdBy.asStaff?.fragments.liveRoomStaff.id
                ?? message.createdBy.asCustomer?.id
            {
                if isNew {
                    newMessageSubject.send()
                }
                let isShadowBanned = isShadowBannedMessage(messageDetails: message)

                guard !isShadowBanned else {
                    return
                }

                let viewModel = LiveChatMessageViewModel(
                    chatMessage: message,
                    isHost: isHostMessage(messageDetails: message),
                    isModerator: isModeratorMessage(messageDetails: message),
                    isLocked: lockedUsers.contains(userId),
                    isShadowBanned: isShadowBanned
                )

                chatMessages.append(viewModel)
            }
        }
    }

    private func updateMessagesLockStatus() {
        chatMessages = OrderedSet(
            chatMessages.compactMap { message in
                var updatedMessage = message
                updatedMessage.updateLockedStatus(
                    locked: lockedUsers.contains(where: { $0 == message.userId })
                )
                return updatedMessage
            }
        )
    }

    private func removeMessage(id: String) {
        chatMessages.removeAll(where: {
            $0.id == id
        })
    }

    // MARK: Subscriptions

    func startLiveChatSubscription(completion: VoidCompletion? = nil) {
        guard let id = chatId else {
            return
        }
        network.subscribeToLiveChatUpdates(chatId: id)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveValue: { [weak self] nodeDetails in
                    if let newMessage = nodeDetails.asChatMessage {
                        self?.syncMessages(
                            [newMessage.fragments.chatMessageDetails],
                            isNew: true
                        )
                    }
                    if let deleteMessage = nodeDetails.asDeletedMessageEvent {
                        self?.removeMessage(id: deleteMessage.messageId)
                    }
                    completion?(.success)
                }
            )
            .store(in: &cancellables)
    }

    func stopSubscription() {
        liveChatUpdates?.cancel()
        liveChatUpdates = nil
    }

    // MARK: Query

    @MainActor
    func getLatestMessages(limit: Int) async -> Bool {
        guard let id = chatId else {
            return false
        }
        do {
            let messages = try await network.fetchLatestMessages(chatId: id, limit: limit)
            syncMessages(
                messages.compactMap {
                    $0?.fragments.chatMessageDetails
                }
            )

            return true
        } catch {
            logger.debug(
                "Fetch chat messages error: \(error)",
                .network
            )
            return false
        }
    }

    // MARK: Mutation

    @MainActor
    func postMessage(message: String) async -> Bool {
        guard let id = chatId else {
            return false
        }

        do {
            let message = try await network.postMessage(
                chatId: id,
                message: String(
                    message.stripHtmlTags().prefix(Constants.messageLimit)
                )
            )
            let metaBlob = AnalyticsEvent.MetaBlob(roomId: roomId)
            await Analytics.track(
                event: .init(
                    verb: .click,
                    view: .liveRoomMainStage,
                    element: .sendMessage,
                    objectType: .messageId,
                    objectIdentifier: message.createMessage.fragments.chatMessageDetails
                        .messageId,
                    metaBlob: metaBlob
                )
            )
            return true
        } catch {
            logger.debug(
                "Post chat messages error: \(error)",
                .network
            )
            return false
        }
    }

    @MainActor
    func deleteMessage(messageId: String) async -> Bool {
        guard let id = chatId else {
            return false
        }

        do {
            _ = try await network.deleteMessage(
                chatId: id,
                messageId: messageId
            )
            removeMessage(id: messageId)
            return true
        } catch {
            logger.debug(
                "Delete chat message error: \(error)",
                .network
            )
            return false
        }
    }

    @MainActor
    func reportMessage(messageId: String, reason: GQL.ReportedReason) async -> Bool {
        guard let id = chatId else {
            return false
        }
        do {
            _ = try await network.reportMessage(
                chatId: id,
                messageId: messageId,
                reason: reason
            )
            return true
        } catch {
            logger.debug(
                "Flag chat message error: \(error)",
                .network
            )
            return false
        }
    }

    @MainActor
    func blockUser(userId: String) async {
        guard let id = chatId else {
            return
        }
        do {
            _ = try await network.blockUser(
                chatId: id,
                userId: userId
            )
        } catch {
        }
    }

    @MainActor
    func lockUser(userId: String) async -> Bool {
        guard let roomId = roomId else {
            return false
        }
        do {
            _ = try await network.lockUserFromRoom(
                id: userId,
                fromRoom: roomId
            )
            return true
        } catch {
            logger.debug(
                "Lock user from room error: \(error)",
                .network
            )
            return false
        }
    }
}

// MARK: - Identifiable

extension LiveChat: Identifiable {}
