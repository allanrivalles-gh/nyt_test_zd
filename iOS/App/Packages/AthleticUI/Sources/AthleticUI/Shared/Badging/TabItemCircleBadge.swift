//
//  TabItemCircleBadge.swift
//
//
//  Created by Jason Leyrer on 11/8/23.
//

import SwiftUI

public struct TabItemCircleBadge: View {

    public init() {}

    public var body: some View {
        RedCircleBadge(size: 12, topPadding: 0)
            .overlay {
                Circle().stroke(Color.chalk.dark200, lineWidth: 2)
            }
    }
}

#Preview {
    TabItemCircleBadge()
}
