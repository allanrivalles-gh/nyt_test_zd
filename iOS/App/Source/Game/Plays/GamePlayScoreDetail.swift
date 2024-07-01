//
//  GamePlayScoreDetail.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 19/5/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

struct GamePlayScoreDetail: View {
    let viewModel: GamePlayScoreDetailViewModel

    var body: some View {
        HStack(spacing: 2) {
            GamePlayScoreTile(viewModel: viewModel.firstScore)
            GamePlayScoreTile(viewModel: viewModel.secondScore)
        }
    }
}

private struct GamePlayScoreTile: View {
    let viewModel: GamePlayScoreViewModel

    var body: some View {
        VStack(spacing: 2) {
            Text(viewModel.value)
                .fontStyle(.calibreUtility.l.medium)
                .foregroundColor(.chalk.dark700)
            Text(viewModel.title)
                .fontStyle(.calibreUtility.s.regular)
                .foregroundColor(.chalk.dark500)
        }
        .frame(minWidth: 32)
    }
}
