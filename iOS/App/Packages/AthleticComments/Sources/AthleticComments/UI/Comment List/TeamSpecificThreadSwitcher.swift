//
//  TeamThreadSwitcher.swift
//
//
//  Created by kevin fremgen on 4/4/23.
//

import AthleticAnalytics
import AthleticUI
import SwiftUI

struct TeamSpecificThreadSwitcher: View {

    let currentThread: TeamSpecificThreadViewModel
    let otherThread: TeamSpecificThreadViewModel?
    @Binding var isShowingThreadSwticher: Bool
    let surface: AnalyticsCommentSpecification.Surface
    let switchAction: (String, AnalyticsCommentSpecification.Surface) async -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {

            VStack(alignment: .leading, spacing: 35) {

                HStack(spacing: 10) {

                    TeamLogoLazyImage(size: 32, resources: currentThread.teamLogos)
                        .frame(width: 48, height: 48)
                        .background(
                            Circle()
                                .fill(currentThread.teamColor)
                        )

                    Text(currentThread.longTitle)
                        .fontStyle(.slab.s.bold)
                        .foregroundColor(.chalk.dark800)
                        .lineLimit(nil)
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)

                }
                .frame(maxWidth: .infinity, alignment: .leading)

                if let otherThread {

                    HStack(spacing: 0) {
                        TeamLogoLazyImage(size: 32, resources: otherThread.teamLogos)
                            .frame(width: 48, height: 48, alignment: .center)

                        Spacer()
                            .frame(width: 10)

                        Text(otherThread.longTitle)
                            .fontStyle(.calibreUtility.l.regular)
                            .foregroundColor(.chalk.dark700)
                            .lineLimit(nil)
                            .multilineTextAlignment(.leading)
                            .fixedSize(horizontal: false, vertical: true)

                        Spacer(minLength: 4)

                        Image(systemName: "chevron.right")
                            .fontStyle(.calibreUtility.l.regular)
                            .foregroundColor(.chalk.dark800)

                    }
                    .onTapGesture {
                        isShowingThreadSwticher = false
                        Task {
                            await switchAction(otherThread.teamId, surface)
                        }
                    }
                }

            }

            Spacer(minLength: 16)

            Button {
                isShowingThreadSwticher = false
            } label: {
                Text(Strings.close.localized)
                    .fontStyle(.calibreUtility.xl.medium)
                    .foregroundColor(.chalk.dark800)
                    .frame(maxWidth: .infinity)
            }
            .frame(height: 48)
            .background(Color.chalk.dark300)
            .cornerRadius(2)

        }
    }
}
