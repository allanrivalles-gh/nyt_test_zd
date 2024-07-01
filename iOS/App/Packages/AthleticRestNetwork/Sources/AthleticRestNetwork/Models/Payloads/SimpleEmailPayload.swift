public struct SimpleEmailPayload: Encodable {
    public let regEmail: String

    public init(email: String) {
        regEmail = email
    }
}
