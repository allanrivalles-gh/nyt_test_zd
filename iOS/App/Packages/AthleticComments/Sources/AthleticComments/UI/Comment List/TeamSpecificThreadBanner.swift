//
//  TeamSpecificThreadBanner.swift
//
//
//  Created by Jason Leyrer on 3/31/23.
//

import AthleticApolloTypes
import AthleticUI
import SwiftUI

struct TeamSpecificThreadBanner: View {
    let viewModel: TeamSpecificThreadViewModel
    let showChangeButton: Bool
    @Binding var isShowingThreadSwitcher: Bool

    var body: some View {
        HStack(spacing: 8) {
            TeamLogoLazyImage(size: 24, resources: viewModel.teamLogos)
                .frame(width: 24, height: 24)
            Text(viewModel.shortTitle)
                .fontStyle(.calibreUtility.s.medium)
                .foregroundColor(
                    Color.highContrastAppearance(
                        of: .chalk.dark700,
                        forBackgroundColor: viewModel.teamColor
                    )
                )
            Spacer(minLength: 0)

            if showChangeButton {
                Button {
                    isShowingThreadSwitcher = true
                } label: {
                    Text(Strings.change.localized)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(
                            Color.highContrastAppearance(
                                of: .chalk.dark700,
                                forBackgroundColor: viewModel.teamColor
                            )
                        )
                }
            }

        }
        .padding(.horizontal, 16)
        .padding(.vertical, 6)
        .background(viewModel.teamColor)
    }
}

struct TeamSpecificThreadBanner_Previews: PreviewProvider {
    private static let team = GQL.TeamV2(
        id: "UCpcTyLfqRlzhPdi",
        alias: "SJ",
        name: "San Jose Sharks",
        displayName: "Sharks",
        logos: [
            try! .init(
                GQL.TeamLogo(
                    id: "team-logo-27-72x72",
                    uri: "https://cdn-team-logos.theathletic.com/team-logo-27-72x72.png",
                    width: 72,
                    height: 72
                )
            ),
            try! .init(
                GQL.TeamLogo(
                    id: "team-logo-27-96x96",
                    uri: "https://cdn-team-logos.theathletic.com/team-logo-27-96x96.png",
                    width: 96,
                    height: 96
                )
            ),
        ],
        league: [
            .init(
                id: .nhl,
                alias: "NHL",
                name: "National Hockey League",
                displayName: "NHL",
                sport: .hockey
            )
        ]
    )

    private static let viewModel = TeamSpecificThreadViewModel(
        thread: .init(
            label: "You're in the discussion for San Jose Sharks followers",
            team: try! .init(team)
        )
    )!

    static var previews: some View {
        TeamSpecificThreadBanner(
            viewModel: viewModel,
            showChangeButton: false,
            isShowingThreadSwitcher: .constant(false)
        )
        TeamSpecificThreadBanner(
            viewModel: viewModel,
            showChangeButton: true,
            isShowingThreadSwitcher: .constant(false)
        )
    }
}
