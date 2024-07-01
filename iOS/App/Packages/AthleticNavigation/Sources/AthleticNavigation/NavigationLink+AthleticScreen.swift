//
//  NavigationLink+AthleticScreen.swift
//
//  Created by Mark Corbyn on 5/6/2023.
//

import SwiftUI

extension NavigationLink where Destination == Never {

    public init(screen: AthleticScreen, @ViewBuilder label: () -> Label) {
        self.init(value: screen, label: label)
    }

}
