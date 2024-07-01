//
//  CodeOfConductView.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 24/08/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

struct CodeOfConductView: View {
    let isAgreeing: Bool
    let onAgree: VoidClosure
    let onDisagree: VoidClosure

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text(Strings.codeOfConductTitle.localized)
                    .fontStyle(.slab.m.bold)
                info(Strings.codeOfConductHeader.localized)
                section(
                    title: Strings.codeOfConductSection0Title.localized,
                    info: Strings.codeOfConductSection0Info.localized
                )
                section(
                    title: Strings.codeOfConductSection1Title.localized,
                    info: Strings.codeOfConductSection1Info.localized
                )
                section(
                    title: Strings.codeOfConductSection2Title.localized,
                    info: Strings.codeOfConductSection2Info.localized
                )
                section(
                    title: Strings.codeOfConductSection3Title.localized,
                    info: Strings.codeOfConductSection3Info.localized
                )
                info(Strings.codeOfConductFooter.localized, markdown: true)
                ZStack {
                    Button(Strings.codeOfConductAgreeButton.localized, action: onAgree)
                        .disabled(isAgreeing)
                        .frame(maxWidth: .infinity, alignment: .center)
                        .fontStyle(.slab.m.bold)
                        .foregroundColor(isAgreeing ? .clear : .chalk.dark800)
                    if isAgreeing {
                        ProgressView()
                    }
                }
                Button(Strings.codeOfConductDisagreeButton.localized, action: onDisagree)
                    .disabled(isAgreeing)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .fontStyle(.slab.m.bold)
                    .foregroundColor(.chalk.dark500)
            }
            .padding(.vertical, 50)
            .padding(.horizontal, 18)
        }
    }

    @ViewBuilder
    private func section(title: String, info text: String) -> some View {
        Text(title).fontStyle(.calibreUtility.l.medium)
        info(text)
    }

    @ViewBuilder
    private func info(_ text: String, markdown: Bool = false) -> some View {
        Group {
            if markdown {
                Text(LocalizedStringKey(text))
            } else {
                Text(text)
            }
        }
        .fontStyle(.calibreUtility.l.regular)
    }
}

struct CodeOfConductView_Previews: PreviewProvider {
    static var previews: some View {
        CodeOfConductView(
            isAgreeing: false,
            onAgree: {},
            onDisagree: {}
        )
    }
}
