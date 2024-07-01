//
//  LiveStatusIndicatorView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 10/22/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

public struct LiveStatusIndicatorView: View {

    public enum DisplayType {
        case blogContent
        case tabBarBadge
        case sideBarBadge

        var horizontalPadding: CGFloat {
            switch self {
            case .blogContent:
                return 8
            case .tabBarBadge, .sideBarBadge:
                return 4
            }
        }

        var verticalPadding: CGFloat {
            switch self {
            case .blogContent:
                return 4
            case .tabBarBadge, .sideBarBadge:
                return 2
            }
        }

        var fontSize: CGFloat {
            switch self {
            case .blogContent:
                return 14
            case .tabBarBadge, .sideBarBadge:
                return 9
            }
        }
    }

    let type: DisplayType

    public init(type: DisplayType) {
        self.type = type
    }

    public var body: some View {
        Text(Strings.live.localized)
            .font(.font(name: .calibreMedium, size: type.fontSize))
            .foregroundColor(.chalk.dark200)
            .padding(.horizontal, type.horizontalPadding)
            .padding(.vertical, type.verticalPadding)
            .background(
                RoundedRectangle(cornerRadius: 2)
                    .fill(Color.chalk.red)
            )
            .shadow(
                color: type == .tabBarBadge ? .chalk.dark100.opacity(0.25) : .clear,
                radius: 3,
                x: 0,
                y: 3
            )
    }
}

struct LiveStatusIndicatorView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            HStack {
                VStack {
                    Text("Dark")
                    LiveStatusIndicatorView(type: .blogContent)
                }
                .padding()
                .background(Color.chalk.dark300)
                .darkScheme()
                VStack {
                    Text("Light")
                    LiveStatusIndicatorView(type: .blogContent)
                }
                .padding()
                .background(Color.chalk.dark300)
                .lightScheme()
            }
        }

        .loadCustomFonts()
    }
}
