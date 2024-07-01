//
//  Experiments.swift
//  theathletic-ios
//
//  Created by Jason Xu on 11/29/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import Foundation

struct ArticleBoxScoreExperiment: CompassExperiment {
    static var id: String = CompassExperimentIdentifier.articleBoxScore.rawValue
    var variant: CompassExperimentVariant
    let isEnabled: Bool?
}
