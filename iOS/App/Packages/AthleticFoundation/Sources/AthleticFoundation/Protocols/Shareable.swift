//
//  Shareable.swift
//
//
//  Created by Jason Xu on 10/4/23.
//

import Foundation
import SwiftUI

public struct ShareItem {
    public let url: URL
    public let title: String?

    public var titleText: Text? {
        guard let title else { return nil }
        /// We add an extra space at the end so that the user can add additional text to post without needing to type a space first
        return Text(title + " ")
    }
}

public protocol Shareable {
    var permalinkUrl: URL? { get }
    var shareTitle: String? { get }
}

extension Shareable {
    public var shareItem: ShareItem? {
        guard let url = permalinkUrl else { return nil }

        return ShareItem(url: url, title: shareTitle)
    }
}
