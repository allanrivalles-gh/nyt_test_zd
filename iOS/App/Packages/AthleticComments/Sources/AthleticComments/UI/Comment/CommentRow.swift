//
//  CommentRow.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticFoundation
import AthleticUI
import SwiftUI

public struct CommentRow: View {
    public enum Appearance: Equatable {
        case articlePreview
        case normal
        case discussionStaff(color: Color?)
    }

    @ObservedObject var viewModel: CommentViewModel
    @Environment(\.openURL) private var openURL

    let containerProxy: GeometryProxy
    let index: Int
    let surface: AnalyticsCommentSpecification.Surface
    let commentListViewModel: CommentListViewModel
    let focusedId: String?
    let lineLimit: Int?
    let tapAction: VoidClosure?
    let replyAction: VoidClosure?
    private let appearance: Appearance
    private let customAnalyticRecord: AnalyticsImpressionRecord?

    private var isDiscussionStaffComment: Bool {
        switch appearance {
        case .discussionStaff:
            return true
        default:
            return false
        }
    }

    public init(
        viewModel: CommentViewModel,
        containerProxy: GeometryProxy,
        index: Int,
        surface: AnalyticsCommentSpecification.Surface,
        commentListViewModel: CommentListViewModel,
        appearance: Appearance = .normal,
        focusedId: String? = nil,
        lineLimit: Int? = nil,
        tapAction: VoidClosure? = nil,
        replyAction: VoidClosure? = nil,
        analyticsImpressionRecord: AnalyticsImpressionRecord? = nil
    ) {
        self.viewModel = viewModel
        self.containerProxy = containerProxy
        self.index = index
        self.surface = surface
        self.commentListViewModel = commentListViewModel
        self.appearance = appearance
        self.focusedId = focusedId
        self.lineLimit = lineLimit
        self.tapAction = tapAction
        self.replyAction = replyAction
        self.customAnalyticRecord = analyticsImpressionRecord
    }

