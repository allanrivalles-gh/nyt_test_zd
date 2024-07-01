//
//  NoNotificationsContent.swift
//
//
//  Created by Jason Leyrer on 7/13/23.
//

import AthleticAnalytics
import AthleticNavigation
import SwiftUI

struct NoNotificationsContent: View {

    let type: EmptyContentDisplayType
    @Binding var isShowingSubscriptionPlans: Bool
    let analyticsDefaults: AnalyticsRequiredValues
    @Environment(\.openURL) private var openUrl
    @EnvironmentObject private var navigationModel: NavigationModel

    public var body: some View {
        VStack(spacing: 0) {
            Image(type.iconImageName)
                .resizable()
                .frame(width: 40, height: 40)
                .padding(.bottom, 8)

            Text(type.title)
                .multilineTextAlignment(.center)
                .fontStyle(.calibreHeadline.s.semibold)
                .foregroundColor(.chalk.dark800)
                .padding(.bottom, 4)

            Text(type.subtitle)
                .multilineTextAlignment(.center)
                .fontStyle(.calibreUtility.l.regular)
                .foregroundColor(.chalk.dark800)

            if let title = type.ctaButtonTitle {
                Button {
                    switch type {
                    case .noUpdates, .noActivity:
                        /// These types don't have a button title, so we should never get here.
                        break
                    case .notSubscribed:
                        isShowingSubscriptionPlans = true
                    case .commentNotificationsOff:
                        navigationModel.addScreenToSelectedTab(.account(.notificationSettings(nil)))
                    default:
                        Analytics.track(
                            event: .init(
                                verb: .click,
                                view: .notifications,
                                element: .notificationSettings,
                                requiredValues: analyticsDefaults
                            )
                        )

                        guard let url = URL(string: UIApplication.openSettingsURLString) else {
                            return
                        }

                        openUrl(url)
                    }
                } label: {
                    Text(title)
                }
                .buttonStyle(.core(size: .fitted, level: .primary))
                .padding(.top, 24)
            }
        }
        .padding(.horizontal, 32)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.chalk.dark200)
    }
}
