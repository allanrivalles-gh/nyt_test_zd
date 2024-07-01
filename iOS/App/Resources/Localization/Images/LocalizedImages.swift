//
//  LocalizedImages.swift
//  theathletic-ios
//
//  Created by Jan Remes on 08/09/2017.
//  Copyright © 2017 The Athletic. All rights reserved.
//

import Foundation
import UIKit

extension UIImage {

    class func localizedGiftAsset() -> UIImage? {

        if Locale.current.identifier.hasPrefix("fr") {
            return #imageLiteral(resourceName: "gift_banner_fr")
        } else {
            return #imageLiteral(resourceName: "Gift_Banner")
        }
    }
}
