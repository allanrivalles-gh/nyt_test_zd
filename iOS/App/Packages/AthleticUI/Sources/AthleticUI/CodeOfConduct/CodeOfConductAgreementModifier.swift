//
//  CodeOfConductAgreementModifier.swift
//
//
//  Created by Leonardo da Silva on 26/08/22.
//

import SwiftUI

private struct CodeOfConductAgreementKey: EnvironmentKey {
    static let defaultValue = CodeOfConductAgreement.constant(isAgreed: true)
}

extension EnvironmentValues {
    public var codeOfConductAgreement: CodeOfConductAgreement {
        get { self[CodeOfConductAgreementKey.self] }
        set { self[CodeOfConductAgreementKey.self] = newValue }
    }
}

extension View {
    public func codeOfConductAgreement(_ agreement: CodeOfConductAgreement) -> some View {
        environment(\.codeOfConductAgreement, agreement)
    }
}
