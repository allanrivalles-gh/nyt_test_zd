//
//  SharingSourceProvider.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 6/30/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import UIKit

public final class SharingItemSourceProvider: NSObject, UIActivityItemSource {
    public let title: String

    public init(title: String) {
        self.title = title
    }

    public func activityViewControllerPlaceholderItem(
        _ activityViewController: UIActivityViewController
    ) -> Any {
        ""
    }

    public func activityViewController(
        _ activityViewController: UIActivityViewController,
        itemForActivityType activityType: UIActivity.ActivityType?
    ) -> Any? {
        switch activityType {
        case .some(.postToTwitter):
            return "\(title)\n\n\(Strings.viaTheAthleticTwitter.localized) "
        default:
            return nil
        }
    }
}
