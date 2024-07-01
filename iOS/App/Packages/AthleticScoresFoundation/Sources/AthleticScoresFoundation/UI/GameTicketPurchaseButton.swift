//
//  GameTicketPurchaseButton.swift
//
//
//  Created by Mark Corbyn on 29/6/2023.
//

import AthleticAnalytics
import AthleticUI
import SwiftUI

public struct GameTicketPurchaseButton: View {

    public let viewModel: GameTicketPurchaseButtonViewModel
    public let shouldShowLogo: Bool
    public let fontStyle: AthleticFont.Style
    public let fontForegroundColor: Color
    public let chevronWidth: CGFloat
    public let chevronHeight: CGFloat
    public let chevronForegroundColor: Color
    public let verticalPadding: CGFloat

    @Environment(\.openURL) private var openUrl
    @Environment(\.colorScheme) private var colorScheme

    public init(
        viewModel: GameTicketPurchaseButtonViewModel,
        shouldShowLogo: Bool = true,
        fontStyle: AthleticFont.Style = .calibreUtility.s.regular,
        fontForegroundColor: Color = .chalk.dark600,
        chevronWidth: CGFloat = 10,
        chevronHeight: CGFloat = 10,
        chevronForegroundColor: Color = .chalk.dark700,
        verticalPadding: CGFloat = 16
    ) {
        self.viewModel = viewModel
        self.shouldShowLogo = shouldShowLogo
        self.fontStyle = fontStyle
        self.fontForegroundColor = fontForegroundColor
        self.chevronWidth = chevronWidth
        self.chevronHeight = chevronHeight
        self.chevronForegroundColor = chevronForegroundColor
        self.verticalPadding = verticalPadding
    }

    public var body: some View {
        Button(action: {
            Task {
                await viewModel.trackClickEvent()
            }
            openUrl(viewModel.url)
        }) {
            HStack(spacing: 0) {
                Spacer(minLength: 0)
                if shouldShowLogo, let logoUrl {
                    PlaceholderLazyImage(
                        imageUrl: logoUrl,
                        modifyImage: { $0.aspectRatio(contentMode: .fit) }
                    )
                    .padding(.trailing, 8)
                    .frame(height: 16)
                }

                HStack(spacing: 4) {
                    Text(viewModel.title)
                        .foregroundColor(fontForegroundColor)
                        .fontStyle(fontStyle)

                    Chevron(
                        foregroundColor: chevronForegroundColor,
                        width: chevronWidth,
                        height: chevronHeight,
                        direction: .right
                    )
                }
                .padding(.vertical, verticalPadding)

                Spacer(minLength: 0)
            }
            .background(Color.chalk.dark200)
        }
    }

    private var logoUrl: URL? {
        let logos = colorScheme == .dark ? viewModel.darkModeLogos : viewModel.lightModeLogos
        return logos.bestUrl(for: CGSize(width: 100, height: 16))
    }
}

struct GameTicketPurchaseButton_Previews: PreviewProvider {

    @ViewBuilder
    private static var content: some View {
        let viewModel = GameTicketPurchaseButtonViewModel(
            title: "Buy the amazing tickets",
            url: "https://theathletic.com/tickets".url!,
            provider: "provider",
            lightModeLogos: [
                .init(
                    url:
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/The_Athletic_wordmark_black_2020.svg/500px-The_Athletic_wordmark_black_2020.svg.png"
                        .url!
                )
            ],
            darkModeLogos: [
                .init(
                    url:
                        "https://theathletic.com/app/themes/athletic/assets/img/open-graph-asset.png"
                        .url!
                )
            ],
            analyticsDefaults: PreviewAnalyticDefaults()
        )

        GameTicketPurchaseButton(viewModel: viewModel)
            .loadCustomFonts()
    }

    static var previews: some View {
        Group {
            content
                .preferredColorScheme(.dark)

            content
                .preferredColorScheme(.light)
        }
    }
}
