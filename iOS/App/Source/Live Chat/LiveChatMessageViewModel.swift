//
//  LiveChatMessageViewModel.swift
//  theathletic-ios
//
//  Created by Charles Huang on 10/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloTypes
import AthleticFoundation
import SwiftUI

struct LiveChatMessageViewModel: Hashable, Equatable {
    var initials: String {
        firstName.prefix(1).uppercased()
    }

    var displayName: String {
        guard !isModerator else {
            return Strings.theAthletic.localized
        }

        guard !lastName.isEmpty else {
            return firstName
        }
        return "\(firstName) \(lastName.prefix(1))."
    }

    var chatPreviewName: String {
        guard !lastName.isEmpty else {
            return firstName
        }
        return "\(firstName) \(lastName.prefix(1))"
    }

    var isNewsroom: Bool {
        guard !isModerator, isStaff, let userRole = self.role,
            [.author, .editor, .podcastHost, .podcastProducer].contains(userRole)
        else {
            return false
        }
        return true
    }

    var isHighlighted: Bool {
        guard isModerator || isNewsroom || isHost else {
            return false
        }

        return true
    }

    let id: String
    let chatId: String
    let type: GQL.ChatNodeType
    let isStaff: Bool
    let isHost: Bool
    let isModerator: Bool
    let isShadowBanned: Bool
    let role: GQL.UserRole?
    let userId: String
    let firstName: String
    let lastName: String
    let createdAt: Date
    let avatarUrl: String?
    private(set) var message: String
    private(set) var isUserLocked: Bool

    init(
        chatMessage: GQL.ChatMessageDetails,
        isHost: Bool,
        isModerator: Bool,
        isLocked: Bool,
        isShadowBanned: Bool
    ) {
        id = chatMessage.messageId
        chatId = chatMessage.id
        type = chatMessage.type
        isStaff = chatMessage.createdBy.asStaff != nil
        self.isHost = isHost
        self.isModerator = isModerator
        self.isShadowBanned = isShadowBanned
        userId =
            chatMessage.createdBy.asStaff?.fragments.liveRoomStaff.id
            ?? chatMessage.createdBy.asCustomer?.id ?? ""
        firstName =
            chatMessage.createdBy.asStaff?.fragments.liveRoomStaff.firstName
            ?? chatMessage.createdBy.asCustomer?.firstName ?? ""
        lastName =
            chatMessage.createdBy.asStaff?.fragments.liveRoomStaff.lastName
            ?? chatMessage.createdBy.asCustomer?.lastName ?? ""
        message = chatMessage.message
        createdAt = chatMessage.createdAt
        isUserLocked = isLocked
        role = chatMessage.createdBy.asStaff?.fragments.liveRoomStaff.role

        if let url = chatMessage.createdBy.asStaff?.fragments.liveRoomStaff.avatarUri,
            !url.isEmpty
        {
            avatarUrl = url
        } else {
            avatarUrl = nil
        }
    }

    mutating func updateLockedStatus(locked: Bool) {
        isUserLocked = locked
    }
}

// MARK: - Identifiable

extension LiveChatMessageViewModel: Identifiable {}

// MARK: - StorageObject

extension LiveChatMessageViewModel: StorageObject {
    var storageIdentifier: String {
        return id
    }
}
