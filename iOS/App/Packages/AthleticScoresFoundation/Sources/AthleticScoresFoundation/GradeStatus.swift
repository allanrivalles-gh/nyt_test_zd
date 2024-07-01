//
//  GradeStatus.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 19/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

public enum GradeStatus: Hashable {
    case disabled
    case enabled
    case locked
    case other(String)
}
