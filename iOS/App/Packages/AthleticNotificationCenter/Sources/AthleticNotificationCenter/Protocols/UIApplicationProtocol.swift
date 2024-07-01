//
//  UIApplicationProtocol.swift
//
//
//  Created by Jason Leyrer on 8/7/23.
//

import Foundation
import UIKit

public protocol UIApplicationProtocol {
    var applicationIconBadgeNumber: Int { get set }
}

extension UIApplication: UIApplicationProtocol {}
