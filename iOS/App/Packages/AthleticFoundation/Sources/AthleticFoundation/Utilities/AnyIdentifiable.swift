//
//  AnyIdentifiable.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 6/5/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

/// A type-erased wrapper around objects conforming to `Identifiable` protocol.
/// Boxes up the given Identifiable object to solve Swift type compiler issues with referencing generic protocols that have Self or
/// associated type requirements.
/// Much like other objects Apple provide for us like `AnyHashable`, `AnySequence` etc.
/// However, Apple's versions have magical auto-boxing/unboxing when using `as?`, `as!`. We don't get the same luxury so we
/// have to manually box and unbox in our code:
///
///     let boxed = AnyIdentifiable(myIdentifiableThing)
///     let unboxed = boxed.id
public struct AnyIdentifiable: Identifiable {
    private let box: AnyIdentifiableBox

    public init<T: Identifiable>(_ identifiable: T) {
        box = ConcreteIdentifiableBox(identifiable)
    }

    public var id: AnyHashable {
        return box.id
    }

    public var base: Any {
        return box.base
    }
}

// MARK: - Private types

private protocol AnyIdentifiableBox {
    var id: AnyHashable { get }
    var base: Any { get }
}

private struct ConcreteIdentifiableBox<Base: Identifiable>: AnyIdentifiableBox {
    let _base: Base

    init(_ base: Base) {
        self._base = base
    }

    var id: AnyHashable {
        return _base.id
    }

    var base: Any {
        return _base
    }
}
