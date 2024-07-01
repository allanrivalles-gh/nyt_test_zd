//
//  ReturnToTopButton.swift
//
//
//  Created by Jason Leyrer on 8/4/22.
//

import AthleticFoundation
import SwiftUI

public struct ReturnToTopButton: View {
    let tapAction: VoidClosure

    public init(tapAction: @escaping VoidClosure) {
        self.tapAction = tapAction
    }

    public var body: some View {
        Button(action: {
            tapAction()
        }) {
            Circle()
                .fill(Color.chalk.dark800)
                .frame(width: 44, height: 44)
                .overlay(
                    Chevron(foregroundColor: .chalk.dark100, direction: .up)
                )
        }
    }
}

struct ReturnToTopButton_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            ReturnToTopButton {}
                .previewLayout(.sizeThatFits)
                .preferredColorScheme(.dark)

            ReturnToTopButton {}
                .previewLayout(.sizeThatFits)
                .preferredColorScheme(.light)
        }
    }
}
