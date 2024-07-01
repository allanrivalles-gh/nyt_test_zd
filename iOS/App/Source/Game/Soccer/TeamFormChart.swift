//
//  TeamFormChart.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 5/7/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct TeamFormChart: View {
    let viewModel: TeamFormChartViewModel

    var body: some View {
        HStack(spacing: 2) {
            let _ = DuplicateIDLogger.logDuplicates(in: Array(viewModel.items.indices), id: \.self)
            ForEach(viewModel.items.indices, id: \.self) { index in
                let item = viewModel.items[index]
                TeamFormResultItem(
                    item: item,
                    offsetFromMostRecent: viewModel.isChronological
                        ? viewModel.items.endIndex - 1 - index
                        : index
                )
            }
        }
    }
}

struct CompactTeamFormChart: View {
    /// Compact, text-only. Used for Game modules in feeds

    let viewModel: TeamFormChartViewModel

    var body: some View {
        HStack(spacing: 0) {
            let _ = DuplicateIDLogger.logDuplicates(in: Array(viewModel.items.indices), id: \.self)
            ForEach(viewModel.items.indices, id: \.self) { index in
                let item = viewModel.items[index]

                if let text = item.text {
                    Text(text)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(
                            color(at: index, isChronological: viewModel.isChronological) ?? .clear
                        )
                }
            }
        }
    }

    private func color(at index: Int, isChronological: Bool) -> Color? {
        switch index {
        case 0:
            return isChronological ? .chalk.dark400 : .chalk.dark700
        case 1:
            return isChronological ? .chalk.dark500 : .chalk.dark600
        case 2:
            return .chalk.dark500
        case 3:
            return isChronological ? .chalk.dark600 : .chalk.dark500
        case 4:
            return isChronological ? .chalk.dark700 : .chalk.dark400
        default:
            return nil
        }
    }
}

private struct TeamFormResultItem: View {
    let item: TeamFormChartViewModel.Item
    let offsetFromMostRecent: Int

    var body: some View {
        Circle()
            .fill(circleColor)
            .frame(width: 16, height: 16)
            .overlay(
                Group {
                    if let text = item.text {
                        Text(text)
                            .fontStyle(.calibreUtility.xs.regular)
                            .foregroundColor(.chalk.dark800)
                            .lineLimit(1)
                            .darkScheme()
                            .offset(x: 0, y: -0.5)
                    }
                }
            )
            .opacity(opacity)
    }

    private var opacity: CGFloat {
        if case .placeholder = item.style {
            return 1
        } else {
            return (1 - Double(offsetFromMostRecent) * 0.15).clamped(to: 0.3...1)
        }
    }

    private var circleColor: Color {
        switch item.style {
        case .win:
            return .chalk.green
        case .draw:
            return .chalk.dark400
        case .loss:
            return .chalk.red
        case .placeholder:
            return .chalk.dark300
        }
    }
}

struct TeamFormChart_Previews: PreviewProvider {

    static var previews: some View {
        content
            .preferredColorScheme(.dark)
            .previewLayout(.sizeThatFits)
        content
            .preferredColorScheme(.light)
            .previewLayout(.sizeThatFits)
    }

    private static var content: some View {
        VStack {
            chronological
            reverseChronological
        }
    }

    private static let formString = "LDTW"

    @ViewBuilder
    private static var chronological: some View {
        Text("Chronological (Rest of World)")
            .fontStyle(.calibreUtility.xs.regular)

        TeamFormChart(
            viewModel: .init(
                form: formString,
                displayCount: 5,
                locale: Locale(identifier: "en_GB")
            )
        )
    }

    @ViewBuilder
    private static var reverseChronological: some View {
        Text("Reverse Chronological (US)")
            .fontStyle(.calibreUtility.xs.regular)

        TeamFormChart(
            viewModel: .init(
                form: formString,
                displayCount: 5,
                locale: Locale(identifier: "en_US")
            )
        )
    }

}
