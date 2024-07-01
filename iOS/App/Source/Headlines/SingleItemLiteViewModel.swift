//
//  SingleItemLiteViewModel.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 5/20/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticNavigation
import Foundation

final class SingleItemLiteViewModel {

    enum ItemType: Equatable {
        case headline(identifier: String, title: String, tagTitle: String?)
        case article(identifier: String, title: String)
        case qanda(identifier: String, title: String)
        case discussion(identifier: String, title: String)
        case liveBlog(
            identifier: String,
            title: String,
            permalink: String,
            permalinkForEmbed: String,
            gameId: String?
        )

        var identifier: String {
            switch self {
            case .headline(let id, _, _),
                .article(let id, _),
                .qanda(let id, _),
                .discussion(let id, _),
                .liveBlog(let id, _, _, _, _):
                return id
            }
        }

        var title: String {
            switch self {
            case .headline(_, let titleString, _),
                .article(_, let titleString),
                .qanda(_, let titleString),
                .discussion(_, let titleString),
                .liveBlog(_, let titleString, _, _, _):
                return titleString
            }
        }

        var tagTitle: String? {
            switch self {
            case .headline(_, _, let tagString):
                return tagString
            default:
                return nil
            }
        }

        var commentsContentType: GQL.ContentType? {
            switch self {
            case .headline:
                return .post
            case .article:
                return .post
            case .discussion:
                return .discussion
            case .qanda:
                return .qanda
            case .liveBlog:
                return nil
            }
        }
    }

    // MARK: - Properties

    let title: String
    let identifier: String
    let tagTitle: String?
    let type: ItemType
    let isGroupedTabletDisplay: Bool

    private let analyticsConfiguration: FeedSectionAnalyticsConfiguration
    private let analyticsEventTypes: Set<AnalyticData.EventType>

    var shouldShowTag: Bool {
        tagTitle != nil
    }

    var impressionManager: AnalyticImpressionManager {
        analyticsConfiguration.impressionManager
    }

    var navigationDestination: AthleticScreen {
        if case .liveBlog(let id, _, _, _, let gameId) = type {
            return .liveBlog(id: id, gameId: gameId)
        } else {
            return .feed(.article(.detail(id: type.identifier, commentId: nil)))
        }
    }

    // MARK: - Initialization

    init(
        type: ItemType,
        isGroupedTabletDisplay: Bool = false,
        analytics: FeedSectionAnalyticsConfiguration,
        eventTypes: Set<AnalyticData.EventType> = [.click, .impress, .view]
    ) {
        self.title = type.title
        self.identifier = type.identifier
        self.tagTitle = type.tagTitle
        self.type = type
        self.isGroupedTabletDisplay = isGroupedTabletDisplay

        analyticsEventTypes = eventTypes
        analyticsConfiguration = analytics
    }
}

// MARK: - Hashable
extension SingleItemLiteViewModel: Hashable {
    func hash(into hasher: inout Hasher) {
        hasher.combine(title)
        hasher.combine(identifier)
    }

    static func == (lhs: SingleItemLiteViewModel, rhs: SingleItemLiteViewModel) -> Bool {
        lhs.title == rhs.title && lhs.identifier == rhs.identifier
    }
}

// MARK: - Identifiable
extension SingleItemLiteViewModel: Identifiable {
    var id: Int {
        hashValue
    }
}

// MARK: - Analytical
extension SingleItemLiteViewModel: Analytical {
    var analyticData: AnalyticData {
        AnalyticData(
            config: analyticsConfiguration,
            objectIdentifier: identifier,
            eventTypes: analyticsEventTypes,
            indexVOverride: analyticsConfiguration.indexPath.item
        )
    }
}
