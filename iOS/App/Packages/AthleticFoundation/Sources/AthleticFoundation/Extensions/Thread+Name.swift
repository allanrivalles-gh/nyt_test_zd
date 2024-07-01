//
//  Thread+Name.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 21/6/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

extension Thread {
    public static var currentName: String {
        if let underlyingDispatchQueue = OperationQueue.current?.underlyingQueue?.label {
            return underlyingDispatchQueue
        } else {
            let name = __dispatch_queue_get_label(nil)
            return String(cString: name, encoding: .utf8) ?? Thread.current.description
        }
    }
}
