public struct UserTopicsPayload: Encodable {
    public let leagueIds: [Int]
    public let authorIds: [Int]
    public let teamIds: [Int]

    public init(leagueIds: [Int], authorIds: [Int], teamIds: [Int]) {
        self.leagueIds = leagueIds
        self.authorIds = authorIds
        self.teamIds = teamIds
    }
}
