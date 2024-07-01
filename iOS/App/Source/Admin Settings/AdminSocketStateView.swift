//
//  AdminSocketStateView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 18/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

struct AdminSocketStateView: View {
    private struct Constants {
        static let title = "Web Socket State:"
        static let stateLabel = "State:"
        static let isConnectedLabel = "Is Connected?"
        static let isEnabledLabel = "Is Enabled?"
        static let reconnectionCountLabel = "Reconnect Count (since last connection & foreground):"
        static let isReconnectScheduledLabel = "Is Reconnect Scheduled? "
    }

    @State private var isShowingWebSocketHistory = false

    var body: some View {
        VStack(alignment: .leading, spacing: ATHTheme.Dimension.small.padding) {
            Text(Constants.title)
                .font(Font(UIFont.font(for: .calibreHeadline.s.medium)))
            VStack(alignment: .leading, spacing: ATHTheme.Dimension.tiniest.padding) {
                HStack(spacing: ATHTheme.Dimension.small.padding) {
                    Text(
                        [
                            Constants.stateLabel,
                            String(describing: AppEnvironment.shared.network.webSocketState),
                        ].joined(separator: " ")
                    )
                    Button("History >") {
                        self.isShowingWebSocketHistory.toggle()
                    }
                    .foregroundColor(.blue)
                    .font(Font(UIFont.font(for: .calibreUtility.l.regular)))
                    .deeplinkListeningSheet(isPresented: $isShowingWebSocketHistory) {
                        SocketHistoryView()
                    }
                }
                Text(
                    [
                        Constants.isConnectedLabel,
                        String(AppEnvironment.shared.network.isWebSocketConnected),
                    ].joined(separator: " ")
                )
                Text(
                    [
                        Constants.isEnabledLabel,
                        String(AppEnvironment.shared.network.isWebSocketEnabled),
                    ].joined(separator: " ")
                )
                Text(
                    [
                        Constants.reconnectionCountLabel,
                        String(
                            describing: AppEnvironment.shared.network.webSocketReconnectionCount
                        ),
                    ].joined(separator: " ")
                )
                Text(
                    [
                        Constants.isReconnectScheduledLabel,
                        String(AppEnvironment.shared.network.isWebSocketReconnectScheduled),
                    ].joined(separator: " ")
                )
            }
            .font(Font(UIFont.font(for: .calibreUtility.s.regular)))
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

private struct SocketHistoryView: View {

    private var dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.timeZone = .current
        formatter.dateFormat = "HH:mm:ss.SSSS"
        return formatter
    }()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 8) {
                Text("Most Recent")
                    .font(.system(size: 14, weight: .bold))
                let _ = DuplicateIDLogger.logDuplicates(
                    in: AppEnvironment.shared.network.webSocketKeyEvents.reversed(),
                    id: \.self
                )
                ForEach(AppEnvironment.shared.network.webSocketKeyEvents.reversed(), id: \.self) {
                    Text(
                        "\(dateFormatter.string(from: $0.date)) (\($0.fileName)): \($0.description)"
                    )
                }
                Text("Oldest")
                    .font(.system(size: 14, weight: .bold))
            }
            .font(.body)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .frame(maxWidth: .infinity)
        .padding()
    }

}

extension String {

    fileprivate init(_ bool: Bool) {
        self = bool ? "YES" : "NO"
    }

}

struct AdminSocketStateView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            AdminSocketStateView()
                .preferredColorScheme(.light)
                .previewDisplayName("Light")
            AdminSocketStateView()
                .preferredColorScheme(.dark)
                .previewDisplayName("Dark")
        }
        .previewLayout(.sizeThatFits)
    }
}
