//
//  PublishedValue.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 24/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Combine
import Foundation

public final class PublishedValue<Value> {
    @Published public var value: Value

    private var updates: AnyCancellable?

    public init(value: Value, updates: AnyPublisher<Value, Never>) {
        self.value = value
        self.updates = updates.sink { [weak self] in
            self?.value = $0
        }
    }
}
