//
//  GameBaseballPlayEventRow.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 8/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticScoresFoundation
import Foundation
import SwiftUI

struct GameBaseballPlayEventRow: View {
    let viewModel: GameBaseballPlayEventViewModel

    var body: some View {
        HStack(spacing: 0) {
            HStack(spacing: 12) {
                if let number = viewModel.number {
                    Text(number)
                        .fontStyle(.calibreUtility.xs.medium)
                        .foregroundColor(.chalk.dark800)
                        .frame(minWidth: 16, minHeight: 16)
                        .background(
                            viewModel.color?.clipShape(Circle())
                        )
                        .darkScheme()
                } else {
                    Color.clear.frame(width: 16, height: 16)
                }
                VStack(alignment: .leading, spacing: 0) {
                    Text(viewModel.title)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(.chalk.dark800)
                        .padding(.vertical, viewModel.subtitle == nil ? 12 : 0)
                        .fixedSize(horizontal: false, vertical: true)
                    if let subtitle = viewModel.subtitle {
                        Text(subtitle)
                            .fontStyle(.calibreUtility.xs.regular)
                            .foregroundColor(.chalk.dark500)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
            }
            Spacer(minLength: 0)
            if let icons = viewModel.icons {
                HStack(spacing: 12) {
                    BaseballBasesDiamond(highlighting: icons.basesHighlighting, baseSize: 7)
                        .padding(.trailing, 2)
                    Group {
                        if let color = viewModel.color, let position = icons.pitchPosition {
                            BaseballPitchZoneIcon(color: color, pitchPosition: position)
                        } else {
                            Rectangle().opacity(0)
                        }
                    }
                    .frame(width: 20, height: 20)
                    Group {
                        if let color = viewModel.color, let position = icons.hitPosition {
                            BaseballHitZoneIcon(color: color, hitPosition: position)
                        } else {
                            Rectangle().opacity(0)
                        }
                    }
                    .frame(width: 20, height: 20)
                }
            }
        }
        .padding(.vertical, 4)
    }
}

struct GameBaseballPlayEventRow_Previews: PreviewProvider {
    static var previews: some View {
        content
            .preferredColorScheme(.dark)
        content
            .preferredColorScheme(.light)
    }

    private static var content: some View {
        VStack(spacing: 0) {
            let _ = DuplicateIDLogger.logDuplicates(in: viewModels)
            ForEach(viewModels) {
                GameBaseballPlayEventRow(viewModel: $0)
            }
            Spacer()
        }
        .padding(.horizontal, 16)
    }

    static var viewModels: [GameBaseballPlayEventViewModel] {
        [
            GameBaseballPlayEventViewModel(
                id: "1",
                color: .chalk.red,
                number: "1",
                title: "Strike Looking",
                subtitle: "92mph slider",
                icons: .init(
                    basesHighlighting: .right,
                    pitchPosition: BaseballPitchZone(zone: 1).position,
                    hitPosition: BaseballHitZone(zone: 2).position
                )
            ),
            GameBaseballPlayEventViewModel(
                id: "2",
                color: nil,
                number: nil,
                title: "Ramírez scored on passed ball by Roberto Perez.",
                subtitle: nil,
                icons: .init(
                    basesHighlighting: [.left, .middle],
                    pitchPosition: nil,
                    hitPosition: BaseballHitZone(zone: 5).position
                )
            ),
            GameBaseballPlayEventViewModel(
                id: "3",
                color: .chalk.dark500,
                number: "3",
                title: "Strike Swinging",
                subtitle: "101mph breaking ball",
                icons: nil
            ),
            GameBaseballPlayEventViewModel(
                id: "4",
                color: .chalk.blue,
                number: "4",
                title: "Strike Swinging",
                subtitle: "101mph breaking ball",
                icons: .init(
                    basesHighlighting: .none,
                    pitchPosition: nil,
                    hitPosition: BaseballHitZone(zone: 3).position
                )
            ),
        ]
    }
}
