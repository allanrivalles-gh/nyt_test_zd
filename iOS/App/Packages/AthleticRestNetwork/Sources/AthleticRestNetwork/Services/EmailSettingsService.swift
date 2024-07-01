//
//  EmailSettingsService.swift
//
//
//  Created by Kyle Browning on 11/7/19.
//

import Combine
import Foundation

public struct EmailSettingService {

    public static func getEmailSettings(on network: Network) -> ATHNetworkPublisher<EmailSettings> {
        Service.requestAndDecode(for: NetworkAPIEndpoint.emailSettings, on: network)
    }

    public static func togglePromoSetting(for endpoint: NetworkAPIEndpoint, on network: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(for: endpoint, on: network)
    }

}
