//
//  LiveChatRow.swift
//  theathletic-ios
//
//  Created by Charles Huang on 11/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

struct LiveChatRow: View {
    @EnvironmentObject private var user: UserModel
    let viewModel: LiveChatMessageViewModel

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack(alignment: .center, spacing: 8) {
                if viewModel.isModerator {
                    ZStack(alignment: .center) {
                        Circle()
                            .fill(Color.chalk.dark300)
                            .frame(width: 16, height: 16)
                            .clipShape(Circle())
                            .darkScheme()
                        Image("social_athletic_icon")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 8, height: 8)
                    }
                } else {
                    LiveChatAvatarView(
                        avatarUrl: viewModel.avatarUrl,
                        userId: viewModel.userId,
                        initials: String(viewModel.initials),
                        dimension: 16
                    )
                    .padding(.top, 1)
                }

                Text(viewModel.displayName)
                    .fontStyle(.calibreUtility.s.regular)
                    .foregroundColor(.chalk.dark600)
                    .lineLimit(1)

                if viewModel.isNewsroom || viewModel.isHost {
                    ZStack(alignment: .center) {
                        Rectangle()
                            .fill(Color.chalk.dark100)
                            .frame(width: 38, height: 14)
                            .clipShape(Capsule())
                        Text(
                            viewModel.isHost
                                ? Strings.roomRequestsHost.localized.uppercased()
                                : Strings.staff.localized
                        )
                        .font(Font(UIFont.font(name: .calibreMedium, size: 10)))
                        .foregroundColor(.chalk.dark800)
                        .padding(.top, -1)
                    }
                } else if viewModel.isUserLocked,
                    user.current?.isStaff == true
                {
                    Image(systemName: "lock.fill")
                        .resizable()
                        .scaledToFit()
                        .foregroundColor(.chalk.red)
                        .frame(width: 10, height: 10)
                }
                Spacer()
            }

            Text(viewModel.message)
                .fontStyle(.calibreUtility.l.regular)
                .foregroundColor(.chalk.dark700)
                .multilineTextAlignment(.leading)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}
