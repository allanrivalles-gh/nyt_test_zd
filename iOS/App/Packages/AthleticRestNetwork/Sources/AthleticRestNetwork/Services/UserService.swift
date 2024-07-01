//
//  UserService.swift
//
//
//  Created by Kyle Browning on 12/24/19.
//

import Combine
import Foundation

public struct UserService {
    public static func authenticate(with payload: UserPasswordPayload, on: Network)
        -> ATHNetworkPublisher<User>
    {
        Service.requestAndDecode(for: UserEndpoint.authWithPassword(payload: payload), on: on)
    }

    public static func updateArticleReadState(for payload: ArticleReadPayload, on: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(for: UserEndpoint.updateArticleReadState(payload: payload), on: on)
    }

    public static func createReferralUrl(for payload: SimpleUserPayload, on: Network)
        -> ATHNetworkPublisher<ReferralResult>
    {
        Service.requestAndDecode(for: UserEndpoint.createReferralUrl(payload: payload), on: on)
    }

    /**
     Get user dynamic data
     - Parameter payload: the instance of [SimpleUserPayload](x-source-tag://SimpleUserPayload).
     - Parameter network: the network instance initialized by APIManager.
     - Returns: The [NetworkDataTask](x-source-tag://NetworkDataTask) which contains the instance of URLRequest and the response within ATHNetworkPublisher, as well as a task.
     ### Usage Example: ###
     ```
     let dataTask = UserDynamicDataService(for: payload, on: network)
     let ATHNetworkPublisher = dataTask.dataFuture
     ATHNetworkPublisher.whenComplete { responseData in
        switch responseData {
        case .success(let data):
        // Decode to your type
        try! decoder.decode(UserDynamicData.self, from: data)
        case .failure(let error):
     }
     ```
    */
    public static func getDynamicData(for payload: SimpleUserPayload, on network: Network)
        -> ATHNetworkPublisher<DynamicData>
    {
        Service.requestAndDecode(
            for: UserEndpoint.getUserDynamicData(payload: payload),
            on: network
        )
    }

    public static func getUserProfile(for payload: SimpleUserPayload, on network: Network)
        -> ATHNetworkPublisher<UserClass>
    {
        let endpoint = UserEndpoint.getUserProfile(payload: payload)
        return Service.requestAndDecode(for: endpoint, on: network)
    }

    public static func checkEmailUniqueness(for payload: SimpleEmailPayload, on network: Network)
        -> ATHNetworkPublisher<EmailUniqueness>
    {
        Service.requestAndDecode(for: UserEndpoint.isEmailUnique(payload: payload), on: network)
    }

    public static func updateCurrentUser(for payload: UpdateUserPayload, on network: Network)
        -> ATHNetworkPublisher<SimpleResult>
    {
        Service.requestAndDecode(for: UserEndpoint.updateCurrentUser(payload: payload), on: network)
    }

    public static func updateNotificationSetting(
        for payload: UserNotificationPayload,
        on network: Network
    ) -> ATHNetworkPublisher<SimpleResult> {
        Service.requestAndDecode(
            for: UserEndpoint.updateUserNotifications(payload: payload),
            on: network
        )
    }

    public static func setUserTopics(for payload: UserTopicsPayload, on network: Network)
        -> ATHNetworkPublisher<UserClass>
    {
        Service.requestAndDecode(for: UserEndpoint.setUserTopics(payload: payload), on: network)
    }

    public static func updateUserPolicy(for payload: UserPolicy, on network: Network)
        -> ATHNetworkPublisher<UserPolicy>
    {
        Service.requestAndDecode(
            for: UserEndpoint.updateUserPolicyAgreement(payload: payload),
            on: network
        )
    }

    public static func subscriptionOfferAvailable(with params: [String: Any], on network: Network)
        -> ATHNetworkPublisher<Data>
    {
        Service.request(for: UserEndpoint.subscribtionOfferEligible(params: params), on: network)
    }
}
