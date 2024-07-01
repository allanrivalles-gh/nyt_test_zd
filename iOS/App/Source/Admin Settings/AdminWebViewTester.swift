//
//  AdminWebViewTester.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 4/5/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticNavigation
import AthleticUI
import SwiftUI

struct AdminWebViewTester: View {
    @EnvironmentObject private var navigationModel: NavigationModel
    @Binding var isShowingFullScreenWebviewTester: Bool

    /// Test URL: https://theathletic.com/live-blogs/college-football-recruiting/pjxqZ8ASxOcp/?embed=1
    @State private var urlToLoad: String = ""

    var body: some View {
        PathObservingNavigationStack(path: navigationModel.webViewTestPath) {
            VStack {
                TextField("URL", text: $urlToLoad)
                    .padding(10)
                    .background(Color.chalk.dark300)
                Spacer()
                NavigationLink(
                    screen: .webview(type: .webViewTest(urlToLoad)),
                    label: {
                        Text("Go")
                    }
                )
                .buttonStyle(.core(size: .regular, level: .primary))
            }
            .padding()
            .navigationTitle("WebView Tester")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Exit") {
                        isShowingFullScreenWebviewTester = false
                    }
                }
            }
            .handleNavigationLinks()
        }
    }
}

struct AdminWebViewTester_Previews: PreviewProvider {
    static var previews: some View {
        AdminWebViewTester(isShowingFullScreenWebviewTester: .constant(false))
    }
}
