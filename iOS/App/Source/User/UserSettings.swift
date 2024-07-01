//
//  UserSettings.swift
//  theathletic-ios
//
//  Created by Jan Remes on 14.07.16.
//  Copyright © 2016 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import KeychainSwift
import UIKit

class UserSettings: NSObject {

    enum DefaultTextSize: Int {
        case normal = 0
        case medium = 1
        case large = 2
        case extraLarge = 3

        var title: String {
            switch self {
            case .normal:
                return Strings.normal.localized
            case .medium:
                return Strings.medium.localized
            case .large:
                return Strings.large.localized
            case .extraLarge:
                return Strings.extraLarge.localized
            }
        }

        var fontSize: CGFloat {
            switch self {
            case .normal:
                return 20
            case .medium:
                return 22
            case .large:
                return 24
            case .extraLarge:
                return 28
            }
        }
    }

    static let shared = UserSettings()

    private let keychain = KeychainSwift()

    // Keychain keys
    private let articlesReadIdsKey = "ArticlesReadCountKey"

    class func onboardingFreeTrialDisplayed(userId: String) -> Bool {
        return UserDefaults.standard.bool(forKey: "onboarding_free_trial_displayed-\(userId)")
    }

    class func setOnboardingFreeTrialDisplayed(userId: String) {
        UserDefaults.standard.set(true, forKey: "onboarding_free_trial_displayed-\(userId)")
    }

    // MARK: - Device Token

    static let deviceToken: String = {
        if let deviceIdentifier = UIDevice.current.identifierForVendor?.uuidString {
            return deviceIdentifier
        }

        if let token = UserDefaults.standard.string(forKey: "CustomDeviceToken") {
            return token
        } else {
            let token = UUID().uuidString
            UserDefaults.standard.set(token, forKey: "CustomDeviceToken")
            return token
        }
    }()

    // MARK: - Tracking first app launch

    /**
     Tracking first app launch
     */
    func trackAppLaunch() {
        if UserDefaults.appOpenFirstTime == nil {
            UserDefaults.appOpenFirstTime = Date()
        }
    }

    // MARK: - Tracking article reading
    func trackOpenArticle(_ articleId: String) {
        if AppEnvironment.shared.entitlement.hasAccessToContent {
            cleanReadArticles()
            return
        }

        let thisMonthReadKey = articlesReadThisMonthKey()
        if let articleIdsString = keychain.get(thisMonthReadKey) {
            let articleIds = articleIdsString.components(separatedBy: ",")
            if !articleIds.contains(articleId) {
                let stringToSave = articleIdsString + "," + articleId
                keychain.set(
                    stringToSave,
                    forKey: thisMonthReadKey,
                    withAccess: .accessibleAlways
                )
            }
        } else {
            keychain.set(
                articleId,
                forKey: thisMonthReadKey,
                withAccess: .accessibleAlways
            )
        }
    }

    var numberOfArticlesReadThisMonth: Int {
        let key = articlesReadThisMonthKey()
        guard let articleIdString = keychain.get(key) else {
            return 0
        }
        return articleIdString.components(separatedBy: ",").count
    }

    private func articlesReadThisMonthKey(timeSettings: TimeSettings = SystemTimeSettings())
        -> String
    {
        let month = "\(timeSettings.now().month)"
        let year = "\(timeSettings.now().year)"
        return [year, month, articlesReadIdsKey].joined(separator: "-")
    }

    func enablePushNotifications() {
        RemoteNotificationHandler.shared.requestAuthorization()
    }

    func cleanReadArticles() {
        if keychain.get(articlesReadIdsKey) != nil {
            if !keychain.delete(articlesReadIdsKey) {
                ATHLogger(category: .user).warning("failed to delete user articles key.")
            }
        }
    }
}
