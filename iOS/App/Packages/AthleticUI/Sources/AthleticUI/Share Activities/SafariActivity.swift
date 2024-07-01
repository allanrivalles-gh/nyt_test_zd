//
//  SafariActivity.swift
//  theathletic-ios
//
//  Created by Jan Remes on 31/10/2017.
//  Copyright © 2017 The Athletic. All rights reserved.
//

import Foundation
import UIKit

/// #### A UIActivity subclass that opens URLs in Safari
/// You can specify a custom at init if you don't want to use the default icon
open class SafariActivity: UIActivity {
    private var url: URL?
    private var image: UIImage?
    private let ATHSafariActivityType: String = "ATHSafariActivity"

    public override init() {
        super.init()
    }

    public init(image: UIImage) {
        super.init()
        self.image = image
    }

    open override var activityType: UIActivity.ActivityType? {
        return UIActivity.ActivityType(ATHSafariActivityType)
    }

    open override var activityTitle: String? {
        return Strings.openInSafari.localized
    }

    open override var activityImage: UIImage? {
        if let image = self.image {
            return image
        }

        return #imageLiteral(resourceName: "KRSafariActivity")
    }

    open override func perform() {
        guard let url = url else {
            activityDidFinish(false)
            return
        }

        UIApplication.shared.open(url, options: [:]) { finished in
            self.activityDidFinish(finished)
        }
    }

    open override func canPerform(withActivityItems activityItems: [Any]) -> Bool {
        for activityItem in activityItems {
            if let url = activityItem as? URL {
                if UIApplication.shared.canOpenURL(url) {
                    return true
                }
            }
        }
        return false
    }

    open override func prepare(withActivityItems activityItems: [Any]) {
        for activityItem in activityItems {
            if let url = activityItem as? URL {
                if UIApplication.shared.canOpenURL(url) {
                    self.url = url
                    return
                }
            }
        }
    }
}
