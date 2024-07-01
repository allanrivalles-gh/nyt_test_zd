//
//  Strings.swift
//
//
//  Created by Jason Leyrer on 6/29/23.
//

import AthleticFoundation
import Foundation

public enum Strings: String, Localizable, CaseIterable {
    public var bundle: Bundle { .module }
    public var baseFilename: String { "en" }

    // MARK: - General

    case activity
    case genericError
    case moreUnread
    case notifications
    case updates

    // MARK: - Empty content

    case allNotificationsOffTitle
    case allNotificationsOffSubtitle
    case commentNotificationsOffTitle
    case commentNotificationsOffSubtitle
    case noActivityTitle
    case noActivitySubtitle
    case noUpdatesTitle
    case noUpdatesSubtitle
    case notSubscribedTitle
    case notSubscribedSubtitle
    case subscribeNow
    case goToSettings

    // MARK: Context Menus

    case markContentRead
    case markContentUnread
    case saveContent
    case share
    case unsaveContent
}
