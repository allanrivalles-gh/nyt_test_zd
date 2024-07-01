//
//  CompassExperiment.swift
//  theathletic-ios
//
//  Created by Jan Remes on 14/01/2020.
//  Copyright © 2020 The Athletic. All rights reserved.
//

import Foundation

enum CompassExperimentVariant: String, Codable, CaseIterable {
    case CTRL, A, B, C, D, E, F, G, H
}

class ABTests {
    let compass: Compass

    init(compass: Compass = AppEnvironment.shared.compass) {
        self.compass = compass
    }

    func getExperiment<T: CompassExperiment>(for type: T.Type) -> T? {
        compass.config.experiments[type] as? T
    }
}
