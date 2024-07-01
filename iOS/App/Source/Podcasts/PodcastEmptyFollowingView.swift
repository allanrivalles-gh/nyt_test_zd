//
//  PodcastEmptyFollowingView.swift
//  theathletic-ios
//
//  Created by Andrew Fannin on 9/21/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

struct PodcastEmptyFollowingView: View {

    let discoverButtonAction: VoidClosure

    var body: some View {
        VStack(spacing: 32) {
            Image("icn_listen_large")
                .renderingMode(.template)
                .foregroundColor(.chalk.dark700)
                .frame(width: 62, height: 56)
                .padding(.top, 164)

            VStack(spacing: 12) {
                Text(Strings.titleEmptyPodcast.localized)
                    .tracking(0.01)
                    .foregroundColor(.chalk.dark800)
                    .fontStyle(.calibreHeadline.s.semibold)

                Text(Strings.infoEmptyPodcast.localized)
                    .tracking(0.25)
                    .lineSpacing(1.225)
                    .foregroundColor(.chalk.dark500)
                    .fontStyle(.calibreUtility.l.regular)
                    .multilineTextAlignment(.center)
            }
            .padding(.horizontal, 24)

            Button(action: {
                discoverButtonAction()
            }) {
                Text(Strings.actionEmptyPodcast.localized)
                    .tracking(0.15)
                    .foregroundColor(.chalk.dark200)
                    .lineLimit(1)
                    .fontStyle(.calibreUtility.s.medium)
                    .frame(height: 40)
                    .padding(.horizontal, 24)
            }
            .background(Color.chalk.dark800)
            .cornerRadius(2)

            Spacer()
        }
        .background(
            LinearGradient(
                gradient: Gradient(colors: [.chalk.dark200, .chalk.dark300]),
                startPoint: .bottom,
                endPoint: .top
            )
        )
    }
}

struct EmptyFollowingView_Preview: PreviewProvider {
    static var previews: some View {
        PodcastEmptyFollowingView {}.preferredColorScheme(.light)
    }
}
