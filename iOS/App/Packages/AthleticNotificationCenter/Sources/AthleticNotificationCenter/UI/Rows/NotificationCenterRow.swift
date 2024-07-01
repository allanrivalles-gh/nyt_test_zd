//
//  NotificationCenterRow.swift
//
//
//  Created by Jason Leyrer on 7/6/23.
//

import AthleticNavigation
import AthleticUI
import SwiftUI

struct NotificationCenterRow: View {

    @EnvironmentObject private var navigationModel: NavigationModel
    @Environment(\.colorScheme) private var colorScheme
    @ObservedObject var viewModel: NotificationCenterRowViewModel

    private let imageDimension: CGFloat = 58

    private var primaryTextColor: Color {
        if viewModel.isCommentActivity {
            return viewModel.shouldShowUnreadNotificationState ? .chalk.dark800 : .chalk.dark600
        } else if viewModel.isReadableContent {
            return viewModel.isContentRead ? .chalk.dark500 : .chalk.dark700
        } else {
            return .chalk.dark800
        }
    }

    private var secondaryTextColor: Color {
        if viewModel.isCommentActivity {
            return viewModel.shouldShowUnreadNotificationState ? .chalk.dark700 : .chalk.dark500
        } else if viewModel.isReadableContent {
            return viewModel.isContentRead ? .chalk.dark500 : .chalk.dark600
        } else {
            return .chalk.dark600
        }
    }

    private let titleLineLimit: Int = 2

    private var subtitleLineLimit: Int {
        viewModel.isCommentActivity || viewModel.isReadableContent ? 2 : 4
    }

    private var titleStyle: AthleticFont.Style {
        if viewModel.isCommentActivity {
            return .calibreUtility.l.medium
        } else {
            return viewModel.isReadableContent
                ? .tiemposHeadline.xxs.regular : .calibreUtility.l.medium
        }
    }

    private var subtitleStyle: AthleticFont.Style {
        if viewModel.isCommentActivity {
            return .calibreUtility.s.regular
        } else {
            return viewModel.isReadableContent ? .tiemposBody.xs.regular : .calibreUtility.s.regular
        }
    }

    private var titleKerning: Double {
        if viewModel.isCommentActivity {
            return 0.21
        } else {
            return viewModel.isReadableContent ? 0.03 : 0.25
        }
    }

    private var subtitleKerning: Double {
        if viewModel.isCommentActivity {
            return 0.21
        } else {
            return viewModel.isReadableContent ? 0 : 0.14
        }
    }

    private var commentActivityIconBackgroundColor: Color {
        if viewModel.shouldShowUnreadNotificationState {
            return colorScheme == .dark ? .chalk.dark400 : .chalk.dark200
        } else {
            return .chalk.dark300
        }
    }

    private var dividerViewColor: Color {
        viewModel.shouldShowUnreadNotificationState ? .chalk.dark100 : .chalk.dark300
    }

    @State private var titleHeight: CGFloat = .zero
    @State private var subtitleHeight: CGFloat = .zero

    var body: some View {
        Button {
            guard let screen = viewModel.navigationDestination else { return }
            navigationModel.addScreenToSelectedTab(screen)
        } label: {
            VStack(spacing: 0) {
                ZStack {
                    if viewModel.shouldShowUnreadNotificationState {
                        Rectangle()
                            .fill(Color.chalk.constant.blue800)
                            .opacity(0.12)
                    }

                    HStack(spacing: 0) {
                        textContent
                        Spacer(minLength: 16)

                        HStack(spacing: 0) {
                            imageContent
                            timestampContent
                        }
                    }
                    .padding([.vertical, .leading], 16)
                }

                DividerView(color: dividerViewColor)
            }
            .background(Color.chalk.dark200)
        }
    }

    private var textContent: some View {
        VStack(alignment: .leading, spacing: 2) {
            if viewModel.isCommentActivity {

                /// Comment activity types (can't use Text because we need a reduced line height.)

                CustomUILabelView(
                    text: viewModel.title,
                    font: .preferredFont(for: .calibreUtility.s.medium),
                    lineHeightMultiple: 0.85,
                    numberOfLines: titleLineLimit,
                    textColor: primaryTextColor,
                    kerning: titleKerning,
                    dynamicHeight: $titleHeight
                )
                .frame(minHeight: titleHeight)

                CustomUILabelView(
                    text: viewModel.subtitle,
                    font: .preferredFont(for: .calibreUtility.xs.regular),
                    lineHeightMultiple: 0.85,
                    numberOfLines: subtitleLineLimit,
                    textColor: secondaryTextColor,
                    kerning: subtitleKerning,
                    dynamicHeight: $subtitleHeight
                )
                .frame(minHeight: subtitleHeight)
            } else {

                /// All other types

                Text(viewModel.title)
                    .fontStyle(titleStyle)
                    .foregroundColor(primaryTextColor)
                    .kerning(titleKerning)
                    .lineLimit(titleLineLimit)
                    .multilineTextAlignment(.leading)

                Text(viewModel.subtitle)
                    .fontStyle(subtitleStyle)
                    .foregroundColor(secondaryTextColor)
                    .kerning(subtitleKerning)
                    .lineLimit(subtitleLineLimit)
                    .multilineTextAlignment(.leading)
                    .padding(.top, 2)
            }

            Spacer(minLength: 0)
        }
    }

    @ViewBuilder
    private var imageContent: some View {
        if let imageUrl = viewModel.imageUrl {
            PlaceholderLazyImage(
                imageUrl: imageUrl,
                modifyImage: {
                    $0.aspectRatio(contentMode: .fill)
                }
            )
            .frame(width: imageDimension, height: imageDimension)
            .clipped()
            .grayscale(viewModel.isContentRead ? 0.99 : 0)
            .overlay(
                ReadCircleIndicator(dimension: 10)
                    .opacity(viewModel.isContentRead ? 1 : 0)
                    .offset(x: -3, y: 3),
                alignment: .topTrailing
            )
        } else if viewModel.isCommentActivity {
            ZStack {
                Rectangle()
                    .fill(commentActivityIconBackgroundColor)
                    .frame(width: imageDimension, height: imageDimension)

                Image(
                    viewModel.type == .commentLikeThreshold
                        ? "icn_like" : "icon_comments_with_padding"
                )
                .resizable()
                .renderingMode(.template)
                .foregroundColor(.chalk.dark800)
                .opacity(viewModel.shouldShowUnreadNotificationState ? 1 : 0.8)
                .padding(viewModel.type == .commentLikeThreshold ? 2 : 0)
                .frame(width: 30, height: 30)
            }
        }
    }

    private var timestampContent: some View {
        VStack(spacing: 4) {
            if viewModel.shouldShowUnreadNotificationState {
                RedCircleBadge(size: 8)
            }

            Text(viewModel.displayDate)
                .fontStyle(.calibreUtility.s.regular)
                .foregroundColor(
                    viewModel.isCommentActivity ? primaryTextColor : secondaryTextColor
                )
                .lineLimit(1)
        }
        .frame(width: 54)
        .padding(.horizontal, 2)
    }
}
