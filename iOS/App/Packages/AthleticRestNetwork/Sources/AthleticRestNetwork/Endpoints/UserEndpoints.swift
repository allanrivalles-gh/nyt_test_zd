//
//  UserEndpoints.swift
//
//
//  Created by Kyle Browning on 1/22/20.
//

import Foundation

public enum UserEndpoint: Endpoint {
    case authWithPassword(payload: UserPasswordPayload)
    case getUserDynamicData(payload: SimpleUserPayload)
    case getUserProfile(payload: SimpleUserPayload)
    case createReferralUrl(payload: SimpleUserPayload)
    case isEmailUnique(payload: SimpleEmailPayload)
    case updateCurrentUser(payload: UpdateUserPayload)
    case updateUserNotifications(payload: UserNotificationPayload)
    case getSavedArticles
    case onboarding
    case setUserTopics(payload: UserTopicsPayload)
    case updateUserPolicyAgreement(payload: UserPolicy)
    case updateArticleReadState(payload: ArticleReadPayload)
    case logAppleSub(params: [String: Any])
    case subscribtionOfferEligible(params: [String: Any])
    case introAnnualOfferEligible(params: [String: Any])

    public var path: String {
        switch self {
        case .subscribtionOfferEligible:
            return "app_promotional_offer_eligible"
        case .introAnnualOfferEligible:
            return "app_introductory_annual_offer_eligible"
        case .authWithPassword:
            return "auth"
        case .getUserDynamicData(let payload):
            return "user_dynamic_data/\(payload.userId)"
        case .getUserProfile(let payload):
            return "customer/\(payload.userId)"
        case .logAppleSub:
            return "log_apple_sub"
        case .isEmailUnique:
            return "is_email_unique"
        case .updateUserNotifications(let payload):
            return payload.isSubscribed ? "add_user_notification" : "remove_user_notification"
        case .updateCurrentUser:
            return "edit_customer"
        case .onboarding:
            return "onboarding"
        case .getSavedArticles:
            return "get_user_articles"
        case .setUserTopics:
            return "set_user_topics"
        case .updateUserPolicyAgreement:
            return "update_user_policy_agreement"
        case .createReferralUrl:
            return "create_referral_url"
        case .updateArticleReadState:
            return "log_article_read"
        }
    }

    public var params: Parameters {
        switch self {
        case .getUserDynamicData,
            .getUserProfile,
            .onboarding,
            .getSavedArticles,
            .authWithPassword:
            return [:]
        case .logAppleSub(let params):
            return params
        case .subscribtionOfferEligible(let params):
            return params
        case .introAnnualOfferEligible(let params):
            return params
        case .isEmailUnique(let payload):
            return encodeToURL(t: payload)
        case .updateCurrentUser(let payload):
            return encodeToURL(t: payload)
        case .updateUserNotifications(let payload):
            return encodeToURL(t: payload)
        case .setUserTopics(let payload):
            return encodeToURL(t: payload)
        case .updateUserPolicyAgreement(let payload):
            return encodeToURL(t: payload)
        case .createReferralUrl(let payload):
            return encodeToURL(t: payload)
        case .updateArticleReadState(let payload):
            return encodeToURL(t: payload)
        }
    }

    public var httpMethod: HTTPMethod {
        switch self {
        case .authWithPassword,
            .updateCurrentUser,
            .updateUserNotifications,
            .setUserTopics,
            .updateUserPolicyAgreement,
            .createReferralUrl,
            .logAppleSub,
            .updateArticleReadState:
            return .post

        default:
            return .get
        }
    }

    public var httpEncodingType: HTTPEncodingType {
        switch self {
        case .authWithPassword:
            return .json
        default:
            return .urlencode
        }
    }

    public var encoded: Data? {
        switch self {
        case .authWithPassword(let payload):
            return try? self.encoder.encode(payload)
        default:
            return nil
        }
    }
}
