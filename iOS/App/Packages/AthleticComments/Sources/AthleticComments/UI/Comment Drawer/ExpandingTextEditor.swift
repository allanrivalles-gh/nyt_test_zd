//
//  ExpandingTextEditor.swift
//
//
//  Created by kevin fremgen on 7/13/23.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

struct ViewHeightKey: PreferenceKey {
    static var defaultValue: CGFloat { 0 }
    static func reduce(value: inout Value, nextValue: () -> Value) {
        value = value + nextValue()
    }
}

public struct ExpandingTextEditor: View {
    var isCommentDrawerFocus: FocusState<Bool>.Binding
    @Binding var text: String
    @State var textEditorHeight: CGFloat = 0
    @State var isActive: Bool

    private var placeHolderText: String {
        /// If text is empty
        if text.isEmpty {
            /// Show placeholder text
            return Strings.writeAComment.localized
        } else {
            return text
        }
    }

    private var placeHolderTextColor: Color {
        if !isCommentDrawerFocus.wrappedValue && text.isEmpty {
            /// If text editor not first responder and text is empty
            /// Show placeholder text color
            return .chalk.dark500
        } else if !isCommentDrawerFocus.wrappedValue && !text.isEmpty {
            /// If text editor no first responder and text is not empty
            /// Show text color
            return .chalk.dark800
        } else {
            /// If text is first responder don't show text
            return .clear
        }
    }

    public init(
        isCommentDrawerFocus: FocusState<Bool>.Binding,
        text: Binding<String>
    ) {
        self.isCommentDrawerFocus = isCommentDrawerFocus
        self._text = text
        self._isActive = State(initialValue: isCommentDrawerFocus.wrappedValue)
    }

    public var body: some View {
        ZStack {

            /// Allows for dynamic height control of TextEditor
            /// Also shows placeholder for when TextEditor isn't first responder
            Text(placeHolderText)
                .frame(maxWidth: .infinity, alignment: .leading)
                /// Sets the max amount of lines when active and not active
                ///  Responsible for setting max amount of lines of text editor
                .lineLimit(isActive ? 5 : 2)
                .fontStyle(.calibreUtility.l.regular)
                .foregroundColor(placeHolderTextColor)
                .kerning(0.25)
                .background(
                    GeometryReader {
                        Color.clear.preference(
                            key: ViewHeightKey.self,
                            value: $0.frame(in: .local).size.height
                        )
                    }
                )
                .contentShape(
                    RoundedRectangle(cornerRadius: isActive ? 0 : 16)
                )
                /// Disable view when text editor is first responder
                .disabled(isActive)
                .onTapGesture {
                    isCommentDrawerFocus.wrappedValue = true
                }

            /// Dynamic text editor
            TextEditor(text: $text)
                .focused(isCommentDrawerFocus)
                .fontStyle(.calibreUtility.l.regular)
                .foregroundColor(.chalk.dark800)
                .kerning(0.25)
                .multilineTextAlignment(.leading)
                .scrollContentBackground(.hidden)
                /// Add some extra height to avoid first line being cutt off when going to second line
                .frame(height: isActive ? textEditorHeight * 1.15 : textEditorHeight)
                .opacity(isActive ? 1 : 0)
        }
        .padding(.top, isActive ? 0 : 6)
        .padding(.bottom, isActive ? 0 : 8)
        .padding(.horizontal, isActive ? 0 : 16)
        .background(
            isActive ? Color.chalk.dark200 : Color.chalk.dark300
        )
        .clipShape(
            RoundedRectangle(cornerRadius: isActive ? 0 : 16)
        )
        .onPreferenceChange(ViewHeightKey.self) {
            /// Updates texteditor height
            textEditorHeight = $0
        }
        .onChange(of: isCommentDrawerFocus.wrappedValue) { newValue in
            if newValue {
                /// Add delay updating `isActive` so  view can update after keyboard appears
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.7) {
                    self.isActive = newValue
                }
            } else {
                self.isActive = newValue
            }
        }
    }
}