    public var body: some View {
        Group {
            if case .discussionStaff(let highlightColor) = appearance {
                HStack(spacing: 24) {

                    if viewModel.isReply {
                        Divider()
                    }

                    VStack(alignment: .leading, spacing: 4) {
                        commenterInfo
                        commentContent
                        reactionsBar
                            .padding(.top, 8)
                    }
                    .padding(.vertical, 12)
                    .padding(.horizontal, 16)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(highlightColor ?? Color.chalk.dark300)
                    .cornerRadius(4)
                }
                .foregroundColor(discussionStaffForegroundColor)

            } else {
                VStack(alignment: .leading, spacing: 12) {
                    HStack(spacing: 24) {
                        if viewModel.isReply {
                            Divider()
                        }

                        VStack(alignment: .leading, spacing: 4) {
                            commenterInfo
                            commentContent
                        }
                    }
                    reactionsBar
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(backgroundColor)
        .fixedSize(horizontal: false, vertical: true)
        .trackImpressions(
            with: AnalyticsManagers.commentImpressions,
            record: customAnalyticRecord ?? analyticRecord,
            containerProxy: containerProxy
        )
    }

    private var commenterInfo: some View {
        VStack(spacing: 0) {
            HStack(spacing: 8) {
                if viewModel.isPinned {
                    Image(systemName: "pin.fill")
                        .resizable()
                        .renderingMode(.template)
                        .foregroundColor(.chalk.dark800)
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 12, height: 12)
                        .rotationEffect(.degrees(45))
                }

                if viewModel.isStaff, let url = viewModel.authorAvatarImageUrl {
                    PlaceholderLazyImage(
                        imageUrl: url,
                        modifyImage: {
                            $0.aspectRatio(contentMode: .fill)
                        }
                    )
                    .frame(width: 18, height: 18)
                    .clipShape(Circle())
                } else {
                    CommentAvatar(
                        initial: viewModel.authorInitial,
                        backgroundColorHex: viewModel.authorAvatarColor,
                        size: 18
                    )
                }

                HStack(spacing: 4) {
                    Text(viewModel.author)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(
                            isDiscussionStaffComment
                                ? discussionStaffForegroundColor : .chalk.dark600
                        )

                    Text(viewModel.dateDisplayString)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(
                            isDiscussionStaffComment
                                ? discussionStaffForegroundColor : .chalk.dark500
                        )
                }

                if viewModel.isStaff {
                    CommentStaffLabel(
                        foregroundColor: staffBadgeForegroundColor,
                        backgroundColor: staffBadgeBackgroundColor
                    )
                }

                if !viewModel.authorGameFlairs.isEmpty {
                    HStack(spacing: 4) {
                        let _ = DuplicateIDLogger.logDuplicates(in: viewModel.authorGameFlairs)
                        ForEach(viewModel.authorGameFlairs) { flair in
                            CommentFlairLabel(flair: flair)
                        }
                    }
                }

                Spacer(minLength: 0)

                if let commentMetadata = viewModel.commentMetadata {
                    Text(commentMetadata)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(.chalk.dark500)
                }
            }
        }
    }

    private var commentContent: some View {
        CommentContent(
            content: viewModel.isFlagged
                ? Strings.flagComment.localized : viewModel.comment,
            foregroundColor: isDiscussionStaffComment
                ? discussionStaffForegroundColor : .chalk.dark700,
            lineLimit: lineLimit
        )
        .fixedSize(horizontal: false, vertical: true)
        .onTapGesture { tapAction?() }
    }

    private var reactionsBar: some View {
        HStack(spacing: 0) {
            Spacer()
            AnimatedLikeButton(
                isLiked: viewModel.isLiked,
                likesCount: viewModel.likesCount,
                textColor: isDiscussionStaffComment
                    ? discussionStaffForegroundColor : .chalk.dark700,
                action: { viewModel.handleLikeButtonAction(index: index, surface: surface) }
            )
            .disabled(viewModel.isLoading)
            .padding(.trailing, 12)
            Button(action: {
                commentListViewModel.selectedReplyComment = viewModel
                replyAction?()
            }) {
                Image("ic_reply")
                    .renderingMode(.template)
            }
            .padding(.horizontal, 12)

            Ellipsis()
                .padding(.leading, 12)
                .commentMenu(
                    commentListViewModel: commentListViewModel,
                    viewModel: viewModel
                )
        }
        .foregroundColor(
            isDiscussionStaffComment ? discussionStaffForegroundColor : .chalk.dark500
        )
    }

    private var analyticRecord: AnalyticsImpressionRecord {
        let view: AnalyticsEvent.View
        var parentObjectIdentifer: String? = nil
        var parentObjectType: AnalyticsEvent.ObjectType? = nil

        switch commentListViewModel.commentsType {
        case .post:
            view = .article
        case .headline:
            view = .headline
        case .podcastEpisode:
            view = .podcastEpisode
        case .gameV2:
            view = .boxScore
        case .discussion, .qanda, .postThread, .__unknown(_):
            view = .article
        case .gamePlay:
            view = .boxScore
        default:
            assertionFailure("Unhandled comments type: \(commentListViewModel.commentsType)")
            view = .comments
        }

        if let parentViewModel = viewModel.parentCommentViewModel {
            parentObjectIdentifer = parentViewModel.id
            parentObjectType = .commentId
        }

        if commentListViewModel.commentsType == .gameV2 {
            return AnalyticsImpressionRecord(
                verb: .impress,
                view: view,
                element: .comment,
                objectType: .commentId,
                objectIdentifier: viewModel.id,
                pageOrder: -1,
                parentObjectType: parentObjectType,
                parentObjectIdentifier: parentObjectIdentifer,
                filterType: "team_id",
                filterId: viewModel.legacyTeamId,
                indexH: viewModel.isReply ? 0 : 1,
                indexV: index,
                requiredValues: viewModel.analyticsDefaults
            )
        } else {
            return AnalyticsImpressionRecord(
                verb: .impress,
                view: view,
                element: .comment,
                objectType: .commentId,
                objectIdentifier: viewModel.id,
                pageOrder: -1,
                parentObjectType: parentObjectType,
                parentObjectIdentifier: parentObjectIdentifer,
                indexH: viewModel.isReply ? 0 : 1,
                indexV: index,
                requiredValues: viewModel.analyticsDefaults
            )
        }
    }
}

// MARK: - Color Manipulation
extension CommentRow {
    private var backgroundColor: Color {
        focusedId == viewModel.commentId
            ? Color.chalk.dark300
            : appearance == .articlePreview
                ? Color.chalk.dark200
                : Color.chalk.dark200
    }

    private var discussionStaffForegroundColor: Color {
        highContrastAppearance(for: .chalk.dark700)
    }

    private var staffBadgeForegroundColor: Color {
        guard case .discussionStaff(let highlightColor) = appearance, highlightColor != nil else {
            return .chalk.dark200
        }

        return Color.highContrastAppearance(
            of: .chalk.dark200,
            forBackgroundColor: staffBadgeBackgroundColor
        )
    }

    private var staffBadgeBackgroundColor: Color {
        highContrastAppearance(for: .chalk.dark800)
    }

    private func highContrastAppearance(for color: UIColor) -> Color {
        guard case .discussionStaff(let highlightColor) = appearance,
            let highlightColor = highlightColor
        else {
            return Color(color)
        }

        return Color.highContrastAppearance(of: color, forBackgroundColor: highlightColor)
    }
}

struct CommentRow_Previews: PreviewProvider {
    static var previewViewModels: [CommentViewModel] = [
        CommentPreviewHelper.commentModel,
        CommentPreviewHelper.replyModel,
    ]

    static var previews: some View {
        NavigationStack {
            GeometryReader { proxy in
                VStack {
                    let _ = DuplicateIDLogger.logDuplicates(in: previewViewModels)
                    ForEach(previewViewModels) { viewModel in
                        CommentRow(
                            viewModel: viewModel,
                            containerProxy: proxy,
                            index: 0,
                            surface: .init(.article(id: "0")),
                            commentListViewModel: CommentList_Previews.previewModel,
                            focusedId: nil
                        )
                    }
                }
                .padding()
                .preferredColorScheme(.light)
            }
            .navigationTitle("Example Comments")
            .navigationBarTitleDisplayMode(.inline)
        }
        .loadCustomFonts()
    }
}
