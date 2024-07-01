//
//  Alert+Extension.swift
//
//
//  Created by Jason Xu on 1/20/23.
//

import Foundation
import SwiftUI

extension Alert {
    public func identifiable() -> IdentifiableAlert {
        IdentifiableAlert(alert: self)
    }
}

public struct IdentifiableAlert: Identifiable {
    public let alert: Alert
    public let id = UUID()
}
