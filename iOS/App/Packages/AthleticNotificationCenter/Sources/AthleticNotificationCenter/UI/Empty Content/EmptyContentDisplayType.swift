//
//  EmptyContentDisplayType.swift
//
//
//  Created by Jason Leyrer on 7/13/23.
//

import Foundation

enum EmptyContentDisplayType {
    case allNotificationsOff
    case commentNotificationsOff
    case noActivity
    case noUpdates
    case notSubscribed

    var iconImageName: String {
        self == .notSubscribed ? "icon_comments_with_padding" : "icn_menu_notifications"
    }

    var title: String {
        switch self {
        case .allNotificationsOff:
            return Strings.allNotificationsOffTitle.localized
        case .commentNotificationsOff:
            return Strings.commentNotificationsOffTitle.localized
        case .noActivity:
            return Strings.noActivityTitle.localized
        case .noUpdates:
            return Strings.noUpdatesTitle.localized
        case .notSubscribed:
            return Strings.notSubscribedTitle.localized
        }
    }

    var subtitle: String {
        switch self {
        case .allNotificationsOff:
            return Strings.allNotificationsOffSubtitle.localized
        case .commentNotificationsOff:
            return Strings.commentNotificationsOffSubtitle.localized
        case .noActivity:
            return Strings.noActivitySubtitle.localized
        case .noUpdates:
            return Strings.noUpdatesSubtitle.localized
        case .notSubscribed:
            return Strings.notSubscribedSubtitle.localized
        }
    }

    var ctaButtonTitle: String? {
        switch self {
        case .noUpdates, .noActivity:
            return nil
        case .notSubscribed:
            return Strings.subscribeNow.localized
        default:
            return Strings.goToSettings.localized
        }
    }
}
