//
//  StorageObject.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 4/14/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticStorage
import Foundation

public protocol StorageObject: Codable {
    static var storageDirectory: Storage.Directory { get }
    static var folderPath: String { get }

    static func storagePath(with id: String) -> String

    var storageIdentifier: String { get }
}

extension StorageObject {
    public static var storageDirectory: Storage.Directory {
        return .caches
    }

    public static var folderPath: String {
        String(describing: Self.self)
    }

    public static func storagePath(with id: String) -> String {
        return folderPath + "/" + "\(id).json"
    }

    public static var allStorageItems: [Self] {
        guard
            let dataArray = try? Storage.retrieve(
                folderPath,
                from: Self.storageDirectory,
                as: [Data].self
            )
        else {
            return []
        }

        return dataArray.compactMap { try? JSONDecoder().decode(Self.self, from: $0) }
    }

    public static func retrieveFromStorage(with id: String) -> Self? {
        do {
            return try Storage.retrieve(
                storagePath(with: id),
                from: storageDirectory,
                as: Self.self
            )
        } catch {
            return nil
        }
    }

    public static func retrieveFromStorage(with ids: [String]) -> [Self] {
        return ids.compactMap { retrieveFromStorage(with: $0) }
    }

    public static func removeAll() {
        try? Storage.remove(folderPath, from: storageDirectory)
    }

    public static func remove(with id: String) {
        try? Storage.remove(storagePath(with: id), from: storageDirectory)
    }

    public static func expire(lastModifiedPriorTo timeInterval: TimeInterval) {
        try? Storage.expire(
            lastModifiedPriorTo: timeInterval,
            folderPath: folderPath,
            directory: storageDirectory
        )
    }

    @discardableResult
    public func createOrUpdate() -> Self? {
        do {
            try Storage.save(
                self,
                to: Self.storageDirectory,
                as: Self.storagePath(with: storageIdentifier)
            )
            return self
        } catch {
            return nil
        }
    }
}
