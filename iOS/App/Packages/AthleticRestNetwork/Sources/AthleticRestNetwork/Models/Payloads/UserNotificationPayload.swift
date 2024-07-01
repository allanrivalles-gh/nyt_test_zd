// MARK: UserNotificationPayload
/// - tag: UserNotificationPayload
public struct UserNotificationPayload: Encodable {
    @StringEncoder
    public var notifValue: Int
    public let notifType: String
    public let notifName: String
    public let userId: Int
    public let isSubscribed: Bool

    /**
     Initialization of the toggle follow league payload.
     - Parameter leagueId: the id of the league.
     - Parameter isSubscribed: The flag indicates following or not.
     ### Usage Example: ###
     ````
     let payload = UserNotificationPayload(withLeagueId: leagueId, isSubscribed: isSubscribed)
     and then pass it into the service:
     UserService.updateLeagueSubscription(payload: payload, network: network)
    ````
    */
    public init(
        withNotificationId notifValue: Int,
        andType notifType: String,
        andName notifName: String,
        andUserId userId: Int,
        isSubscribed: Bool
    ) {
        self.notifValue = notifValue
        self.notifType = notifType
        self.notifName = notifName
        self.userId = userId
        self.isSubscribed = isSubscribed
    }

    //Excluding the 'isSubscribed' key when encode
    private enum CodingKeys: String, CodingKey {
        case notifValue
        case notifName
        case notifType
        case userId
    }
}
