//
//  CopyLinkActivity.swift
//  theathletic-ios
//
//  Created by Jan Remes on 24/07/2018.
//  Copyright © 2018 The Athletic. All rights reserved.
//

import Foundation
import UIKit

/// #### A UIActivity subclass that copy link to pasteboard
open class CopyLinkActivity: UIActivity {

    private var url: URL?
    private let activityTypeString: String = "ATHCopyLinkActivity"

    public override init() {
        super.init()
    }

    open override var activityType: UIActivity.ActivityType? {
        return UIActivity.ActivityType(activityTypeString)
    }

    public override var activityTitle: String? {
        return Strings.copyLink.localized
    }

    open override var activityImage: UIImage? {
        UIImage(systemName: "link")
    }

    open override func perform() {
        guard let url = url else {
            activityDidFinish(false)
            return
        }

        UIPasteboard.general.string = url.absoluteString
    }

    open override func canPerform(withActivityItems activityItems: [Any]) -> Bool {
        for activityItem in activityItems where activityItem as? URL != nil {
            return true
        }
        return false
    }

    open override func prepare(withActivityItems activityItems: [Any]) {
        for activityItem in activityItems {
            if let url = activityItem as? URL {
                self.url = url
            }
        }
    }
}
