//
//  ReactCreateButton.swift
//
//
//  Created by Kyle Browning on 5/17/22.
//

import SwiftUI

public struct ReactCreateButton<Content: View>: View {

    let title: String
    @Binding private var isReactEntryFormShown: Bool
    let tapAction: () -> Void
    let content: () -> Content

    public init(
        title: String,
        isReactEntryFormShown: Binding<Bool>,
        tapAction: @escaping () -> Void,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.title = title
        self._isReactEntryFormShown = isReactEntryFormShown
        self.tapAction = tapAction
        self.content = content
    }

    public var body: some View {
        Button {
            isReactEntryFormShown = true
            tapAction()
        } label: {
            Label(title, image: "icn_short_form")
                .foregroundColor(.chalk.dark200)
                .fontStyle(.calibreUtility.xl.medium)
        }
        .frame(width: 103, height: 40)
        .background(Color.chalk.dark800)
        .clipShape(Capsule())
        .padding(.bottom, 16)
        .sheet(isPresented: $isReactEntryFormShown) {
            content()
        }
    }
}

struct ReactCreateButton_Previews: PreviewProvider {
    static var previews: some View {
        ReactCreateButton(title: "React", isReactEntryFormShown: .constant(false), tapAction: {}) {
            HStack {
                Text("Hello!")
            }
        }
    }
}
