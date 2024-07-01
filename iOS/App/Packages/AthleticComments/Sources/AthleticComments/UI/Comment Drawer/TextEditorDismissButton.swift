//
//  TextEditorDismissButton.swift
//
//
//  Created by kevin fremgen on 7/13/23.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

public struct TextEditorDismissButton: View {
    let action: VoidClosure

    public init(action: @escaping VoidClosure) {
        self.action = action
    }

    public var body: some View {
        Button {
            action()
        } label: {
            ZStack(alignment: .center) {
                Circle()
                    .fill(Color.chalk.dark100)
                    .frame(width: 20, height: 20)

                Image(systemName: "xmark.circle.fill")
                    .resizable()
                    .frame(width: 20, height: 20)
                    .foregroundColor(.chalk.dark500)
            }
        }
    }
}
