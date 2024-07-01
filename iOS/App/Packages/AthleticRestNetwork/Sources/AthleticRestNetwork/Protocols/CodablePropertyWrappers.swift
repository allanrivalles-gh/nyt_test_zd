//
//  StringEncodable.swift
//
//
//  Created by Kyle Browning on 12/24/19.
//

import Foundation

public struct IntCodable: Codable, Equatable {
    public var value: Int?

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if let decoded = try? container.decode(Int.self) {
            value = decoded
        } else if let stringDecoded = try? container.decode(String.self),
            let intDecoded = Int(stringDecoded)
        {
            value = intDecoded
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(value)
    }

    public init(_ value: Int?) {
        self.value = value
    }

    public var description: String {
        "\(value ?? -1)"
    }
}

public struct ArrayCodable<T: Codable>: Codable {
    public var value: T?
    public var array: [T]?

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if let decoded = try? container.decode(T.self) {
            value = decoded
        } else if let decoded = try? container.decode(Array<T>.self) {
            array = decoded
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        if let value = value {
            try container.encode(value)
        }
        if let array = array {
            try container.encode(array)
        }
    }
}

/// UGH
// Response might be a single value boolean.
public struct TimestampCodable<T: Codable>: Codable {
    public var value: T?
    public var array: [T]?

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if let decoded = try? container.decode(T.self) {
            value = decoded
        } else if let decoded = try? container.decode(Array<T>.self) {
            array = decoded
        } else if let _ = try? container.decode(Bool.self) {
            value = nil
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        if let value = value {
            try container.encode(value)
        }
        if let array = array {
            try container.encode(array)
        }
    }
}

// MARK: StringCodable
public struct StringCodable: Codable {
    public var value: String?

    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        if let decoded = try? container.decode(String.self) {
            value = decoded
        } else if let decoded = try? container.decode(Decimal.self) {
            value = decoded.description
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(value)
    }
}

// MARk: LosslessStringCodable
public typealias LosslessStringCodable = LosslessStringConvertible & Codable

// MARK: DynamicDecoder
public struct DynamicCoder<T: LosslessStringCodable> {
    public static func decode(from decoder: Decoder) throws -> T? {
        do {
            return try T(from: decoder)
        } catch {
            // Handle different types for the same key error
            func decode<U: LosslessStringCodable>(_: U.Type) -> (Decoder) -> LosslessStringCodable?
            {
                return { try? U.init(from: $0) }
            }
            let types: [(Decoder) -> LosslessStringCodable?] = [
                decode(String.self),
                decode(Bool.self),
                decode(Int.self),
                decode(Double.self),
            ]

            guard let rawValue = types.lazy.compactMap({ $0(decoder) }).first,
                let value = T.init("\(rawValue)")
            else {
                return nil
            }

            return value
        }
    }
}

// MARK: OptionalCodable
public protocol OptionalCodable {
    associatedtype WrappedType: ExpressibleByNilLiteral
    var wrappedValue: WrappedType { get }
    init(wrappedValue: WrappedType)
}

// MARK: CodableValue
@propertyWrapper
public struct CodableValue<T: LosslessStringCodable>: Codable {
    public let wrappedValue: T

    public init(wrappedValue: T) {
        self.wrappedValue = wrappedValue
    }

    public init(from decoder: Decoder) throws {
        let value: T = try DynamicCoder.decode(from: decoder)!
        self.init(wrappedValue: value)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(wrappedValue)
    }
}

// MARK: CodableValueNullable
@propertyWrapper
public struct CodableValueNullable<T: LosslessStringCodable>: Codable {
    public let wrappedValue: T?

    public init(wrappedValue: T?) {
        self.wrappedValue = wrappedValue
    }

    public init(from decoder: Decoder) throws {
        let value: T? = try DynamicCoder.decode(from: decoder)
        self.init(wrappedValue: value)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(wrappedValue)
    }
}

// MARk: CodableValueArray
@propertyWrapper
public struct CodableValueArray<T: LosslessStringCodable>: Codable {
    public var wrappedValue: [T]?

    public init(wrappedValue: [T]?) {
        self.wrappedValue = wrappedValue
    }

    public init(from decoder: Decoder) throws {
        var container = try decoder.unkeyedContainer()

        var elements: [T] = []
        while !container.isAtEnd {
            if let value = try container.decode(CodableValueNullable<T>.self).wrappedValue {
                elements.append(value)
            }
        }
        if elements.count > 0 {
            self.wrappedValue = elements
        } else {
            self.wrappedValue = nil
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.unkeyedContainer()
        guard let wrappedValue = wrappedValue else {
            return try container.encodeNil()
        }
        try container.encode(contentsOf: wrappedValue)
    }
}

// MARK: StringEncoder
@propertyWrapper
public struct StringEncoder<T: LosslessStringCodable>: Codable {
    public var wrappedValue: T

    public init(wrappedValue: T) {
        self.wrappedValue = wrappedValue
    }

    public init(from decoder: Decoder) throws {
        let value: T = try DynamicCoder.decode(from: decoder)!
        self.init(wrappedValue: value)
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(wrappedValue.description)
    }
}

// Extensions to handle missing key error
extension CodableValueNullable: OptionalCodable {}
extension CodableValueArray: OptionalCodable {}

// MARK: KeyedDecodingContainer extension
extension KeyedDecodingContainer {
    // Handle the missing key error
    public func decode<T>(_ type: T.Type, forKey key: KeyedDecodingContainer<K>.Key) throws -> T
    where T: Decodable, T: OptionalCodable {
        return try decodeIfPresent(T.self, forKey: key) ?? T(wrappedValue: nil)
    }
}
