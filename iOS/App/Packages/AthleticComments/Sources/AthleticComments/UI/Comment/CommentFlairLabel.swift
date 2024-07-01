//
//  CommentFlairLabel.swift
//
//
//  Created by Leonardo da Silva on 06/10/22.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

public struct CommentFlairLabel: View {
    let flair: CommentFlair

    public init(flair: CommentFlair) {
        self.flair = flair
    }

    public var body: some View {
        Text(flair.name)
            .tracking(0.25)
            .fixedSize(horizontal: false, vertical: true)
            .fontName(.calibreMedium, size: 10)
            .foregroundColor(
                Color.highContrastAppearance(
                    of: .chalk.dark800,
                    forBackgroundColor: flair.iconContrastColor
                )
            )
            .padding(.vertical, 1)
            .padding(.horizontal, 4)
            .frame(minWidth: 25)
            .background {
                ZStack {
                    let rectangle = RoundedRectangle(cornerRadius: 2)
                    rectangle
                        .fill(flair.iconContrastColor)
                    rectangle
                        .strokeBorder(Color.chalk.dark400, lineWidth: 0.5)
                }
            }
    }
}

struct CommentFlairLabel_Previews: PreviewProvider {
    private static let flairs = [
        GQL.Flair(id: "0", name: "PIT", iconContrastColor: "323232"),
        GQL.Flair(id: "1", name: "CIN", iconContrastColor: "ff6600"),
    ].map(CommentFlair.init)

    static var previews: some View {
        Group {
            let _ = DuplicateIDLogger.logDuplicates(in: flairs)
            ForEach(flairs) { flair in
                CommentFlairLabel(flair: flair)
            }
        }
    }
}
