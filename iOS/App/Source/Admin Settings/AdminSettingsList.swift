//
//  AdminSettingsList.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticNavigation
import SwiftUI

struct AdminSettingsList: View {
    @EnvironmentObject private var userModel: UserModel

    @State private var isShowingFullScreenWebViewTester: Bool = false
    @State private var isShowingFullScreenSlideStoryTester: Bool = false

    var body: some View {
        List {
            Group {
                NavigationLink(screen: .account(.adminSettings(.advertising))) {
                    LeadingAlignedLabel(
                        text: "Advertising",
                        systemImage: "dollarsign"
                    )
                }

                NavigationLink(screen: .account(.adminSettings(.app))) {
                    LeadingAlignedLabel(
                        text: "App",
                        systemImage: "app"
                    )
                }

                NavigationLink(screen: .account(.adminSettings(.diagnostics(.settings)))) {
                    LeadingAlignedLabel(
                        text: "Diagnostics",
                        systemImage: "list.clipboard"
                    )
                }

                NavigationLink(screen: .account(.adminSettings(.environment))) {
                    LeadingAlignedLabel(
                        text: "Environment",
                        systemImage: "cloud"
                    )
                }

                NavigationLink(screen: .account(.adminSettings(.compass))) {
                    LeadingAlignedLabel(
                        text: "Experiments",
                        systemImage: "safari"
                    )
                }

                NavigationLink(screen: .account(.adminSettings(.featureFlags))) {
                    LeadingAlignedLabel(
                        text: "Feature Flags",
                        systemImage: "flag"
                    )
                }

                NavigationLink(screen: .account(.adminSettings(.statsAndTheGame))) {
                    LeadingAlignedLabel(
                        text: "Stats & the Game",
                        systemImage: "sportscourt"
                    )
                }

                Button {
                    isShowingFullScreenWebViewTester.toggle()
                } label: {
                    LeadingAlignedLabel(
                        text: "WebView tester",
                        systemImage: "safari"
                    )
                }

                Button {
                    isShowingFullScreenSlideStoryTester.toggle()
                } label: {
                    LeadingAlignedLabel(
                        text: "Slide Story tester",
                        systemImage: "newspaper"
                    )
                }
            }
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.dark800)
        }
        .navigationTitle("Admin Settings")
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
        .fullScreenCover(isPresented: $isShowingFullScreenWebViewTester) {
            AdminWebViewTester(isShowingFullScreenWebviewTester: $isShowingFullScreenWebViewTester)
        }
        .transparentFullScreenCover(isPresented: $isShowingFullScreenSlideStoryTester) {
            SlideStory()
        }
    }
}

private struct LeadingAlignedLabel: View {
    let text: String
    let systemImage: String

    var body: some View {
        Label(text, systemImage: systemImage)
            .imageScale(.large)
            .alignmentGuide(.listRowSeparatorLeading) { dimensions in
                dimensions[.leading]
            }
    }
}

struct AdminSettingsList_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            AdminSettingsList()
        }
    }
}
