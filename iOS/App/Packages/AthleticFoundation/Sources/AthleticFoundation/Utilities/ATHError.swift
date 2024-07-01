//
//  Errors.swift
//  theathletic-ios
//
//  Created by Jan Remes on 18/07/16.
//  Copyright © 2016 The Athletic. All rights reserved.
//

import Foundation

public enum AthError: LocalizedError, Equatable {

    case userIdMismatch
    case userIdMissing
    case userIsNotLoggedIn
    case userCancelledRegistration
    case resourceIsNotAvailable
    case generalNetworkRequestError
    case articleJsonFailure
    case missingParams
    case feedLoadingError(reason: String)
    case userIsMissing
    case feedItemJSONFailure
    case databaseDeleted
    case endDateFormatWrong
    case fbLoginFailed
    case webviewTerminated
    case testingError
    case locationNotAvailable
    case databaseTooLarge
    case completeAccountReturnedEmptyTeamsLeagues
    case commentsParsingFailed
    case missingDiscountIdentifier
    case userIdIsNotNumeric
    case jsonParsingFailure
    case tooManyRequests
    case forceCancelled(reason: String)
    case appleLoginFailed
    case googleLoginFailed
    case newNetworkDoesntExist
    case restNetworkDoesntExist
    case articleReferralDoesntExist
    case diskSaveError
    case failedQueryError
    case failedMutationError
    case failedMutationErrorWithMessage(message: String)
    case impressionBadRequest
    case headlineContainerError
    case timeout
    case missingData
    case purchaseFailed(message: String)
    case loggingPurchaseFailed
    case webAuthenticationSessionError
    case taskCancelled

    public var errorDescription: String? {
        switch self {
        case .userIdMismatch:
            return "User id is not matching that present in profile"
        case .userIdMissing:
            return "User id is missing"
        case .userCancelledRegistration:
            return "User cancelled registration."
        case .resourceIsNotAvailable:
            return "Resource is not available"
        case .generalNetworkRequestError:
            return "Response does not contain response status in JSON."
        case .articleJsonFailure:
            return "Article couldnt be parsed"
        case .missingParams:
            return "The request is missing parameters. Please try again."
        case .userIsNotLoggedIn:
            return "User not logged in"
        case .feedLoadingError(let reason):
            return "Feed Service Error: \(reason)"
        case .userIsMissing:
            return "User is missing, please log in"
        case .feedItemJSONFailure:
            return "JSON is not valid"
        case .databaseDeleted:
            return "Database corrupted and needs to be deleted"
        case .endDateFormatWrong:
            return "End date was here and now its gone"
        case .fbLoginFailed:
            return "Facebook Login Failed"
        case .webviewTerminated:
            return "WKWebView terminated unexpectedly"
        case .testingError:
            return "Just for testing"
        case .locationNotAvailable:
            return "Location service is not available"
        case .databaseTooLarge:
            return "Database size is very huge"
        case .completeAccountReturnedEmptyTeamsLeagues:
            return "Complete account returned needs repeat onboarding"
        case .commentsParsingFailed:
            return "Comments parsing failed"
        case .missingDiscountIdentifier:
            return "Missing payment discount identifier"
        case .userIdIsNotNumeric:
            return "User id is not numeric"
        case .jsonParsingFailure:
            return "It's not possible to parse response"
        case .tooManyRequests:
            return "Too many requests"
        case .forceCancelled(let reason):
            return "Force canceled the request, reason: \(reason)"
        case .appleLoginFailed:
            return "Apple Login Failed"
        case .googleLoginFailed:
            return "Google Login Failed"
        case .newNetworkDoesntExist:
            return "New Network does not exist"
        case .restNetworkDoesntExist:
            return "REST network does not exist"
        case .articleReferralDoesntExist:
            return "No article id found"
        case .diskSaveError:
            return "Cannot save to disk"
        case .failedQueryError:
            return "GraphQL query failed"
        case .failedMutationError:
            return "GraphQL returned false on attempted mutation"
        case .impressionBadRequest:
            return "Conversion of JSON to Avro failed"
        case .headlineContainerError:
            return "Headline Container failed to load data"
        case .timeout:
            return "Request timed out"
        case .failedMutationErrorWithMessage(let message):
            return "Request failed: \(message)"
        case .missingData:
            return "Missing data"
        case .webAuthenticationSessionError:
            return "Error establishing ASWebAuthenticationSession"
        case .purchaseFailed(let message):
            return "\(message)"
        case .loggingPurchaseFailed:
            return "Logging to the backend failed"
        case .taskCancelled:
            return "The running task was cancelled"
        }
    }

    public var _domain: String {
        return "AthErrorDomain"
    }

    public var _code: Int {
        switch self {
        case .userIdMismatch:
            return 1
        case .userIdMissing:
            return 2
        case .userCancelledRegistration:
            return 3
        case .resourceIsNotAvailable:
            return 4
        case .generalNetworkRequestError:
            return 5
        case .articleJsonFailure:
            return 6
        case .missingParams:
            return 7
        case .userIsNotLoggedIn:
            return 8
        case .feedLoadingError:
            return 9
        case .userIsMissing:
            return 10
        case .feedItemJSONFailure:
            return 11
        case .databaseDeleted:
            return 12
        case .endDateFormatWrong:
            return 13
        case .fbLoginFailed:
            return 15
        case .webviewTerminated:
            return 16
        case .testingError:
            return 17
        case .locationNotAvailable:
            return 18
        case .databaseTooLarge:
            return 19
        case .completeAccountReturnedEmptyTeamsLeagues:
            return 20
        case .commentsParsingFailed:
            return 21
        case .missingDiscountIdentifier:
            return 22
        case .jsonParsingFailure:
            return 23
        case .tooManyRequests:
            return 24
        case .appleLoginFailed:
            return 25
        case .googleLoginFailed:
            return 26
        case .newNetworkDoesntExist:
            return 27
        case .articleReferralDoesntExist:
            return 28
        case .diskSaveError:
            return 29
        case .failedMutationError:
            return 30
        case .impressionBadRequest:
            return 31
        case .forceCancelled:
            return 32
        case .headlineContainerError:
            return 33
        case .timeout:
            return 34
        case .failedMutationErrorWithMessage:
            return 35
        case .missingData:
            return 36
        case .failedQueryError:
            return 37
        case .webAuthenticationSessionError:
            return 38
        case .restNetworkDoesntExist:
            return 39
        case .purchaseFailed:
            return 40
        case .userIdIsNotNumeric:
            return 41
        case .loggingPurchaseFailed:
            return 42
        case .taskCancelled:
            return 43
        }
    }
}
