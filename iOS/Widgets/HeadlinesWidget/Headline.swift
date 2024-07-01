//
//  Headline.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

struct Headline: Hashable {
    let id: String
    let title: String
    let tag: String?
    let date: Date
    let imageURI: String?
}
