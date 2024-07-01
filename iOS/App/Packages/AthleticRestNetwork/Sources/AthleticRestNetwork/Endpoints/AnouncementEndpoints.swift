import Foundation

public enum AnnouncementEndoint: Endpoint {

    case hideAnnouncement(announcementId: String)
    case clickAnnouncement(announcementId: String)
    case getAnnouncements

    public var path: String {
        switch self {
        case .getAnnouncements:
            return "announcements"
        case .hideAnnouncement:
            return "hide_announcement"
        case .clickAnnouncement:
            return "click_announcement"
        }
    }

    public var params: Parameters {
        switch self {
        case .getAnnouncements:
            return [:]
        case .clickAnnouncement(let announcementId):
            return ["announcement_id": announcementId]
        case .hideAnnouncement(let announcementId):
            return ["announcement_id": announcementId]
        }
    }

    public var httpMethod: HTTPMethod {
        switch self {
        case .clickAnnouncement,
            .hideAnnouncement:
            return .post

        default:
            return .get
        }
    }

    public var httpEncodingType: HTTPEncodingType {
        return .urlencode
    }

    public var encoded: Data? {
        return nil
    }
}
