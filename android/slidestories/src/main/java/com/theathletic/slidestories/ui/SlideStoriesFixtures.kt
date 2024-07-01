package com.theathletic.slidestories.ui

import com.theathletic.slidestories.ui.slidecomponents.SlideCardBlocks

object SlideStoriesFixtures {

    val takeawaySmallMessage = SlideStoriesUiModel.TakeawaySmallMessage(
        "001",
        "Video: Stephen Maturen / Getty Images"
    )

    val takeawayMessage = SlideStoriesUiModel.TakeawayMessage(
        "002",
        "9.",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    )

    val readMore = SlideStoriesUiModel.ReadMore(
        "003",
        "Go Deeper",
        "Wilfried Zaha and Manchester United: What really happened",
        "",
        ""
    ) {}

    val byLineIntro = SlideStoriesUiModel.Byline(
        "004",
        "By Andy McCullough",
        "Reporting from Dallas, TX",
        listOf("A"),
        true
    )

    val byLineFooter = SlideStoriesUiModel.Byline(
        "004",
        "By Andy McCullough",
        null,
        listOf("A"),
        false
    )

    val slideQuoteLarge = SlideStoriesUiModel.QuoteSlide(
        id = "005",
        quote = "He’s got a special ability. I tell him, if you can do that every day, you’re going to be one of the top dudes in this league for a while. He’s got all the ability in the world.",
        attributor = "Nathan Eovaldi",
        attributorRole = "Rangers Starting Pitcher",
        slideCardBlocks = SlideCardBlocks(byLineFooter, takeawayMessage, readMore)
    )

    val slideQuoteSmall = SlideStoriesUiModel.QuoteSlide(
        id = "006",
        quote = "I don't know how many rabbits I have left in my hat.",
        attributor = "Nathan Eovaldi",
        attributorRole = "Rangers Starting Pitcher",
        slideCardBlocks = SlideCardBlocks(byLineIntro, takeawayMessage, readMore)
    )

    val slideImage = SlideStoriesUiModel.ImageSlide(
        id = "007",
        imageUrl = "https://s3-alpha-sig.figma.com/img/17b5/6a71/35b32a649d608d0b89308a7db68b5521?Expires=1705881600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=X17j4Kzr2HMObrwvs1vcnye5N0heNv-rOWbeekMPOrMRiYMwdaOhVl681AedU7LhCnmKYWALEhnrNq4ok-2GhiPi9tvERxloGpgXebz1ITl1mTUURycookIgRGJOmx1GccyjR-G-AwPSjX-Sj8LKKK5c-9diyG7RtTIZkdEts3j2tlkrn8h9UMvaNOVLa6zm4vhqt7uhp52cv4LVVRoUp50-vLy25d8pPkIdoKir1gUuwfnlwDsO8ILQ~bndFiZJnF~MolqzF1cM9c-FSfaP9qA-fbcc88c9b7mBC8~io5UnF1lu5OTch4kYLo4Ouixut2NrG6ihKdo9f7HNL1UMhQ__",
        credit = "Steve Limentani / ISI Photos / Getty Images",
        slideCardBlocks = SlideCardBlocks(takeawayMessage = takeawayMessage)

    )
}