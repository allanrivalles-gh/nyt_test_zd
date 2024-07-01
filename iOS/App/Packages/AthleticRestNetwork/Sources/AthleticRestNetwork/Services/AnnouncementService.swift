import Foundation

public struct AnnouncementService {

    public static func hideAnnouncement(
        forAnnouncementId announcementId: String,
        on network: Network
    ) -> ATHNetworkPublisher<Data> {
        Service.request(
            for: AnnouncementEndoint.hideAnnouncement(announcementId: announcementId),
            on: network
        )
    }

    public static func clickAnnouncement(
        forAnnouncementId announcementId: String,
        on network: Network
    ) -> ATHNetworkPublisher<Data> {
        Service.request(
            for: AnnouncementEndoint.clickAnnouncement(announcementId: announcementId),
            on: network
        )
    }
}
