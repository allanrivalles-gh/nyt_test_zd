//
//  FeedA1SectionViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 14/3/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

struct FeedA1SectionViewModel: Hashable, Identifiable {
    let id: String
    let title: String
    let type: GQL.LayoutType
    let items: [SpotlightViewModel]
}
