//
//  SwiftUIView.swift
//
//
//  Created by kevin fremgen on 5/10/23.
//

import AthleticUI
import SwiftUI

public struct CommentContent: View {

    let content: String
    let foregroundColor: Color
    let lineLimit: Int?

    public init(content: String, foregroundColor: Color = .chalk.dark700, lineLimit: Int?) {
        self.content = content
        self.foregroundColor = foregroundColor
        self.lineLimit = lineLimit
    }

    public var body: some View {
        Text(content.asFormattedMarkdown)
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(foregroundColor)
            .tint(Color.chalk.blue)
            .lineLimit(lineLimit)
    }
}

struct CommentContent_Previews: PreviewProvider {
    static var previews: some View {
        CommentContent(content: "This is a test", foregroundColor: .red, lineLimit: 3)
    }
}
