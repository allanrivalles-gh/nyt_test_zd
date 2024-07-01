//
//  Network+Extensions.swift
//
//
//  Created by Kyle Browning on 12/14/19.
//

import Combine
import Foundation

// MARK: - URL
extension URL {
    var simplifiedSuffix: String {
        let comps = pathComponents.filter({ $0 != "/" && !$0.isEmpty })
        return comps.joined(separator: "/")
    }
}

extension URLComponents {
    mutating func setQueryItems(with parameters: [String: Any]) {
        self.queryItems = parameters.map { URLQueryItem(name: $0.key, value: "\($0.value)") }
    }
}

extension JSONEncoder {
    public static let `default`: JSONEncoder = {
        return JSONEncoder()
    }()
}

extension JSONDecoder {
    public static let snakeCaseConverting: JSONDecoder = {
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        return decoder
    }()
}

extension Publisher where Output == Data {
    public func decode<T: Decodable>(
        as type: T.Type = T.self,
        using decoder: JSONDecoder = .snakeCaseConverting
    ) -> Publishers.Decode<Self, T, JSONDecoder> {
        decode(type: type, decoder: decoder)
    }
}

extension Publisher {
    public func customValidate(
        using validator: @escaping (Output) throws -> Void
    ) -> Publishers.TryMap<Self, Output> {
        tryMap { output in
            try validator(output)
            return output
        }
    }
}

extension Publisher {
    public func unwrap<T>(
        orThrow error: @escaping @autoclosure () -> Failure
    ) -> Publishers.TryMap<Self, T> where Output == T? {
        tryMap { output in
            switch output {
            case .some(let value):
                return value
            case nil:
                throw error()
            }
        }
    }
}

extension Publisher {
    public func convertToResult() -> AnyPublisher<Result<Output, Failure>, Never> {
        self.map(Result.success)
            .catch { Just(.failure($0)) }
            .eraseToAnyPublisher()
    }
}
