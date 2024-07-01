//
//  DockingCoordinator.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 4/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import UIKit

final class DockingCoordinator: ObservableObject {
    @Published private(set) var dockedViewModel: MiniAudioPlayerViewModel?

    func dock(viewModel: MiniAudioPlayerViewModel) {
        if let existingViewModel = dockedViewModel {
            guard !existingViewModel.requiresExplicitDismissal else {
                /// A new mini player cannot be docked while this one is docked.
                return
            }

            undock()
        }

        dockedViewModel = viewModel
    }

    func undock() {
        dockedViewModel?.willUndock?()
        dockedViewModel = nil
    }
}
