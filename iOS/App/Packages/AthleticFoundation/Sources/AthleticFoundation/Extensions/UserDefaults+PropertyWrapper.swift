//
//  UserDefaults+PropertyWrapper.swift
//
//  Created by Mark Corbyn on 12/1/2023.
//

import Foundation

@propertyWrapper
public struct UserDefault<Value> {

    private let queue = DispatchQueue(label: "com.theathletic.UserDefaultAtomic")

    public let key: String
    public let defaultValue: Value?
    public var container: UserDefaults

    public init(key: String, defaultValue: Value? = nil, container: UserDefaults = .standard) {
        self.key = key
        self.defaultValue = defaultValue
        self.container = container
    }

    public var wrappedValue: Value! {
        get {
            queue.sync {
                container.object(forKey: key) as? Value ?? defaultValue
            }
        }
        set {
            queue.sync {
                if newValue != nil {
                    container.set(newValue, forKey: key)
                } else {
                    container.removeObject(forKey: key)
                }
            }
        }
    }
}

@propertyWrapper
public struct RawRepresentableStorage<T: RawRepresentable> {

    let key: String
    let defaultValue: T?
    var container: UserDefaults

    public init(key: String, defaultValue: T? = nil, container: UserDefaults = .standard) {
        self.key = key
        self.defaultValue = defaultValue
        self.container = container
    }

    public var wrappedValue: T! {
        get {
            guard let object = container.object(forKey: key) as? T.RawValue else {
                return defaultValue
            }

            return T(rawValue: object) ?? defaultValue
        }
        set {
            container.set(newValue.rawValue, forKey: key)
        }
    }
}
