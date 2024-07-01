//
//  PodcastPreviewHelper.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 1/29/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Apollo
import AthleticApolloTypes
import SwiftUI

struct PodcastPreviewHelper {
    static var allExamples: [GQL.PodcastDetail] = [
        athleticHockeyShow, soccerEveryDay, oneOfTheseYears, theBeatWithBrendanQuinn, theLead,
    ]
    static var national: [GQL.PodcastDetail] = [athleticHockeyShow, soccerEveryDay, theLead]
    static var local: [GQL.PodcastDetail] = [theBeatWithBrendanQuinn, oneOfTheseYears]
    static var userPodcasts: [GQL.PodcastDetail] = [athleticHockeyShow, oneOfTheseYears]
    static var recommendedPodcats: [GQL.PodcastDetail] = [theBeatWithBrendanQuinn, theLead]

    static var athleticHockeyShow: GQL.PodcastDetail = GQL.PodcastDetail(
        id: "240",
        title: "The Athletic Hockey Show",
        description:
            "The Athletic's flagship hockey podcast features hosts Ian Mendes, Craig Custance, Hailey Salvian, Sean Gentille, Sean McIndoe, CBC's Rob Pizzo, Jesse Granger, Sara Civian, Max Bultman, and Corey Pronman, as well as The Athletic's great NHL writers, delivering previews of the week's best matchups, interviews with league insiders, analytics, prospect updates, betting picks, and much more, every Monday, Tuesday, Wednesday, and Thursday, plus select Fridays throughout the season.",
        notifEpisodesOn: false,
        imageUrl: "https://cdn.theathletic.com/app/uploads/2021/08/03202726/TA-Hockey-Show.jpg",
        shortDescription:
            "Ian Mendes, Craig Custance, Hailey Salvian, Sean Gentille, Sean McIndoe, CBC's Rob Pizzo, Jesse Granger, Sara Civian, Max Bultman, and Corey Pronman bring you NHL coverage 5 days a week.",
        metadataString: "NHL",
        isFollowing: true
    )

    static var soccerEveryDay: GQL.PodcastDetail = GQL.PodcastDetail(
        id: "241",
        title: "Soccer Every Day",
        description:
            "",
        notifEpisodesOn: false,
        imageUrl: "https://cdn.theathletic.com/app/uploads/2021/08/03202726/TA-Hockey-Show.jpg",
        shortDescription:
            "Ian Mendes, Craig Custance, Hailey Salvian, Sean Gentille, Sean McIndoe, CBC's Rob Pizzo, Jesse Granger, Sara Civian, Max Bultman, and Corey Pronman bring you NHL coverage 5 days a week.",
        metadataString: "NHL",
        isFollowing: false
    )

    static var oneOfTheseYears: GQL.PodcastDetail = GQL.PodcastDetail(
        id: "250",
        title: "One of These Years",
        description:
            "Detroit Lions coverage with veteran beat writers Chris Burke and Nick Baumgardner of The Athletic, bringing you in-depth analysis, the X's & O's, off-the-field stories & more.",
        notifEpisodesOn: true,
        imageUrl:
            "https://cdn.theathletic.com/app/uploads/2021/09/07180104/OneOfTheseYears.png",
        shortDescription:
            "Detroit Lions coverage with veteran beat writers Chris Burke and Nick Baumgardner.",
        isFollowing: true
    )

    static var theBeatWithBrendanQuinn: GQL.PodcastDetail = GQL.PodcastDetail(
        id: "85",
        title: "The Beat with Brendan Quinn and Nick Baumgardner",
        description:
            "The Beat is a college sports podcast from The Athletic Detroit that takes you places you’d least expect. Hosts Nick Baumgardner, Brendan Quinn, Austin Meek and Colton Pouncy will talk all things Michigan college sports and be sure to keep you up to date on the Wolverines and Spartans.\r\n\r\nMake sure to subscribe to catch every single episode at theathletic.com/collegebeat",
        notifEpisodesOn: nil,
        imageUrl:
            "https://cdn.theathletic.com/app/uploads/2019/11/07072010/TheBeat.jpg",
        shortDescription:
            "A podcast about the State of Michigan's college sports.",
        metadataString: "Michigan State Spartans",
        isFollowing: false
    )

    static var theLead: GQL.PodcastDetail = GQL.PodcastDetail(
        id: "99",
        title: "The Lead",
        description:
            "The award-winning daily sports show from Wondery and The Athletic, known for delivering the best storytelling in sports. Every weekday, co-hosts Tiffany Oshinsky and Anders Kelto dive into the biggest and most fascinating sports stories of the day, as told by the reporters who cover them up close. The Lead cuts through the chatter and brings you in-depth reporting and emotional stories, to help make sense of the complicated sports landscape. New episodes every Monday through Friday.",
        notifEpisodesOn: nil,
        imageUrl:
            "https://content.production.cdn.art19.com/images/c0/6a/f9/04/c06af904-afb3-456a-bded-3e4e07736639/7e2474e7ee866124695b3e5e669e8f4784079577ab4b8b84d0e50332702442f4408964a64d6cb81c9e4e23bda83facdcd7e25c266013decfb305cca9a6fdb4a9.jpeg",
        shortDescription:
            "The award-winning daily sports show from Wondery and The Athletic, known for delivering the best storytelling in sports. Every weekday, co-hosts Tiffany Oshinsky and Anders Kelto dive into the biggest and most fascinating sports stories of the day...",
        metadataString: nil,
        isFollowing: false
    )

