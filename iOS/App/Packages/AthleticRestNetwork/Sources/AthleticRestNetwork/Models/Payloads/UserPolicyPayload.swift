public struct UserPolicy: Codable {
    let privacyPolicy: Bool
    let termsAndConditions: Bool

    public init(privacyPolicy: Bool, termsAndConditions: Bool) {
        self.privacyPolicy = privacyPolicy
        self.termsAndConditions = termsAndConditions
    }
}
