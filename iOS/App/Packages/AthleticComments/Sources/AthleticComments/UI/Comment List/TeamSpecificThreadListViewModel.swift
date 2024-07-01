//
//  TeamSpecificThreadListViewModel.swift
//
//
//  Created by kevin fremgen on 4/5/23.
//

import Foundation

public struct TeamSpecificThreadListViewModel {
    public let currentThread: TeamSpecificThreadViewModel
    public let otherThread: TeamSpecificThreadViewModel?
    public let teamThreads: [TeamSpecificThreadViewModel]

    public init(
        currentThread: TeamSpecificThreadViewModel,
        teamThreads: [TeamSpecificThreadViewModel]
    ) {
        self.currentThread = currentThread
        self.teamThreads = teamThreads
        self.otherThread = teamThreads.first { teamThread in
            teamThread != currentThread
        }
    }
}
