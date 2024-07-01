//
//  TheAthleticApp.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 1/20/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticFoundation
import AthleticNavigation
import AthleticUI
import Datadog
import Embrace
import SwiftUI

@main
struct TheAthleticApp: App {
    @UIApplicationDelegateAdaptor private var appDelegate: AppDelegate
    @Environment(\.scenePhase) private var scenePhase
    @State private var hasDeepLinkError = false

    private let environment = AppEnvironment.shared
    private let model: TheAthleticAppModel

    init() {
        model = TheAthleticAppModel(environment: environment)
        AthleticUI.registerFonts()
        AppearanceAppDelegate.setupAppearance()
    }

    var body: some Scene {
        WindowGroup {
            InitialLoadingView(
                network: environment.network,
                startupManager: model.startupManager,
                appDelegate: appDelegate
            )
            .globalEnvironment()
            .environment(\.hasDeepLinkError, $hasDeepLinkError)
            .onOpenURL {
                model.onOpenUrl(
                    url: $0,
                    errorAction: {
                        Task { @MainActor in
                            environment.navigationModel.homePath.clear()
                            environment.navigationModel.selectedTab.value = .home
                            hasDeepLinkError = true
                            // Avoid hanging toast
                            try? await Task.sleep(seconds: 5)
                            if hasDeepLinkError {
                                hasDeepLinkError = false
                            }
                        }
                    }
                )
            }
            .onAppear {
                /// End Embrace App Startup for performance
                Embrace.sharedInstance().endAppStartup()
            }
            .trackRUMView(name: "InitialLoadingView")
        }
        .onChange(of: scenePhase) { newPhase in
            switch newPhase {
            case .active:
                model.didBecomeActive()
            case .inactive:
                model.didBecomeInactive()
            case .background:
                model.didEnterBackground()
            @unknown default:
                // Fallback for future cases
                break
            }
        }
        .backgroundTask(.appRefresh(model.initialDataLoadIdentifier)) {
            await model.executeInitialBackgroundDataLoad()
        }
    }
}

extension TheAthleticApp {

    /// A human readable string indicating how much free memory The Athletic app process has remaining.
    /// This value changes regularly depending on system resource usage.
    static var availableMemoryHumanReadable: String {
        let availableMemory = os_proc_available_memory()
        let memoryString = ByteCountFormatter.string(
            fromByteCount: Int64(availableMemory),
            countStyle: .memory
        )
        return memoryString
    }

    static var isStaging: Bool {
        Bundle.main.bundleIdentifier == "com.theathletic.news-staging"
    }
}
