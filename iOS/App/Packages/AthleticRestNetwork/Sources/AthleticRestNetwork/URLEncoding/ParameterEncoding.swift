//
//  ParameterEncoding.swift

import Foundation

/// A dictionary of parameters to apply to a `URLRequest`.
public typealias Parameters = [String: Any]

/// A type used to define how a set of parameters are applied to a `URLRequest`.
public protocol ParameterEncoding {
    /// Creates a `URLRequest` by encoding parameters and applying them on the passed request.
    ///
    /// - Parameters:
    ///   - urlRequest: `URLRequestConvertible` value onto which parameters will be encoded.
    ///   - parameters: `Parameters` to encode onto the request.
    ///
    /// - Returns:      The encoded `URLRequest`.
    /// - Throws:       Any `Error` produced during parameter encoding.
    func encode(_ urlRequest: URLRequest, with parameters: Parameters?) throws -> URLRequest
}

// MARK: -

/// Creates a url-encoded query string to be set as or appended to any existing URL query string or set as the HTTP
/// body of the URL request. Whether the query string is set or appended to any existing URL query string or set as
/// the HTTP body depends on the destination of the encoding.
///
/// The `Content-Type` HTTP header field of an encoded request with HTTP body is set to
/// `application/x-www-form-urlencoded; charset=utf-8`.
///
/// There is no published specification for how to encode collection types. By default the convention of appending
/// `[]` to the key for array values (`foo[]=1&foo[]=2`), and appending the key surrounded by square brackets for
/// nested dictionary values (`foo[bar]=baz`) is used. Optionally, `ArrayEncoding` can be used to omit the
/// square brackets appended to array keys.
///
/// `BoolEncoding` can be used to configure how boolean values are encoded. The default behavior is to encode
/// `true` as 1 and `false` as 0.
public struct URLFormEncoder: ParameterEncoding {
    // MARK: Helper Types

    /// Defines whether the url-encoded query string is applied to the existing query string or HTTP body of the
    /// resulting URL request.
    public enum Destination {
        /// Applies encoded query string result to existing query string for `GET`, `HEAD` and `DELETE` requests and
        /// sets as the HTTP body for requests with any other HTTP method.
        case methodDependent
        /// Sets or appends encoded query string result to existing query string.
        case queryString
        /// Sets encoded query string result as the HTTP body of the URL request.
        case httpBody

        func encodesParametersInURL(for method: HTTPMethod) -> Bool {
            switch self {
            case .methodDependent: return [.get].contains(method)
            case .queryString: return true
            case .httpBody: return false
            }
        }
    }

    /// Configures how `Array` parameters are encoded.
    public enum ArrayEncoding {
        /// An empty set of square brackets is appended to the key for every value. This is the default behavior.
        case brackets
        /// No brackets are appended. The key is encoded as is.
        case noBrackets

        func encode(key: String) -> String {
            switch self {
            case .brackets:
                return "\(key)[]"
            case .noBrackets:
                return key
            }
        }
    }

    /// Configures how `Bool` parameters are encoded.
    public enum BoolEncoding {
        /// Encode `true` as `1` and `false` as `0`. This is the default behavior.
        case numeric
        /// Encode `true` and `false` as string literals.
        case literal

        func encode(value: Bool) -> String {
            switch self {
            case .numeric:
                return value ? "1" : "0"
            case .literal:
                return value ? "true" : "false"
            }
        }
    }

    // MARK: Properties

    /// Returns a default `URLFormEncoder` instance with a `.methodDependent` destination.
    public static var `default`: URLFormEncoder { URLFormEncoder() }

    /// Returns a `URLFormEncoder` instance with a `.queryString` destination.
    public static var queryString: URLFormEncoder { URLFormEncoder(destination: .queryString) }

    /// The destination defining where the encoded query string is to be applied to the URL request.
    public let destination: Destination

    /// The encoding to use for `Array` parameters.
    public let arrayEncoding: ArrayEncoding

    /// The encoding to use for `Bool` parameters.
    public let boolEncoding: BoolEncoding

    // MARK: Initialization