    static var podcastEpisodeConsumable: GQL.PodcastEpisodeConsumable =
        GQL.PodcastEpisodeConsumable(
            id: "f4e134a31d5e19edd7def0e73d1977691c9c07b6c75e5a8e",
            description:
                "In this week's Nerder with Dave, Seth and Mo. Cleveland\\'s fun young core leading with defense. Teams have something to play for and the joy that comes with it. Joel Embiid's resurgence post covid absence. Trade talk with the Pacers, Blazers, Knicks, Warriors, and Celtics. Fits for Myles Turner and Domantas Sabonis.",
            duration: 3420,
            imageUrl:
                "https://cdn.theathletic.com/app/uploads/2023/04/27090325/NBA_SHOW3000-scaled.jpg",
            mp3Url:
                "https://staging2.theathletic.com/signed-mp3-redirect-url/?podcast_episode_id=24265",
            number: 1,
            permalink: "https://staging2.theathletic.com/?p=24265",
            publishedAt: Date.now,
            title: "Fun Young Cavs! Playing with Joy, Embiid\\'s Resurgence + Trade Talk",
            disableComments: false,
            podcastCommentCount: 3,
            podcastEpisodeId: "12",
            parentPodcast: try! .init(
                GQL.PodcastDetail(
                    id: "240",
                    title: "The Athletic Hockey Show",
                    description:
                        "The Athletic's flagship hockey podcast features hosts Ian Mendes, Craig Custance, Hailey Salvian, Sean Gentille, Sean McIndoe, CBC's Rob Pizzo, Jesse Granger, Sara Civian, Max Bultman, and Corey Pronman, as well as The Athletic's great NHL writers, delivering previews of the week's best matchups, interviews with league insiders, analytics, prospect updates, betting picks, and much more, every Monday, Tuesday, Wednesday, and Thursday, plus select Fridays throughout the season.",
                    notifEpisodesOn: true,
                    imageUrl:
                        "https://cdn.theathletic.com/app/uploads/2021/08/03202726/TA-Hockey-Show.jpg",
                    shortDescription:
                        "Ian Mendes, Craig Custance, Hailey Salvian, Sean Gentille, Sean McIndoe, CBC's Rob Pizzo, Jesse Granger, Sara Civian, Max Bultman, and Corey Pronman bring you NHL coverage 5 days a week.",
                    isFollowing: true
                )
            ),
            clips: [
                .init(
                    id: 1,
                    startPosition: 0,
                    endPosition: 1000,
                    title: "First clip title"
                ),
                .init(
                    id: 2,
                    startPosition: 1001,
                    endPosition: 2001,
                    title: "Second clip title"
                ),
                .init(
                    id: 3,
                    startPosition: 2002,
                    endPosition: 3420,
                    title: "Third clip title"
                ),
            ]
        )

    static var boxScorePodcastEpisode: GQL.BoxScorePodcastEpisodeBlock =
        GQL.BoxScorePodcastEpisodeBlock(
            id: "f4e134a31d5e19edd7def0e73d1977691c9c07b6c75e5a8e",
            episodeId: "25214",
            title: "Fun Young Cavs! Playing with Joy, Embiid's Resurgence + Trade Talk",
            description:
                "In this week's Nerder with Dave, Seth and Mo. Cleveland's fun young core leading with defense. Teams have something to play for and the joy that comes with it. Joel Embiid's resurgence post covid absence. Trade talk with the Pacers, Blazers, Knicks, Warriors, and Celtics. Fits for Myles Turner and Domantas Sabonis.",
            disableComments: false,
            commentCount: 22,
            imageUrl:
                "https://cdn.theathletic.com/app/uploads/2023/04/27090325/NBA_SHOW3000-scaled.jpg",
            mp3Url:
                "https://staging2.theathletic.com/signed-mp3-redirect-url/?podcast_episode_id=24265",
            permalink: "https://staging2.theathletic.com/?p=24265",
            publishedAt: Date.now,
            duration: 3420,
            clips: [
                .init(
                    id: 1,
                    title: "First clip title",
                    startPosition: 0,
                    endPosition: 1000
                ),
                .init(
                    id: 2,
                    title: "Second clip title",
                    startPosition: 1001,
                    endPosition: 2001
                ),
                .init(
                    id: 3,
                    title: "Third clip title",
                    startPosition: 2002,
                    endPosition: 3420
                ),
            ],
            podcastId: "240",
            podcastTitle: "The Athletic Hockey Show",
            userData: .init(
                isFinished: false,
                timeElapsed: 101
            )
        )
}
