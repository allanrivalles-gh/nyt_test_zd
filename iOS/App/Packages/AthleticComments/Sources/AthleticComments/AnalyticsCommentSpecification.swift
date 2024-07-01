//
//  CommentsLoggingSpec.swift
//
//
//  Created by Leonardo da Silva on 27/02/23.
//

import AthleticAnalytics
import AthleticApolloTypes

/// This struct contains all the events for the Comments Section Logging Spec
/// and contains comments list and comment row events related to the Game Threads Logging Spec
/// References:
/// - [Logging Spec] Comments Section
/// https://docs.google.com/spreadsheets/d/1o2IrPg9M-os2JuPXbXopAlE8MvvP1HKPx61vDvPhLRI
/// - Logging Spec: Game Threads
/// https://docs.google.com/spreadsheets/d/1uJI54OIaGl13LsUBbMbarQ_mGEaBNKqHtqw4lCk8Eqo
public struct AnalyticsCommentSpecification {
    // MARK: Content related

    /// Click Comments Icon
    public static func onClickCommentsIcon(
        surface: Surface,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.contentView,
                element: .commentIcon,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                requiredValues: requiredValues
            )
        )
    }

    /// Click 'View More Comments'
    public static func onClickViewMoreComments(
        surface: Surface,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.contentView,
                element: .viewMoreComments,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                requiredValues: requiredValues
            )
        )
    }

    /// View Comment Preview
    public static func onViewCommentPreview(
        surface: Surface,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .view,
                /// this method does not inherit the view from the surface
                view: .commentsPreview,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                requiredValues: requiredValues
            )
        )
    }

    /// Comment  dismissal
    public static func onCommentDismissal(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: Surface,
        requiredValues: AnalyticsRequiredValues
    ) async {
        await Analytics.track(
            event: .init(
                verb: .click,
                view: .commentsDrawer,
                element: .close,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                requiredValues: requiredValues
            ),
            manager: manager
        )
    }

    /// Comment dismissal undo
    public static func onCommentDismissalUndo(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: Surface,
        requiredValues: AnalyticsRequiredValues
    ) async {
        await Analytics.track(
            event: .init(
                verb: .click,
                view: .commentsDrawer,
                element: .recover,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                requiredValues: requiredValues
            ),
            manager: manager
        )
    }

    // MARK: List related

    /// Switch threads
    public static func onTeamThreadSwitch(
        surface: Surface,
        currentTeamId: String?,
        clickedTeamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .teamSpace,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                metaBlob: .init(
                    currentTeamId: currentTeamId,
                    clickedTeamId: clickedTeamId,
                    requiredValues: requiredValues
                ),
                requiredValues: requiredValues
            )
        )
    }

    /// View All Comments
    public static func onViewAllComments(
        surface: Surface,
        commentsViewLinkId: String?,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .view,
                view: surface.commentsView,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                metaBlob: surface.metaBlob(
                    identifyContent: false,
                    requiredValues: requiredValues,
                    commentViewLinkId: commentsViewLinkId,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    /// Refresh Comments
    public static func onRefreshComments(
        surface: Surface,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .refresh,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                metaBlob: surface.metaBlob(
                    identifyContent: false,
                    requiredValues: requiredValues,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    /// Timer
    public static func onSecondsPassed(
        manager: AnalyticEventManager = AnalyticsManagers.events,
        surface: Surface,
        commentViewLinkId: String,
        seconds: Int,
        requiredValues: AnalyticsRequiredValues
    ) async {
        await Analytics.track(
            event: .init(
                verb: .heartbeat,
                view: surface.commentsView,
                element: .seconds,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                metaBlob: .init(
                    teamId: nil,
                    commentViewLinkId: commentViewLinkId,
                    seconds: seconds,
                    requiredValues: requiredValues
                ),
                requiredValues: requiredValues
            ),
            manager: manager
        )
    }

    /// Sort Comments
    public static func onSortComments(
        sortBy: GQL.CommentSortBy,
        surface: Surface,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .sort,
                objectType: sortBy.analyticsObjectType,
                metaBlob: surface.metaBlob(
                    requiredValues: requiredValues,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    /// Click to top of Comments
    public static func onClickToTopOfComments(
        surface: Surface,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .clickToTop,
                objectType: surface.objectType,
                objectIdentifier: surface.objectIdentifier,
                metaBlob: surface.metaBlob(
                    identifyContent: false,
                    requiredValues: requiredValues,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    // MARK: Row related

    /// Like a Comment
    public static func onLikeComment(
        commentId: String,
        vIndex: Int,
        surface: Surface,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .like,
                objectType: .commentId,
                objectIdentifier: commentId,
                metaBlob: surface.metaBlob(
                    vIndex: vIndex,
                    requiredValues: requiredValues,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    /// Unlike a Comment
    public static func onUnlikeComment(
        commentId: String,
        vIndex: Int,
        surface: Surface,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .unlike,
                objectType: .commentId,
                objectIdentifier: commentId,
                metaBlob: surface.metaBlob(
                    vIndex: vIndex,
                    requiredValues: requiredValues,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    /// Flag a Comment
    public static func onFlagComment(
        commentId: String,
        vIndex: Int,
        surface: Surface,
        teamId: String?,
        requiredValues: AnalyticsRequiredValues
    ) {
        Analytics.track(
            event: .init(
                verb: .click,
                view: surface.commentsView,
                element: .flag,
                objectType: .commentId,
                objectIdentifier: commentId,
                metaBlob: surface.metaBlob(
                    vIndex: vIndex,
                    requiredValues: requiredValues,
                    teamId: teamId
                ),
                requiredValues: requiredValues
            )
        )
    }

    public struct Surface {
        let type: SurfaceType
        let filterType: FilterType?

        public init(_ type: SurfaceType, filterType: FilterType? = .all) {
            self.type = type
            self.filterType = filterType
        }

        fileprivate var commentsView: AnalyticsEvent.View {
            switch type {
            case .game(_, _, let view): return view
            default: return .comments
            }
        }

        fileprivate var contentView: AnalyticsEvent.View {
            switch type {
            case .article: return .article
            case .headline: return .headline
            case .podcastEpisode: return .podcastEpisode
            case .game(_, _, let view): return view
            }
        }

        fileprivate var objectType: AnalyticsEvent.ObjectType {
            switch type {
            case .article: return .articleId
            case .headline: return .headlineId
            case .podcastEpisode: return .podcastEpisodeId
            case .game: return .gameId
            }
        }

        fileprivate var objectIdentifier: String {
            switch type {
            case .article(let id): return id
            case .headline(let id): return id
            case .podcastEpisode(let id): return id
            case .game(let id, _, _): return id
            }
        }

        fileprivate func metaBlob(
            vIndex: Int? = nil,
            /// we don't want to identify content in the metaBlob if it has already been identified
            /// in `objectType` and `objectIdentifier`
            identifyContent: Bool = true,
            requiredValues: AnalyticsRequiredValues,
            commentViewLinkId: String? = nil,
            teamId: String? = nil
        ) -> AnalyticsEvent.MetaBlob {
            var articleId: String?
            var headlineId: String?
            var podcastEpisodeId: String?
            var gameId: String?
            var leagueId: String?
            if identifyContent {
                switch type {
                case .article(let id): articleId = id
                case .headline(let id): headlineId = id
                case .podcastEpisode(let id): podcastEpisodeId = id
                case .game(let id, _, _): gameId = id
                }
            }
            switch type {
            case .game(_, let leagueCode, _):
                leagueId = leagueCode.rawValue
            default: break
            }

            var filterType = self.filterType
            /// we don't need filter type when it is not relative to a comment row (which has `vIndex` set)
            if vIndex == nil { filterType = nil }
            return AnalyticsEvent.MetaBlob(
                indexV: vIndex,
                articleId: articleId,
                leagueId: leagueId,
                gameId: gameId,
                teamId: teamId,
                headlineId: headlineId,
                podcastEpisodeId: podcastEpisodeId,
                filterType: filterType?.rawValue,
                commentViewLinkId: commentViewLinkId,
                requiredValues: requiredValues
            )
        }
    }

    public enum SurfaceType {
        case article(id: String)
        case headline(id: String)
        case podcastEpisode(id: String)
        /// for games we have different views depending on the game phase
        /// ideally we would have the game phase here and then decide from that
        /// but the game phase type is not available in this context
        case game(id: String, leagueCode: GQL.LeagueCode, view: AnalyticsEvent.View)
    }

    public enum FilterType: String {
        case all
        case preview
        case topCommentModule = "top_comment_module"
    }
}

extension GQL.CommentSortBy {
    fileprivate var analyticsObjectType: AnalyticsEvent.ObjectType {
        switch self {
        case .likes:
            return .mostLiked
        case .recent:
            return .newest
        case .time:
            return .oldest
        case .trending:
            return .trending
        case .__unknown(let rawValue):
            assertionFailure("New Sort By case not handled \(rawValue)")
            return .unknown
        }
    }
}
