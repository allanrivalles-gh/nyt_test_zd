//
//  CodeOfConductRequiredModifier.swift
//
//
//  Created by Leonardo da Silva on 26/08/22.
//

import AthleticFoundation
import SwiftUI

private struct CodeOfConductRequiredModifier: ViewModifier {
    @Environment(\.codeOfConductAgreement) private var agreement: CodeOfConductAgreement
    // we need to store whether it has appeared, because the sheet won't show if initially presented
    @State private var hasAppeared: Bool = false
    @State private var isAgreeing: Bool = false
    let isRequired: Bool
    let dismiss: VoidClosure

    func body(content: Content) -> some View {
        content
            .sheet(isPresented: .constant(hasAppeared && isRequired && !agreement.isAgreed)) {
                CodeOfConductView(
                    isAgreeing: isAgreeing,
                    onAgree: onAgree,
                    onDisagree: onDisagree
                )
                .interactiveDismissDisabled()
            }
            .onAppear { hasAppeared = true }
    }

    private func onAgree() {
        isAgreeing = true
        Task {
            await agreement.agree()
            await MainActor.run {
                isAgreeing = false
            }
        }
    }

    private func onDisagree() {
        dismiss()
    }
}

extension View {
    public func codeOfConductRequired(
        _ isRequired: Bool = true,
        dismiss: @escaping VoidClosure
    ) -> some View {
        return ModifiedContent(
            content: self,
            modifier: CodeOfConductRequiredModifier(
                isRequired: isRequired,
                dismiss: dismiss
            )
        )
    }
}

struct CodeOfConductRequiredModifier_Previews: PreviewProvider {
    struct CodeOfConductRequiredPreview: View {
        @State var isRequired = false
        @State var isAgreed = false
        @State var isAgreeing = false

        var body: some View {
            let agreement = CodeOfConductAgreement(isAgreed: isAgreed, agree: agree)
            VStack {
                Group {
                    Toggle("isAgreed", isOn: $isAgreed)
                        .disabled(isAgreeing || !isAgreed)
                    Toggle("isRequired", isOn: $isRequired)
                        .disabled(isAgreeing)
                }
                .padding()
                Divider()
                Spacer()
            }
            .codeOfConductRequired(isRequired) {
                isRequired = false
            }
            .codeOfConductAgreement(agreement)
        }

        private func agree() async {
            isAgreeing = true
            try! await Task.sleep(seconds: 2)
            isAgreeing = false
            isAgreed = true
        }
    }

    static var previews: some View {
        CodeOfConductRequiredPreview()
    }
}
