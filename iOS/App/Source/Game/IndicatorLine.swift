//
//  IndicatorLine.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 21/2/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import SwiftUI

struct IndicatorLine: View {
    let viewModel: IndicatorLineViewModel

    var body: some View {
        HStack(spacing: 2) {
            let _ = DuplicateIDLogger.logDuplicates(
                in: Array(zip(viewModel.items.indices, viewModel.items)),
                id: \.0
            )
            ForEach(Array(zip(viewModel.items.indices, viewModel.items)), id: \.0) { _, item in
                Circle()
                    .fill(
                        item.isHighlighted
                            ? Color.chalk.yellow
                            : Color.chalk.dark500
                    )
                    .frame(width: 4, height: 4)
            }
        }
    }
}
