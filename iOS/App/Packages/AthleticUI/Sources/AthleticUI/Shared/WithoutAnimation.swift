//
//  WithoutAnimation.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 09/01/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import SwiftUI

/// copied from https://stackoverflow.com/a/72973172/8633918

public func withoutAnimation(action: @escaping () -> Void) {
    var transaction = Transaction()
    transaction.disablesAnimations = true
    withTransaction(transaction, action)
}
