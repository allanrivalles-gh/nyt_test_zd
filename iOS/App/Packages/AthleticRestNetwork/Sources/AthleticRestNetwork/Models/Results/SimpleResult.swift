/// - Tag: SimpleResult
public struct SimpleResult: Codable {
    let result: String
}

public struct SimpleBoolResult: Codable {
    let result: Bool
}

public struct SimpleResultSuccess: Codable {
    let success: Bool
}
