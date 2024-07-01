//
//  PlayerGradeAllPlayersGradedView.swift
//  theathletic-ios
//
//  Created by Duncan Lau on 21/12/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Foundation
import SwiftUI

struct PlayerGradeAllGradedView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.gameSelectTab) private var gameSelectTab
    var viewModel: PlayerGradeAllGradedViewModel

    var body: some View {
        ZStack {
            Text(viewModel.gradedAllPlayersText)
                .multilineTextAlignment(.center)
                .fontStyle(.calibreHeadline.s.medium)
                .foregroundColor(.chalk.dark800)
                .padding(.horizontal, 75)

            VStack(spacing: 0) {
                Text(viewModel.title)
                    .fontStyle(.slab.m.bold)
                    .foregroundColor(.chalk.dark800)

                Spacer()

                Button(viewModel.seeAllPlayerGradesText) {
                    dismiss()
                    gameSelectTab(.playerGrades(.placeholder))
                }
                .buttonStyle(.core(size: .regular, level: .primary))
                .padding(.horizontal, 16)
            }
            .padding(.top, 19)
            .padding(.bottom, 50)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.chalk.dark200)
    }
}

struct PlayerGradeAllPlayersGradedView_Previews: PreviewProvider {

    static var previews: some View {
        Group {
            PlayerGradeAllGradedView(
                viewModel: PlayerGradeAllGradedViewModel(id: "all-graded")
            )
            PlayerGradeAllGradedView(
                viewModel: PlayerGradeAllGradedViewModel(id: "all-graded")
            )
            .darkScheme()
        }

    }
}