    /// Creates an instance using the specified parameters.
    ///
    /// - Parameters:
    ///   - destination:   `Destination` defining where the encoded query string will be applied. `.methodDependent` by
    ///                    default.
    ///   - arrayEncoding: `ArrayEncoding` to use. `.brackets` by default.
    ///   - boolEncoding:  `BoolEncoding` to use. `.numeric` by default.
    public init(
        destination: Destination = .methodDependent,
        arrayEncoding: ArrayEncoding = .brackets,
        boolEncoding: BoolEncoding = .numeric
    ) {
        self.destination = destination
        self.arrayEncoding = arrayEncoding
        self.boolEncoding = boolEncoding
    }

    // MARK: Encoding

    public func encode(_ urlRequest: URLRequest, with parameters: Parameters?) -> URLRequest {
        var urlRequest = urlRequest
        guard let parameters = parameters else { return urlRequest }
        guard let methodString = urlRequest.httpMethod else { return urlRequest }
        guard let method = HTTPMethod(rawValue: methodString) else { return urlRequest }
        if destination.encodesParametersInURL(for: method) {
            if var urlComponents = URLComponents(
                url: urlRequest.url!,
                resolvingAgainstBaseURL: false
            ), !parameters.isEmpty {
                let percentEncodedQuery =
                    (urlComponents.percentEncodedQuery.map { $0 + "&" } ?? "") + query(parameters)
                urlComponents.percentEncodedQuery = percentEncodedQuery
                urlRequest.url = urlComponents.url
            }
        } else {
            if urlRequest.value(forHTTPHeaderField: "Content-Type") == nil {
                urlRequest.setValue(
                    "application/x-www-form-urlencoded; charset=utf-8",
                    forHTTPHeaderField: "Content-Type"
                )
            }

            urlRequest.httpBody = Data(query(parameters).utf8)
        }

        return urlRequest
    }

    /// Creates a percent-escaped, URL encoded query string components from the given key-value pair recursively.
    ///
    /// - Parameters:
    ///   - key:   Key of the query component.
    ///   - value: Value of the query component.
    ///
    /// - Returns: The percent-escaped, URL encoded query string components.
    public func queryComponents(fromKey key: String, value: Any) -> [(String, String)] {
        var components: [(String, String)] = []

        if let dictionary = value as? [String: Any] {
            for (nestedKey, value) in dictionary {
                components += queryComponents(fromKey: "\(key)[\(nestedKey)]", value: value)
            }
        } else if let array = value as? [Any] {
            for value in array {
                components += queryComponents(fromKey: arrayEncoding.encode(key: key), value: value)
            }
        } else if let value = value as? NSNumber {
            if value.isBool {
                components.append(
                    (escape(key), escape(boolEncoding.encode(value: value.boolValue)))
                )
            } else {
                components.append((escape(key), escape("\(value)")))
            }
        } else if let bool = value as? Bool {
            components.append((escape(key), escape(boolEncoding.encode(value: bool))))
        } else {
            // Safe fallback to allways encode a string
            components.append((escape(key), escape("\(value)")))
        }

        return components
    }

    /// Creates a percent-escaped string following RFC 3986 for a query string key or value.
    ///
    /// - Parameter string: `String` to be percent-escaped.
    ///
    /// - Returns:          The percent-escaped `String`.
    public func escape(_ string: String) -> String {
        string.addingPercentEncoding(withAllowedCharacters: .URLQueryAllowed) ?? string
    }

    private func query(_ parameters: [String: Any]) -> String {
        var components: [(String, String)] = []

        for key in parameters.keys.sorted(by: <) {
            let value = parameters[key]!
            components += queryComponents(fromKey: key, value: value)
        }
        return components.map { "\($0)=\($1)" }.joined(separator: "&")
    }
}

// MARK: -

extension NSNumber {
    fileprivate var isBool: Bool {
        // Use Obj-C type encoding to check whether the underlying type is a `Bool`, as it's guaranteed as part of
        // swift-corelibs-foundation, per [this discussion on the Swift forums](https://forums.swift.org/t/alamofire-on-linux-possible-but-not-release-ready/34553/22).
        String(cString: objCType) == "c"
    }
}
