//
//  CommentList.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/16/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloNetworking
import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import Combine
import SwiftUI

public struct CommentList<Header: View>: View {

    @Environment(\.commentingPlay) @Binding private var commentingPlay
    @Environment(\.focusedComment) @Binding private var focusedComment
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.dynamicTypeSize) var sizeCategory
    @Environment(\.scenePhase) var scenePhase
    @Environment(\.commentDrawerDismissed) private var drawerDismissed

    @ObservedObject public var viewModel: CommentListViewModel

    @State private var inputText = ""
    @State private var sending: Bool = false
    @State private var isVisible = false
    @FocusState private var isCommentDrawerFocus: Bool
    @State private var isShowingTemporaryBan: Bool = false
    @State private var isShowingThreadSwitcher: Bool = false

    private let surface: AnalyticsCommentSpecification.Surface
    private let contrastingBackgroundInLightMode: Bool
    private let header: (GeometryProxy) -> Header

    private var backgroundColor: Color {
        colorScheme == .light && contrastingBackgroundInLightMode
            ? .chalk.dark200
            : .chalk.dark100
    }

    public init(
        viewModel: CommentListViewModel,
        surface: AnalyticsCommentSpecification.Surface,
        contrastingBackgroundInLightMode: Bool = true,
        @ViewBuilder header: @escaping (GeometryProxy) -> Header
    ) {
        self.viewModel = viewModel
        self.surface = surface
        self.contrastingBackgroundInLightMode = contrastingBackgroundInLightMode
        self.header = header
    }

    public var body: some View {
        GeometryReader { containerProxy in
            VStack(spacing: 0) {
                if let teamThreadList = viewModel.teamThreadList {
                    TeamSpecificThreadBanner(
                        viewModel: teamThreadList.currentThread,
                        showChangeButton: teamThreadList.teamThreads.count > 1,
                        isShowingThreadSwitcher: $isShowingThreadSwitcher
                    )
                    .sheet(isPresented: $isShowingThreadSwitcher) {
                        TeamSpecificThreadSwitcher(
                            currentThread: teamThreadList.currentThread,
                            otherThread: teamThreadList.otherThread,
                            isShowingThreadSwticher: $isShowingThreadSwitcher,
                            surface: surface,
                            switchAction: viewModel.updateThread
                        )
                        .padding(.top, 36)
                        .padding([.bottom, .horizontal], 16)
                        .presentationDetents(
                            sizeCategory.isAccessibilitySize ? [.medium] : [.height(296)]
                        )
                    }
                }

                ListContent(
                    viewModel: viewModel,
                    inputText: $inputText,
                    isCommentDrawerFocus: $isCommentDrawerFocus,
                    isShowingTemporaryBan: $isShowingTemporaryBan,
                    containerProxy: containerProxy,
                    header: header,
                    surface: surface
                )
                .background(backgroundColor)
                .commentInteractionToast(
                    commentInteractionError: $viewModel.commentInteractionError
                )
                .commentUndoToast(
                    dismissedComment: $viewModel.dismissedComment,
                    text: $inputText,
                    isCommentDrawerFocus: $isCommentDrawerFocus
                ) {
                    Task {
                        await viewModel.undoDismissCommentAnalytics(surface: surface)
                    }

                    return viewModel.undoDismissComment()
                }

                CommentReplyView(
                    viewModel: viewModel,
                    text: $inputText,
                    sending: $sending,
                    isCommentDrawerFocus: $isCommentDrawerFocus,
                    isShowingTemporaryBan: $isShowingTemporaryBan,
                    surface: surface
                )
                .frame(maxHeight: containerProxy.size.height)
                .fixedSize(horizontal: false, vertical: true)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.chalk.dark200.edgesIgnoringSafeArea(.all))
        }
        .toolbar {
            ToolbarItem(placement: .principal) {
                NavigationBarTitleText(viewModel.toolbarTitle)
            }
        }
        .overlay(
            Group {
                if viewModel.state == .failed {
                    ProgressView(Strings.genericError.localized)
                        .progressViewStyle(.athleticFailed)
                        .onTapGesture {
                            viewModel.state = .loaded
                        }
                } else {
                    EmptyContent(
                        state: viewModel.state,
                        backgroundColor: .clear,
                        errorMessage: Strings.genericError.localized,
                        showProgressViewOnLoading:
                            viewModel.state == .loading(showPlaceholders: true)
                    )
                }
            }
        )
        .aboveSafeAreaColor(backgroundColor)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            isVisible = true
            viewModel.trackViewEvents(surface: surface)
            onCommentingPlay(commentingPlay)
            onFocusedComment(focusedComment)

            guard !viewModel.state.isLoading else {
                return
            }

            Task {
                await viewModel.fetchData(isInitialLoad: viewModel.comments.isEmpty)
            }
        }
        .onDisappear {
            isVisible = false
            /// Stop timer for analytics
            viewModel.stopTrackingSeconds()
        }
        .onChange(of: commentingPlay, perform: onCommentingPlay)
        .onReceive(drawerDismissed) { isCommentDrawerFocus = false }
        .onChange(of: scenePhase) { newPhase in
            /// This callback gets triggered even when the Discuss tab is not selected on game hub.
            /// That is why we need to make sure it is visible, which means the tab is selected.
            guard isVisible else { return }

            switch newPhase {
            case .active:
                /// When scene becomes active we want to send view appear and restart the timer
                viewModel.trackViewEvents(surface: surface)
            case .inactive:
                /// When scene becomes inactive we need to stop timer
                viewModel.stopTrackingSeconds()
            default:
                break
            }
        }
        .onReceive(viewModel.$commentingPlay.dropFirst()) { newValue in
            commentingPlay = newValue
        }
    }

    private func onFocusedComment(_ focusedComment: FocusedComment?) {
        inputText = ""
        if let focusedComment = focusedComment {
            if focusedComment.isReply {
                viewModel.selectedReplyComment = focusedComment.comment
                viewModel.focusCommentAfterNextFetch(id: focusedComment.comment.id)
                DispatchQueue.main.async {
                    isCommentDrawerFocus = true
                }
            } else {
                viewModel.focusedComment = focusedComment.comment
                viewModel.focusCommentAfterNextFetch(id: focusedComment.comment.id)
            }
        }
    }

    private func onCommentingPlay(_ commentingPlay: CommentingPlay?) {
        if let play = commentingPlay {
            viewModel.commentingPlay = play
            DispatchQueue.main.async {
                isCommentDrawerFocus = true
            }
        } else {
            viewModel.commentingPlay = nil
            isCommentDrawerFocus = false
        }
    }
}

