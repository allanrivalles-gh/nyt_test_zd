//
//  NavigationRestorationModel.swift
//
//
//  Created by kevin fremgen on 8/31/23.
//

import AthleticFoundation
import Foundation

public struct NavigationRestorationModel: Codable {
    public enum RestorationType: Codable {
        case article(id: String)
        case game(destination: BoxScoreDestination)
        case liveBlog(liveblogId: String)
        case entity(entity: FollowingEntity)
        case podcast(episode: PodcastEpisode)
    }

    public let type: RestorationType
    public let time: Date

    public init(type: RestorationType, time: Date = Date()) {
        self.type = type
        self.time = time
    }

    public var screen: AthleticScreen? {
        switch type {

        case .article(let id):
            return .feed(.article(.detail(id: id, commentId: nil)))
        case .game(let destination):
            return .scores(.boxScore(destination))
        case .liveBlog(let id):
            return .liveBlog(liveBlogId: id, postId: nil)
        case .entity(let entity):
            return .hubDetails(entity: entity, preferredTab: nil)
        case .podcast(let episode):
            return .listen(.podcastEpisode(episode, focusedCommentId: nil))
        }
    }
}
