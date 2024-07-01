//
//  TrackClickViewModifier.swift
//
//
//  Created by Jason Leyrer on 6/14/23.
//

import AthleticUI
import Foundation
import SwiftUI

extension View {
    public func trackClick(
        viewModel: Analytical,
        manager: AnalyticEventManager = AnalyticsManagers.events
    ) -> some View {
        modifier(
            TrackClickViewModifier(viewModel: viewModel, manager: manager)
        )
    }
}

private struct TrackClickViewModifier: ViewModifier {

    let viewModel: Analytical
    let manager: AnalyticEventManager

    func body(content: Self.Content) -> some View {
        content
            .onSimultaneousTapGesture {
                await viewModel.trackClickEvent(manager: manager)
            }
    }
}
