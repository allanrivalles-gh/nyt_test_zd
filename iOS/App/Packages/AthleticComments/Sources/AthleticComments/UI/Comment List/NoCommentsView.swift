//
//  NoCommentsView.swift
//
//
//  Created by Jason Leyrer on 11/21/22.
//

import SwiftUI

struct NoCommentsView: View {
    var body: some View {
        VStack(spacing: 8) {
            Image("empty_comments_icon")
                .padding(.bottom, 8)
            Text(Strings.beTheFirstToCommentOnThisStory.localized)
                .foregroundColor(.chalk.dark700)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 16)
    }
}

struct NoCommentsView_Previews: PreviewProvider {
    static var previews: some View {
        NoCommentsView()
    }
}
