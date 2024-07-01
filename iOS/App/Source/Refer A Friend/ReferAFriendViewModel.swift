//
//  ReferAFriendViewModel.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 12/01/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticUI
import Foundation
import SwiftUI
import UIKit

@MainActor
class ReferAFriendViewModel: ObservableObject {
    private static let logger = ATHLogger(category: .refer)

    @Published private(set) var isSubmitting: Bool = false
    @Published private(set) var moreReferralsRequested: Bool = false
    @Published var submitError: Error?
    @Published private(set) var referralItems: (url: URL, message: String)? = nil

    let referralSubject = Strings.referralsEmailSubject.localized

    var hasReferralsLeft: Bool {
        guard let user = userModel.current else {
            return false
        }

        return user.referralsRedeemed < user.referralsTotal
    }

    var referralsCount: String {
        userModel.current?.referralsCount ?? ""
    }

    private let userModel: UserModel

    init(userModel: UserModel) {
        self.userModel = userModel
    }

    func generateReferralUrl() async {
        guard referralItems == nil else { return }

        isSubmitting = true

        do {
            let referralUrl = try await userModel.generateReferralUrl()
            let message = [Strings.referralsShare.localized, referralUrl.absoluteString].joined(
                separator: " "
            )
            referralItems = (url: referralUrl, message: message)

            await Analytics.track(
                event: .init(
                    verb: .leaveApp,
                    view: .shareReferralCode,
                    objectType: .referralLinkId,
                    objectIdentifier: referralUrl.pathComponents[safe: 2],
                    metaBlob: AnalyticsEvent.MetaBlob(
                        referralsAvailable: referralsCount.intValue
                    )
                )
            )
        } catch {
            Self.logger.error(
                "create referral url failed with error: \n\(error)",
                .network
            )

            submitError = error
        }

        isSubmitting = false
    }

    func onMoreReferralsRequested() {
        Analytics.track(
            event: .init(
                verb: .click,
                view: .referralsPage,
                element: .requestMoreReferrals
            )
        )

        moreReferralsRequested = true
    }
}
