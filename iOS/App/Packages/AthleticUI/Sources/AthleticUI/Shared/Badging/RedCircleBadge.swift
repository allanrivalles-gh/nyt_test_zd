//
//  RedCircleBadge.swift
//
//
//  Created by Jason Leyrer on 7/29/22.
//

import SwiftUI

public struct RedCircleBadge: View {
    let size: CGFloat
    let topPadding: CGFloat

    public init(
        size: CGFloat = 6,
        topPadding: CGFloat = 2
    ) {
        self.size = size
        self.topPadding = topPadding
    }

    public var body: some View {
        Circle()
            .fill(Color.chalk.red)
            .frame(width: size, height: size)
            .padding(.top, topPadding)
    }
}

struct RedCircleBadge_Previews: PreviewProvider {
    static var previews: some View {
        RedCircleBadge()
    }
}
