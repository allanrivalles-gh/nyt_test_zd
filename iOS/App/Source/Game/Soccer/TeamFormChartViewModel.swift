//
//  TeamFormChartViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 5/7/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

struct TeamFormChartViewModel {
    struct Item {
        enum Style {
            case win
            case draw
            case loss
            case placeholder
        }

        let text: String?
        let style: Style
    }

    let items: [Item]
    let isChronological: Bool
}

extension TeamFormChartViewModel {

    /// Init from a string of W's, D's, T's & L's.
    /// - Parameters:
    ///   - form: Form string such as "WWDWL", must be provided in reverse chronological order (most recent first).
    ///   - displayCount: The number of game results to display. If there are less results than the given count, it will be padded with placeholders.
    init(form: String?, displayCount: Int = 5, locale: Locale = .current) {
        let form = form ?? ""
        let symbols: [String] =
            form.prefix(displayCount)
            .map { String($0) }

        let orderedSymbols: [String] = locale.isUS ? symbols : symbols.reversed()

        let gameItems: [TeamFormChartViewModel.Item] = orderedSymbols.map {
            TeamFormChartViewModel.Item(resultSymbol: $0)
        }

        let paddedPlaceholders: [TeamFormChartViewModel.Item] = Array(
            repeating: .init(text: nil, style: .placeholder),
            count: displayCount - gameItems.count
        )
        let items: [TeamFormChartViewModel.Item]
        if locale.isUS {
            items = gameItems + paddedPlaceholders
        } else {
            items = paddedPlaceholders + gameItems
        }

        self.init(items: items, isChronological: !locale.isUS)
    }
}

extension TeamFormChartViewModel.Item {

    /// The raw data string from the backend
    /// - Parameter resultSymbol: W / D / T / L
    init(resultSymbol: String) {
        /// We're intentionally checking cases using hardcoded strings rather than localized because this value is from the backend, not localized.
        switch resultSymbol {
        case "W":
            self.init(text: Strings.winSymbol.localized, style: .win)
        case "D":
            self.init(text: Strings.drawSymbol.localized, style: .draw)
        case "T":
            self.init(text: Strings.tieSymbol.localized, style: .draw)
        case "L":
            self.init(text: Strings.lossSymbol.localized, style: .loss)
        default:
            self.init(text: resultSymbol, style: .placeholder)
        }
    }
}
