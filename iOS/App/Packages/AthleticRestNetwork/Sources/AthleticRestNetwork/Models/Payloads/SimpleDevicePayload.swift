/// - Tag: SimpleDevicePayload
public struct SimpleDevicePayload: Codable {
    public let deviceId: String?

    public init(deviceId: String) {
        self.deviceId = deviceId
    }
}
