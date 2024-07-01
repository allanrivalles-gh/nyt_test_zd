//
//  HeadlinesNoNetworkText.swift
//  Widgets
//
//  Created by Aja Beckett on 1/10/24.
//  Copyright © 2024 The Athletic. All rights reserved.
//

import SwiftUI

struct HeadlinesNoNetworkText: View {
    var body: some View {
        Text(Strings.noNetworkMessage.localized)
            .fontStyle(.calibreUtility.xl.regular)
            .foregroundColor(.chalk.dark700)
    }
}
