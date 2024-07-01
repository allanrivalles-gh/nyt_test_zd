//
//  TextEditorSendButton.swift
//
//
//  Created by kevin fremgen on 7/13/23.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

public struct TextEditorSendButton: View {

    @Binding var text: String
    @Binding var sending: Bool
    let action: VoidClosure

    private var isEnabled: Bool {
        !sending && !text.isEmpty
    }

    public init(text: Binding<String>, sending: Binding<Bool>, action: @escaping VoidClosure) {
        self._text = text
        self._sending = sending
        self.action = action
    }

    public var body: some View {
        Button {
            action()
        } label: {
            HStack(spacing: 2) {
                Text("Send")
                    .fontStyle(.calibreUtility.s.medium)
                Image(systemName: "chevron.right")
                    .fontName(.calibreRegular, size: 8)

            }
            .foregroundColor(.chalk.dark100)
            .padding(.top, 4)
            .padding(.bottom, 5)
            .padding(.horizontal, 8)
            .background(Color.chalk.dark700)
            .clipShape(RoundedRectangle(cornerRadius: 19))
        }
        .disabled(!isEnabled)
    }
}
