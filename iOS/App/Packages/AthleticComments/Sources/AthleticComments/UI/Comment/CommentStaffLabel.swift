//
//  CommentStaffLabel.swift
//
//
//  Created by kevin fremgen on 5/10/23.
//

import AthleticUI
import SwiftUI

public struct CommentStaffLabel: View {

    let foregroundColor: Color
    let backgroundColor: Color

    public init(foregroundColor: Color = .chalk.dark300, backgroundColor: Color = .chalk.dark800) {
        self.foregroundColor = foregroundColor
        self.backgroundColor = backgroundColor
    }

    public var body: some View {
        Text(Strings.staff.localized.uppercased())
            .tracking(0.25)
            .fixedSize(horizontal: false, vertical: true)
            .fontName(.calibreMedium, size: 10)
            .foregroundColor(foregroundColor)
            .padding(.vertical, 3)
            .padding(.horizontal, 4)
            .background(backgroundColor)
            .cornerRadius(2)
    }
}

struct CommentStaffLabelView_Previews: PreviewProvider {
    static var previews: some View {
        CommentStaffLabel(foregroundColor: .black, backgroundColor: .white)
    }
}
