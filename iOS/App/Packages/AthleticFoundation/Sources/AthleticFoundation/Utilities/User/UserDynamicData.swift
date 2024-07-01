//
//  UserDynamicData.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 09/03/2020
//  Copyright © 2020 The Athletic. All rights reserved.
//

import AthleticStorage
import Combine
import Foundation

public struct UserDynamicData {
    public static var lastUpdated = Date.distantPast
    public static let article = Article()
    public static let comment = Comment()
    public static let commentCounts = CommentCounts()

    public class CommentCounts {

        private let queue = DispatchQueue(
            label: "UserDynamicData.commentCounts.queue",
            attributes: .concurrent
        )

        private struct CommentCountRecord: Hashable {
            let id: String
            let contentType: String
            let count: Int

            func hash(into hasher: inout Hasher) {
                hasher.combine(id)
                hasher.combine(contentType)
            }

            static func == (lhs: CommentCountRecord, rhs: CommentCountRecord) -> Bool {
                lhs.id == rhs.id
                    && lhs.contentType == rhs.contentType
            }
        }

        private var countsCache: Set<CommentCountRecord> = Set()

        public func count(forId id: String, contentType: String) -> Int? {
            queue.sync {
                return countsCache.first(where: { $0.id == id && $0.contentType == contentType })?
                    .count
            }
        }

        @discardableResult
        public func updateCount(id: String, contentType: String, value: Int) -> Int? {
            return queue.sync(flags: .barrier) {
                countsCache.update(
                    with: CommentCountRecord(id: id, contentType: contentType, count: value)
                )?.count
            }
        }
    }

    public class Article {
        private let queue = DispatchQueue(
            label: "UserDynamicData.article.queue",
            attributes: .concurrent
        )

        public var readCount: Int {
            queue.sync(flags: .barrier) {
                readCache.count
            }
        }

        private var readCache: Set<Int> = Set()
        private var ratedCache: Set<Int> = Set()
        private var savedCache: Set<Int> = Set()

        public func read(for id: Int, value: Bool) {
            queue.sync(flags: .barrier) {
                if !value {
                    readCache.remove(id)
                } else {
                    readCache.insert(id)
                }
            }
        }

        public func isRead(for id: Int) -> Bool {
            queue.sync {
                readCache.contains(id)
            }
        }

        public func save(for id: Int, value: Bool) {
            queue.sync(flags: .barrier) {
                if !value {
                    savedCache.remove(id)
                } else {
                    savedCache.insert(id)
                }
            }
        }

        public func isSaved(for id: Int) -> Bool {
            queue.sync {
                savedCache.contains(id)
            }
        }

        public func rate(for id: Int, value: Bool) {
            queue.sync(flags: .barrier) {
                if !value {
                    ratedCache.remove(id)
                } else {
                    ratedCache.insert(id)
                }
            }
        }

        public func isRated(for id: Int) -> Bool {
            queue.sync {
                ratedCache.contains(id)
            }
        }
    }

    public class Comment {
        private let queue = DispatchQueue(
            label: "UserDynamicData.comment.queue",
            attributes: .concurrent
        )
        private var likeCache: Set<Int> = Set()
        private var flagCache: Set<Int> = Set()

        public func like(for id: Int, value: Bool) {
            queue.sync(flags: .barrier) {
                if !value {
                    likeCache.remove(id)
                } else {
                    likeCache.insert(id)
                }
            }
        }

        public func isLiked(for id: Int) -> Bool {
            queue.sync {
                likeCache.contains(id)
            }
        }

        public func flag(for id: Int, value: Bool) {
            queue.sync(flags: .barrier) {
                if !value {
                    flagCache.remove(id)
                } else {
                    flagCache.insert(id)
                }
            }
        }

        public func isFlagged(for id: Int) -> Bool {
            queue.sync {
                flagCache.contains(id)
            }
        }

        /// Creates a signature that will match the call site with a more verbose name
        public func likeTapAction(id: String, isLiked: Bool) {
            like(for: id.intValue, value: isLiked)
        }

        /// Creates a signature that will match the call site with a more verbose name
        public func flagAction(id: String, isFlagged: Bool) {
            flag(for: id.intValue, value: isFlagged)
        }
    }
}

public protocol ArticleDynamicDataProviding {
    var articleId: String { get }
    var isRead: Bool { get }
    var isSaved: Bool { get }
    var isRated: Bool { get }
}

extension ArticleDynamicDataProviding {
    public var isRead: Bool {
        UserDynamicData.article.isRead(for: articleId.intValue)
    }

    public var isRated: Bool {
        UserDynamicData.article.isRated(for: articleId.intValue)
    }

    public var isSaved: Bool {
        UserDynamicData.article.isSaved(for: articleId.intValue)
    }

    public func read(_ value: Bool) {
        UserDynamicData.article.read(for: articleId.intValue, value: value)
    }

    public func save(_ value: Bool) {
        UserDynamicData.article.save(for: articleId.intValue, value: value)
    }

    public func rate(_ value: Bool) {
        UserDynamicData.article.rate(for: articleId.intValue, value: value)
    }

    public func toggleRead() {
        read(!isRead)
    }

    public func toggleSaved() {
        save(!isSaved)
    }

    public func toggleRated() {
        rate(!isRated)
    }
}
