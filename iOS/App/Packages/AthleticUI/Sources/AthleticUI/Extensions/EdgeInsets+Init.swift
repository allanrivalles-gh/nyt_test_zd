//
//  EdgeInsets+Init.swift
//
//
//  Created by Mark Corbyn on 27/6/2022.
//

import Foundation
import SwiftUI

extension EdgeInsets {

    public init(top: CGFloat, bottom: CGFloat) {
        self.init(top: top, leading: 0, bottom: bottom, trailing: 0)
    }

    public init(leading: CGFloat, trailing: CGFloat) {
        self.init(top: 0, leading: leading, bottom: 0, trailing: trailing)
    }

    public static func horizontal(_ inset: CGFloat) -> EdgeInsets {
        EdgeInsets(leading: inset, trailing: inset)
    }

    public static func vertical(_ inset: CGFloat) -> EdgeInsets {
        EdgeInsets(top: inset, bottom: inset)
    }

}
