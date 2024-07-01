//
//  APIEndoint.swift
//
//
//  Created by Kyle Browning on 10/30/19.
//

import Foundation

public enum HTTPEncodingType {
    case json
    case urlencode

    public var contentType: String {
        switch self {
        case .json:
            return "application/json"
        case .urlencode:
            return "application/x-www-form-urlencoded; charset=utf-8"
        }
    }
}

public enum NetworkAPIEndpoint: Endpoint {

    // MARK: Gifts
    case gifts
    case purchaseGift(payload: GiftPurchasePayload)

    // MARK: Email Settings
    case emailSettings
    case togglePromoEmail(params: [String: Any])
    case addUserEmail(params: [String: Any])
    case removeUserEmail(params: [String: Any])

    // MARK: Comments
    case getArticleComments(payload: CommentsGetPayload)
    case toggleLike(payload: CommentsToggleLikePayload)
    case postComment(payload: CommentsPostPayload)
    case flagComment(payload: FlagCommentPayload)
    case editComment(payload: EditCommentPayload)
    case deleteComment(payload: DeleteCommentPayload)
    case getLastCommentDate(payload: GetLastCommentDatePayload)

    // MARK: Report Cards
    case addReportCardComment(cardId: Int, comment: String)
    case addReportCardRating(cardId: Int, rating: Int, review: String)
    case flagReportCardComment(commentId: Int)

    // Log Analytics
    case logAnalytics(params: [String: Any])

    // MARK: Feed V2
    case getArticle(payload: CoffeeGetArticleProtocol)
    case getRecommendedTeamsGrouped(payload: CoffeeGetRecommendedTeamsPayload)
    case getRecommendedTeams(payload: CoffeeGetRecommendedTeamsPayload)
    case getRelatedArticles(payload: CoffeeGetRelatedArticlesPayload)
    case getAuthorDetail(payload: CoffeeGetAuthorDetailPayload)

    // MARK: Article

    case updateArticleSavedState(payload: ArticleSavedPayload)
    case logArticleRating(articleId: String, ratingId: Int)
    case searchArticle(searchText: String)

    // MARK: Test Endpoints
    case aBadURL
    case ok
    case encodedOk
    case unauthorized
    case serverError

    public var path: String {
        switch self {
        case .aBadURL:
            return "aBadURLTo404"
        case .ok, .encodedOk:
            return "ok"
        case .unauthorized:
            return "unauthorized"
        case .serverError:
            return "server_error"
        case .gifts:
            return "gifts"
        case .purchaseGift:
            return "purchase_gift"
        case .emailSettings:
            return "user_emails"
        case .togglePromoEmail:
            return "toggle_promo_email"
        case .addUserEmail:
            return "add_user_email"
        case .removeUserEmail:
            return "remove_user_email"
        case .getArticleComments:
            return "article_comments"
        case .toggleLike(let payload):
            return payload.isLiked ? "like_comment" : "unlike_comment"
        case .updateArticleSavedState(let payload):
            return payload.isSaved ? "save_user_article" : "unsave_user_article"
        case .postComment:
            return "add_comment"
        case .flagComment:
            return "flag_comment"
        case .editComment:
            return "edit_comment"
        case .deleteComment:
            return "delete_comment"
        case .getLastCommentDate(let payload):
            return "latest_article_comment/\(payload.discussionId)"
        case .getArticle(let payload):
            return "articles/\(payload.id)"
        case .getRecommendedTeamsGrouped:
            return "recommended_grouped_teams"
        case .getRecommendedTeams:
            return "recommended_teams"
        case .getRelatedArticles:
            return "article_related"
        case .logArticleRating:
            return "log_article_rating"
        case .searchArticle:
            return "article_search"
        case .getAuthorDetail(let payload):
            return "authors/\(payload.authorId)"
        case .logAnalytics:
            return "log_analytics"
        case .addReportCardComment:
            return "add_card_comment"
        case .addReportCardRating:
            return "add_card_review"
        case .flagReportCardComment:
            return "flag_ep_comment"
        }
    }

