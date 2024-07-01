//
//  HeadlinesNoContentText.swift
//  HeadlinesWidgetExtension
//
//  Created by Leonardo da Silva on 24/05/23.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import SwiftUI

struct HeadlinesNoContentText: View {
    var body: some View {
        Text(Strings.noContentMessage.localized)
            .fontStyle(.calibreUtility.xl.regular)
            .foregroundColor(.chalk.dark700)
    }
}
