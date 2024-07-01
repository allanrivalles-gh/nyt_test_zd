//
//  LiveChatPreviewChatView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 12/8/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

struct LiveChatPreviewChatView: View {
    let message: LiveChatMessageViewModel

    var body: some View {
        ZStack(alignment: .trailing) {
            LiveChatAvatarView(
                avatarUrl: message.avatarUrl,
                userId: message.userId,
                initials: message.initials,
                dimension: 28
            )
            .padding([.leading], 16)

            if message.isStaff {
                Image("social_auth")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 12, height: 12, alignment: .leading)
                    .padding([.top], 18)
                    .padding([.trailing], -2)
            }
        }
        Text("**\(message.chatPreviewName):** \(message.message)")
            .multilineTextAlignment(.leading)
            .lineLimit(2)
            .fontStyle(.calibreUtility.s.regular)
            .foregroundColor(.chalk.dark600)
            .padding([.leading], 12)
        Spacer()
    }
}
