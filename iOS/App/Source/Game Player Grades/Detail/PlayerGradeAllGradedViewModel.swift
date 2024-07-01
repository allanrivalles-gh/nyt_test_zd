//
//  PlayerGradeAllGradedViewModel.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 22/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

struct PlayerGradeAllGradedViewModel: Hashable, Identifiable {
    let id: String

    var title: String {
        Strings.playerGradesTitle.localized
    }
    var gradedAllPlayersText: String {
        Strings.gradedAllPlayers.localized
    }
    var seeAllPlayerGradesText: String {
        Strings.seeAllPlayerGrades.localized
    }
}
