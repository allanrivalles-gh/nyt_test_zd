//
//  NotificationCenterRowPreviews.swift
//
//
//  Created by Jason Leyrer on 7/14/23.
//

import SwiftUI

struct NotificationCenterRow_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            NotificationCenterRow(
                viewModel: .init(
                    notification: .init(
                        id: "1",
                        type: .headline,
                        title: "‘A huge difference’ for kickers",
                        subtitle:
                            "How NFL equipment managers race against the clock to prepare footballs",
                        createdAt: Date(timeIntervalSince1970: 1_689_028_303),
                        deeplink: "theathletic://headline/4681173",
                        permalink: "https://theathletic.com/4681173",
                        isBadgeable: false,
                        isNotificationRead: false,
                        platform: "iterable"
                    ),
                    deeplinkScreenProvider: { _ in return nil },
                    network: NotificationCenterPreviewHelper.network
                )
            )

            NotificationCenterRow(
                viewModel: .init(
                    notification: .init(
                        id: "2",
                        type: .headline,
                        title: "‘A huge difference’ for kickers",
                        subtitle:
                            "How NFL equipment managers race against the clock to prepare footballs",
                        imageUrl:
                            "https://cdn.theathletic.com/app/uploads/2023/06/27160610/GettyImages-1502973080-scaled-e1687897830278-1024x683.jpg",
                        createdAt: Date(timeIntervalSince1970: 1_689_028_303),
                        deeplink: "theathletic://headline/4681173",
                        permalink: "https://theathletic.com/4681173",
                        isBadgeable: false,
                        isNotificationRead: false,
                        platform: "iterable"
                    ),
                    deeplinkScreenProvider: { _ in return nil },
                    network: NotificationCenterPreviewHelper.network
                )
            )

            NotificationCenterRow(
                viewModel: .init(
                    notification: .init(
                        id: "3",
                        type: .boxscore,
                        title: "FINAL: GSW 101 - LAL 122",
                        subtitle:
                            "Lakers defeat Warriors\nL. James (LAL): 30 PTS, 9 AST, 9 REB\nS. Curry (GSW): 32 PTS, 5 AST, 6 REB",
                        createdAt: Date(timeIntervalSince1970: 1_689_028_303),
                        deeplink: "theathletic://headline/4681173",
                        permalink: "https://theathletic.com/4681173",
                        isBadgeable: false,
                        isNotificationRead: false,
                        platform: "iterable"
                    ),
                    deeplinkScreenProvider: { _ in return nil },
                    network: NotificationCenterPreviewHelper.network
                )
            )

            NotificationCenterRow(
                viewModel: .init(
                    notification: .init(
                        id: "4",
                        type: .podcast,
                        title: "🎙️ The Athletic Football Show",
                        subtitle:
                            "New episode: Kellen Moore to the Chargers, Vic Fangio (maybe) to the Dolphins, updates on ongoing head coach searches, and more with Mike S...",
                        createdAt: Date(timeIntervalSince1970: 1_689_028_303),
                        deeplink: "theathletic://headline/4681173",
                        permalink: "https://theathletic.com/4681173",
                        isBadgeable: false,
                        isNotificationRead: false,
                        platform: "iterable"
                    ),
                    deeplinkScreenProvider: { _ in return nil },
                    network: NotificationCenterPreviewHelper.network
                )
            )

            NotificationCenterRow(
                viewModel: .init(
                    notification: .init(
                        id: "1",
                        type: .commentLikeThreshold,
                        title:
                            "Your comment on ‘Tim Tebow released by Jacksonville’ reached 25 likes!",
                        subtitle:
                            "Everyone laughing should consider the benefits of Tebow transitioning to punter.",
                        createdAt: Date(timeIntervalSince1970: 1_689_028_303),
                        deeplink: "theathletic://headline/4681173",
                        permalink: "https://theathletic.com/4681173",
                        isBadgeable: true,
                        isNotificationRead: false,
                        platform: "iterable"
                    ),
                    deeplinkScreenProvider: { _ in return nil },
                    network: NotificationCenterPreviewHelper.network
                )
            )
        }
    }
}
