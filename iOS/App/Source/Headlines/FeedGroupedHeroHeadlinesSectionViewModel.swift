//
//  FeedGroupedHeroHeadlinesSectionViewModel.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/4/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation

struct FeedGroupedHeroHeadlinesSectionViewModel: Identifiable {
    let hero: FeedHeroSectionViewModel
    let headlines: FeedHeadlinesSectionViewModel

    var id: [String] {
        [hero.id, headlines.id]
    }
}