extension CommentList where Header == EmptyView {
    public init(
        viewModel: CommentListViewModel,
        surface: AnalyticsCommentSpecification.Surface,
        contrastingBackgroundInLightMode: Bool = true
    ) {
        self.init(
            viewModel: viewModel,
            surface: surface,
            contrastingBackgroundInLightMode: contrastingBackgroundInLightMode,
            header: { _ in EmptyView() }
        )
    }
}

private struct ListContent<Header: View>: View {
    @ObservedObject var viewModel: CommentListViewModel

    @Binding var inputText: String
    var isCommentDrawerFocus: FocusState<Bool>.Binding
    @Binding var isShowingTemporaryBan: Bool

    @State private var shouldShowBackToTopButton: Bool = false
    @State private var highlightedCommentId: String?

    private let scrollToTop = PassthroughSubject<Void, Never>()
    private let topId = UUID().uuidString

    let containerProxy: GeometryProxy
    let header: (GeometryProxy) -> Header
    let surface: AnalyticsCommentSpecification.Surface

    var body: some View {
        RefreshableScrollView(
            .vertical,
            showsIndicators: true,
            trackOffset: { offset in
                shouldShowBackToTopButton = !viewModel.comments.isEmpty && floor(offset.y) > 200
                isShowingTemporaryBan = false
            }
        ) {
            await viewModel.didPullToRefresh(surface: surface)
        } content: {
            ScrollViewReader { scrollProxy in
                header(containerProxy)

                EmptyView()
                    .id(topId)

                VStack(alignment: .leading, spacing: 0) {
                    if viewModel.isDiscussion {
                        if let headerViewModel = viewModel.discussionHeaderViewModel {
                            DiscussionHeaderView(viewModel: headerViewModel)
                        }
                    } else if !viewModel.hidesTitle {
                        Text(viewModel.title)
                            .lineSpacing(1)
                            .fontStyle(.tiemposHeadline.m.regular)
                            .foregroundColor(.chalk.dark700)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(16)
                    } else {
                        Spacer(minLength: 8)
                    }
                    Menu {
                        let _ = DuplicateIDLogger.logDuplicates(in: GQL.CommentSortBy.allCases)
                        ForEach(GQL.CommentSortBy.allCases) { sortBy in
                            Button(sortBy.title) {
                                viewModel.sortBy = sortBy

                                viewModel.trackSort(by: sortBy, surface: surface)

                                Task {
                                    await viewModel.fetchData()
                                }
                            }
                        }
                    } label: {
                        HStack(spacing: 0) {
                            MenuLabel(title: viewModel.sortBy.title)
                            Spacer()

                            Text(viewModel.commentCountText)
                                .fontStyle(.calibreUtility.l.medium)
                        }
                        .foregroundColor(.chalk.dark700)
                        .opacity(viewModel.isInitialLoad ? 0 : 1)
                    }
                    .padding(.horizontal, 16)
                    .frame(height: 50)
                    .background(Color.chalk.dark100)

                    if viewModel.comments.isEmpty && viewModel.state == .loaded {
                        NoCommentsView()
                            .padding(.vertical, 16)
                    }

                    LazyVStack(alignment: .leading, spacing: 0) {
                        ForEach(indexed: viewModel.comments, id: \.commentId) { index, comment in
                            VStack(spacing: 0) {
                                if !comment.isReply, index != 0 {
                                    DividerView(style: .large, color: .chalk.dark100)
                                }

                                if let headerViewModel = viewModel.discussionHeaderViewModel,
                                    comment.isStaff
                                {
                                    CommentRow(
                                        viewModel: comment,
                                        containerProxy: containerProxy,
                                        index: index,
                                        surface: surface,
                                        commentListViewModel: viewModel,
                                        appearance: .discussionStaff(
                                            color: headerViewModel.highlightColor
                                        ),
                                        focusedId: viewModel.focusedComment?.commentId
                                    )
                                } else {
                                    CommentRow(
                                        viewModel: comment,
                                        containerProxy: containerProxy,
                                        index: index,
                                        surface: surface,
                                        commentListViewModel: viewModel,
                                        appearance: .normal,
                                        focusedId: viewModel.focusedComment?.commentId
                                    )
                                }
                            }
                        }
                    }
                    .opacity(viewModel.isInitialLoad ? 0 : 1)
                    /// padding for when keyboard is active
                    .padding(.bottom, 80)
                    .animation(.easeInOut, value: viewModel.comments)
                    .onChange(of: viewModel.focusedComment) { newValue in
                        guard let commentId = newValue?.commentId else { return }

                        highlightedCommentId = commentId
                        DispatchQueue.main.async {
                            withAnimation {
                                scrollProxy.scrollTo(commentId, anchor: .top)
                            }
                        }
                    }
                    .onChange(of: viewModel.selectedReplyComment) { selectedReplyComment in
                        if let selectedReplyComment = selectedReplyComment {
                            viewModel.editingComment = nil
                            viewModel.focusedComment = selectedReplyComment
                            let replyPrefix = CommentListViewModel.ReplyCommentPrefix.forAuthor(
                                selectedReplyComment.author
                            )

                            /// For auto generated comments from The Athletic, we don't want handle such as @authorName
                            /// Therefore set the input text to empty
                            if selectedReplyComment.isFromTheAthletic {
                                inputText = ""
                            } else {
                                inputText =
                                    inputText.starts(with: replyPrefix)
                                    ? inputText
                                    : replyPrefix
                            }

                            /// wait to present keyboard after autoscroll to comment
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                isCommentDrawerFocus.wrappedValue = true
                            }
                        } else {
                            highlightedCommentId = nil
                            inputText = ""
                            isCommentDrawerFocus.wrappedValue = false
                        }
                    }
                    .onChange(of: viewModel.editingComment) { editingComment in
                        if let editingComment = editingComment {
                            viewModel.selectedReplyComment = nil
                            viewModel.focusedComment = editingComment
                            inputText = editingComment.comment
                            /// wait to present keyboard after autoscroll to comment
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                                isCommentDrawerFocus.wrappedValue = true
                            }
                        } else {
                            highlightedCommentId = nil
                            inputText = ""
                            isCommentDrawerFocus.wrappedValue = false
                        }
                    }
                }
                .padding(.vertical, 8)
                .onReceive(scrollToTop) {
                    withAnimation {
                        scrollProxy.scrollTo(topId, anchor: .top)
                    }
                }
                .commentAlertSheets(
                    commentListViewModel: viewModel,
                    surface: surface
                )
            }
        }
        .scrollDismissesKeyboard(.interactively)
        .onSimultaneousTapGesture {
            isCommentDrawerFocus.wrappedValue = false
        }
        .overlay(alignment: .bottomTrailing) {
            ReturnToTopButton {
                viewModel.trackClickToTop(surface: surface)

                scrollToTop.send()
            }
            .opacity(shouldShowBackToTopButton ? 1 : 0)
            .animation(.easeInOut(duration: 0.2), value: shouldShowBackToTopButton)
            .padding(.bottom, 32)
            .padding(.trailing, 24)
        }
    }
}

