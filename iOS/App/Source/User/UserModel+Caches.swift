//
//  UserModel+Caches.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/1/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticStorage
import Foundation
import Nuke

extension UserModel {
    public func clearCaches() {
        network.apolloClient.clearCache(callbackQueue: .main, completion: nil)
        clearCacheDirectory()
        URLCache.shared.removeAllCachedResponses()
        UserDynamicData.lastUpdated = Date.distantPast
        ImageCache.shared.removeAll()
        ImagePipeline.shared.cache.removeAll()
    }

    private func clearCacheDirectory() {
        let fileManager = FileManager.default

        guard let cacheUrl = fileManager.urls(for: .cachesDirectory, in: .userDomainMask).first,
            let directoryContents = try? fileManager.contentsOfDirectory(
                at: cacheUrl,
                includingPropertiesForKeys: nil,
                options: []
            )
        else { return }

        for file in directoryContents {
            try? fileManager.removeItem(at: file)
        }
    }
}
