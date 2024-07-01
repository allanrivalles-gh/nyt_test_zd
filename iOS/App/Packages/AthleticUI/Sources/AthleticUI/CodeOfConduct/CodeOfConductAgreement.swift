//
//  CodeOfConductAgreement.swift
//
//
//  Created by Leonardo da Silva on 26/08/22.
//

import Foundation

public struct CodeOfConductAgreement {
    public let isAgreed: Bool
    public let agree: () async -> Void

    public init(isAgreed: Bool, agree: @escaping () async -> Void) {
        self.isAgreed = isAgreed
        self.agree = agree
    }

    public static func constant(isAgreed: Bool) -> CodeOfConductAgreement {
        return CodeOfConductAgreement(
            isAgreed: isAgreed,
            agree: {}
        )
    }
}
