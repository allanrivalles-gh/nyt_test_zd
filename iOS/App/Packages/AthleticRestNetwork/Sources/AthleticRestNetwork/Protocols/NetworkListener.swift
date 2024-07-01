//
//  AthNetworkRequestListener.swift
//
//
//  Created by Kyle Browning on 11/5/19.
//

import Foundation

public protocol NetworkListener {
    func requestFinished(endpoint: String, data: Data?)
    func requestFailed(endpoint: String, error: Error, data: Data?)
}

extension NetworkListener {
    public func requestFinished(endpoint: String, data: Data?) {}
    public func requestFailed(endpoint: String, error: Error, data: Data?) {}
}
