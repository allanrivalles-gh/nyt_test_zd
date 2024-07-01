//
//  LiveChatAvatarView.swift
//  theathletic-ios
//
//  Created by Charles Huang on 10/25/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticUI
import SwiftUI

struct LiveChatAvatarView: View {
    let avatarUrl: String?
    let userId: String
    let initials: String
    let dimension: CGFloat

    var body: some View {
        ZStack {
            Circle()
                .fill(
                    avatarUrl != nil
                        ? Color(.clear)
                        : userId.isEmpty
                            ? .chalk.dark100
                            : LiveChatColors.color(forId: userId)
                )
            Text(
                avatarUrl != nil
                    ? ""
                    : initials
            )
            .fontStyle(.calibreUtility.xs.regular)
            .foregroundColor(.chalk.constant.gray800)
            .multilineTextAlignment(.center)
            .padding(.top, -1)

            if let urlString = avatarUrl, let url = URL(string: urlString) {
                PlaceholderLazyImage(
                    imageUrl: url,
                    modifyImage: {
                        $0.aspectRatio(contentMode: .fill)
                    }
                )
                .clipShape(Circle())
            }
        }
        .frame(width: dimension, height: dimension)
        .clipShape(Circle())
    }
}
