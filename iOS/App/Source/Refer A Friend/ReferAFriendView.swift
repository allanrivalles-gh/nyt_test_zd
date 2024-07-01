//
//  ReferAFriendView.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 11/01/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

struct ReferAFriendView: View {
    @StateObject private var viewModel: ReferAFriendViewModel

    init(userModel: UserModel) {
        _viewModel = StateObject(wrappedValue: ReferAFriendViewModel(userModel: userModel))
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                let hasReferralsLeft = viewModel.hasReferralsLeft
                Image("referral_card")
                    .resizable()
                    .scaledToFit()
                    .padding(.bottom, 35)

                Text(Strings.referralsTitle.localized)
                    .fontStyle(.calibreHeadline.m.semibold)
                    .foregroundColor(.chalk.dark800)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 15)

                Text(
                    hasReferralsLeft
                        ? Strings.referralsSubtitle.localized
                        : Strings.referralsSubtitleRedeemed.localized
                )
                .fontStyle(.calibreUtility.xl.regular)
                .foregroundColor(.chalk.dark500)
                .multilineTextAlignment(.center)

                Spacer()

                Text(viewModel.referralsCount)
                    .fontName(.calibreSemibold, size: 58)
                    .foregroundColor(.chalk.dark800)
                    .padding(.bottom, 5)
                Text(Strings.referralsPassesRedeemed.localized)
                    .fontStyle(.calibreHeadline.s.medium)
                    .foregroundColor(.chalk.dark700)
                    .padding(.bottom, 50)
                Group {
                    if viewModel.moreReferralsRequested {
                        Label(
                            Strings.referralsRequestSent.localized,
                            systemImage: "checkmark.circle.fill"
                        )
                        .fontStyle(.calibreHeadline.s.semibold)
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .foregroundColor(.chalk.dark800)
                        .background(
                            RoundedRectangle(cornerRadius: 2)
                                .stroke(Color.chalk.dark500, lineWidth: 1.5)
                        )
                    } else {
                        Group {
                            if hasReferralsLeft {
                                if let referralItems = viewModel.referralItems {
                                    ShareLink(
                                        item: referralItems.url,
                                        subject: Text(viewModel.referralSubject),
                                        message: Text(referralItems.message)
                                    ) {
                                        Text(Strings.referralsCta.localized)
                                    }
                                }
                            } else {
                                Button(Strings.referralsRequestMore.localized) {
                                    viewModel.onMoreReferralsRequested()
                                }
                            }
                        }
                        .buttonStyle(RedRectangleButtonStyle())
                        .disabled(viewModel.isSubmitting)
                    }
                }
                .padding(.bottom, 40)

            }
            .padding(.top, 44)
            .padding(.horizontal, 20)
        }
        .onAppear {
            Analytics.track(
                event: .init(
                    verb: .view,
                    view: .referralsPage
                )
            )
        }
        .task {
            await viewModel.generateReferralUrl()
        }
        .overlay {
            if viewModel.isSubmitting {
                ProgressView()
                    .progressViewStyle(.athletic)
            }
        }
        .alert(
            Strings.error.localized,
            presenting: $viewModel.submitError,
            message: { error in
                Text(error.localizedDescription)
            }
        )
    }
}

extension View {
    @ViewBuilder
    fileprivate func alert<Presenting, Message: View>(
        _ title: String,
        presenting: Binding<Presenting?>,
        message: (Presenting) -> Message
    ) -> some View {
        let isPresented = Binding<Bool>(
            get: { presenting.wrappedValue != nil },
            set: { if !$0 { presenting.wrappedValue = nil } }
        )
        alert(title, isPresented: isPresented) {
            /// leaving empty gives us the default `Ok` button
        } message: {
            if let presenting = presenting.wrappedValue {
                message(presenting)
            }
        }
    }
}

private struct RedRectangleButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .fontStyle(.calibreHeadline.s.semibold)
            .frame(maxWidth: .infinity, minHeight: 48)
            .foregroundColor(.white)
            .background(
                RoundedRectangle(cornerRadius: 2)
                    .fill(Color.chalk.red)
            )
            .opacity(configuration.isPressed ? 0.5 : 1)
    }
}
