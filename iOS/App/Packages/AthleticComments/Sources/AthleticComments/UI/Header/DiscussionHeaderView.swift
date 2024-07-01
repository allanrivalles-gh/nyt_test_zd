//
//  SwiftUIView.swift
//
//
//  Created by Jason Leyrer on 8/31/22.
//

import AthleticUI
import SwiftUI

struct DiscussionHeaderView: View {

    let viewModel: DiscussionHeaderViewModel

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 0) {
                if let url = viewModel.logoUrl {
                    PlaceholderLazyImage(
                        imageUrl: url,
                        modifyImage: {
                            $0.aspectRatio(contentMode: .fit)
                        }
                    )
                    .frame(width: 30, height: 30)
                } else {
                    Image("a_brand")
                        .resizable()
                        .renderingMode(.template)
                        .foregroundColor(defaultLogoForegroundColor)
                        .padding(6)
                        .background(defaultLogoBackgroundColor)
                        .frame(width: 30, height: 30)
                        .clipShape(Circle())
                }

                DividerView(color: foregroundColor, axis: .vertical)
                    .frame(height: 22)
                    .padding(.horizontal, 8)

                Text(viewModel.typeName)
                    .fontStyle(.calibreHeadline.s.medium)

                if viewModel.isLive {
                    Spacer(minLength: 8)

                    LiveStatusIndicatorView(type: .blogContent)
                }
            }

            Text(viewModel.title)
                .fontStyle(.calibreHeadline.m.semibold)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.vertical, 16)

            Text(viewModel.excerpt)
                .fontStyle(.tiemposBody.m.regular)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.vertical, 8)

            VStack(alignment: .leading, spacing: 0) {
                Text(viewModel.authorName ?? "")
                    .fontStyle(.calibreUtility.s.medium)
                Text(viewModel.date.discussionDateString)
                    .fontStyle(.calibreUtility.s.regular)
            }
            .padding(.top, 16)
        }
        .foregroundColor(foregroundColor)
        .padding([.top, .horizontal], 16)
        .padding(.bottom, 24)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(viewModel.highlightColor ?? Color.chalk.dark300)
    }

    private var foregroundColor: Color {
        guard let color = viewModel.highlightColor else {
            return .chalk.dark700
        }

        return Color.highContrastAppearance(of: .chalk.dark700, forBackgroundColor: color)
    }

    private var defaultLogoBackgroundColor: Color {
        guard let highlightColor = viewModel.highlightColor else {
            return .chalk.dark800
        }

        return Color.highContrastAppearance(
            of: .chalk.dark800,
            forBackgroundColor: highlightColor
        )
    }

    private var defaultLogoForegroundColor: Color {
        guard viewModel.highlightColor != nil else { return .chalk.dark200 }

        return Color.highContrastAppearance(
            of: .chalk.dark200,
            forBackgroundColor: defaultLogoBackgroundColor
        )
    }
}
