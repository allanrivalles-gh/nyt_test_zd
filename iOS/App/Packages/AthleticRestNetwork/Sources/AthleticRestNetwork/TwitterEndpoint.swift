//
//  TwitterEndpoint.swift
//
//
//  Created by Eric Yang on 29/4/20.
//

import Foundation

// MARK: TwitterEndpoint
public enum TwitterEndpoint: Endpoint {
    case twitterOembed(payload: TwitterOembedPayload)

    public var path: String {
        return "oembed"
    }

    public var params: Parameters {
        switch self {
        case .twitterOembed(let payload):
            return encodeToURL(t: payload)
        }
    }

    public var httpMethod: HTTPMethod {
        return .get
    }

    public var encoded: Data? {
        return nil
    }

    public var apiVersion: NetworkAPIVersion? {
        return nil
    }

}
