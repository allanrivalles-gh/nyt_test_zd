/// - Tag: SimpleUserPayload
public struct SimpleUserPayload: Codable {
    public let userId: Int

    public init(userId: Int) {
        self.userId = userId
    }
}
