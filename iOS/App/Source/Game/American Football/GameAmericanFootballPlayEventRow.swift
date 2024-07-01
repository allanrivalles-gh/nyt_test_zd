//
//  GameAmericanFootballPlayEventRow.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 28/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticComments
import SwiftUI

struct GameAmericanFootballPlayEventRow: View {
    let viewModel: GameAmericanFootballPlayEventViewModel

    var body: some View {
        HStack(alignment: .firstTextBaseline, spacing: 0) {
            Group {
                if let clock = viewModel.clock {
                    Text(clock)
                        .foregroundColor(.chalk.dark500)
                        .fontStyle(.calibreUtility.s.regular)
                }
            }
            .padding(.leading, 4)
            .frame(width: 60)

            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 4) {
                    if let title = viewModel.title {
                        Text(title)
                            .foregroundColor(.chalk.dark700)
                            .fontStyle(.calibreUtility.l.medium)
                    }
                    if let suffix = viewModel.titleSuffix {
                        Text(suffix)
                            .foregroundColor(.chalk.dark500)
                            .fontStyle(.calibreUtility.s.regular)
                    }
                }

                Text(viewModel.subtitle)
                    .foregroundColor(.chalk.dark500)
                    .fontStyle(.calibreUtility.s.regular)
            }

            Spacer(minLength: 16)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 4)
        .commentablePlay(CommentingPlay(viewModel: viewModel))
    }
}

extension CommentingPlay {
    fileprivate init(viewModel: GameAmericanFootballPlayEventViewModel) {
        let description = [viewModel.titleSuffix, viewModel.subtitle]
            .compactMap { $0 }
            .joined(separator: "\n")
        self.init(
            id: viewModel.playId,
            description: description,
            occurredAtString: viewModel.occurredAtString
        )
    }
}

struct GameAmericanFootballPlayEventRow_Previews: PreviewProvider {
    static var viewModel: GameAmericanFootballPlayEventViewModel {
        GameAmericanFootballPlayEventViewModel(
            id: "the-id",
            clock: "01:59",
            title: "Drive Title",
            titleSuffix: "1st & 2 at MIA 30",
            subtitle:
                "Team does a thing as part of a drive. It needs a long subtitle to test word wrapping.",
            playId: "play-id",
            occurredAtString: "1667954357000"
        )
    }

    static var content: some View {
        GameAmericanFootballPlayEventRow(viewModel: viewModel)
    }

    static var previews: some View {
        content
            .preferredColorScheme(.dark)
            .previewLayout(.fixed(width: 375, height: 100))
        content
            .preferredColorScheme(.light)
            .previewLayout(.fixed(width: 375, height: 100))
    }
}