    public var params: [String: Any] {
        switch self {
        case .togglePromoEmail(let params),
            .addUserEmail(let params),
            .removeUserEmail(let params),
            .logAnalytics(let params):
            return params
        case .purchaseGift(let payload):
            return encodeToURL(t: payload)
        case .getArticleComments(let payload):
            return encodeToURL(t: payload)
        case .toggleLike(let payload):
            return encodeToURL(t: payload)
        case .postComment(let payload):
            return encodeToURL(t: payload)
        case .flagComment(let payload):
            return encodeToURL(t: payload)
        case .editComment(let payload):
            return encodeToURL(t: payload)
        case .deleteComment(let payload):
            return encodeToURL(t: payload)
        case .updateArticleSavedState(let payload):
            return encodeToURL(t: payload)
        case .getRecommendedTeams(let payload),
            .getRecommendedTeamsGrouped(let payload):
            return encodeToURL(t: payload)
        case .getRelatedArticles(let payload):
            return encodeToURL(t: payload)
        case .logArticleRating(let articleId, let ratingId):
            return ["article_id": articleId, "rating_id": ratingId, "platform": "iOS"]
        case .searchArticle(let searchText):
            return ["search_text": searchText]
        case .addReportCardComment(let cardId, let comment):
            return ["card_id": cardId, "comment": comment]
        case .addReportCardRating(let cardId, let rating, let review):
            return ["card_id": cardId, "rating": rating, "review": review]
        case .flagReportCardComment(let commentId):
            return ["comment_id": commentId]
        default:
            return [:]
        }
    }

    public var httpMethod: HTTPMethod {
        switch self {
        case .purchaseGift,
            .togglePromoEmail,
            .addUserEmail,
            .removeUserEmail,
            .toggleLike,
            .postComment,
            .flagComment,
            .editComment,
            .deleteComment,
            .encodedOk,
            .updateArticleSavedState,
            .searchArticle:
            return .post

        default:
            return .get
        }
    }

    public var encoded: Data? {
        return nil
    }

    public var apiVersion: NetworkAPIVersion? {
        switch self {
        case .getArticleComments(let payload):
            return payload.useCached ? .v5cached : .v5
        case .getArticle(let payload):
            return payload.useCached ? .v5cached : .v5
        case .getRelatedArticles:
            return .v5cached
        default:
            return .v5
        }
    }
}

public protocol Endpoint {
    typealias Parameters = [String: Any]

    var path: String { get }
    var params: Parameters { get }
    var httpMethod: HTTPMethod { get }
    var httpEncodingType: HTTPEncodingType { get }
    var encoded: Data? { get }
    var apiVersion: NetworkAPIVersion? { get }
    var decoder: JSONDecoder { get }
    var encoder: JSONEncoder { get }
    var dateFormatter: DateFormatter { get }
    var queryKey: String { get }
    func encodeToURL<T: Encodable>(t: T) -> Parameters
}

extension Endpoint {
    public var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.timeZone = TimeZone(abbreviation: "GMT")
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter
    }
    // swiftlint:disable force_try force_cast
    public func encodeToURL<T: Encodable>(t: T) -> [String: Any] {
        // If this fails its going to be because encoder cant encode an Encodable?
        // We can remove this when backend accepts application/json
        let json = try! encoder.encode(t)
        return try! JSONSerialization.jsonObject(with: json, options: []) as! [String: Any]
    }

    // swiftlint:disable force_try force_cast
    public func encodeToURLCustomEncoder<T: Encodable>(t: T, encoder: JSONEncoder) -> [String: Any]
    {
        // If this fails its going to be because encoder cant encode an Encodable?
        // We can remove this when backend accepts application/json
        let json = try! encoder.encode(t)
        return try! JSONSerialization.jsonObject(with: json, options: []) as! [String: Any]
    }

    public var decoder: JSONDecoder {
        // Customise JSON decoder
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        decoder.dateDecodingStrategy = .custom({ decoder -> Date in
            let container = try decoder.singleValueContainer()
            if let dateString = try? container.decode(String.self) {
                return self.dateFormatter.date(from: dateString) ?? Date.distantPast
            } else if let seconds = try? container.decode(Int.self),
                let interval = TimeInterval(exactly: seconds)
            {
                return Date(timeIntervalSince1970: interval)
            } else if let milliseconds = try? container.decode(Double.self) {
                return Date(timeIntervalSince1970: milliseconds / 1000)
            } else {
                return Date.distantPast
            }
        })
        return decoder
    }

    public var encoder: JSONEncoder {
        let encoder = JSONEncoder()
        encoder.keyEncodingStrategy = .convertToSnakeCase
        encoder.dateEncodingStrategy = .formatted(dateFormatter)
        return encoder
    }

    public var apiVersion: NetworkAPIVersion? {
        return .v5
    }

    public var httpEncodingType: HTTPEncodingType {
        return .urlencode
    }

    public var queryKey: String {
        return ""
    }
}