struct CommentList_Previews: PreviewProvider {
    static var previewModel: CommentListViewModel = CommentListViewModel(
        id: "3704791",
        title:
            "Signing day around Texas: Why 5-star Denver Harris is waiting, Steve Sarkisian and the Horns are doing flips",
        commentsType: .post,
        comments: CommentRow_Previews.previewViewModels,
        isCommentLikedProvider: nil,
        additionalLikeTapAction: nil,
        isCommentFlaggedProvider: nil,
        additionalFlagAction: nil,
        areTeamSpecificThreadsEnabled: false,
        shouldHideTitleForComments: true,
        network: CommentPreviewHelper.network,
        userId: "124",
        analyticsDefaults: PreviewAnalyticDefaults()
    )

    static var qandA: CommentListViewModel = CommentListViewModel(
        id: "3704791",
        title:
            "Signing day around Texas: Why 5-star Denver Harris is waiting, Steve Sarkisian and the Horns are doing flips",
        commentsType: .qanda,
        comments: CommentRow_Previews.previewViewModels,
        isCommentLikedProvider: nil,
        additionalLikeTapAction: nil,
        isCommentFlaggedProvider: nil,
        additionalFlagAction: nil,
        areTeamSpecificThreadsEnabled: false,
        shouldHideTitleForComments: true,
        network: CommentPreviewHelper.network,
        userId: "124",
        analyticsDefaults: PreviewAnalyticDefaults()
    )

    static var previews: some View {
        Group {
            NavigationStack {
                CommentList(
                    viewModel: previewModel,
                    surface: .init(.article(id: "0"))
                )
            }
            .preferredColorScheme(.dark)
            .previewDisplayName("Dark Normal")
            NavigationStack {
                CommentList(
                    viewModel: previewModel,
                    surface: .init(.article(id: "0"))
                )
            }
            .preferredColorScheme(.light)
            .previewDisplayName("Light Normal")

            NavigationStack {
                CommentList(
                    viewModel: qandA,
                    surface: .init(.article(id: "0"))
                )
            }
            .preferredColorScheme(.dark)
            .previewDisplayName("Dark QA")
            NavigationStack {
                CommentList(
                    viewModel: qandA,
                    surface: .init(.article(id: "0"))
                )
            }
            .preferredColorScheme(.light)
            .previewDisplayName("Light QA")
        }
        .loadCustomFonts()
    }
}
