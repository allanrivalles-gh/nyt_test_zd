//
//  BannerModifier.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 5/5/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import Combine
import Foundation
import SwiftUI

extension View {
    public func banner(
        _ viewModel: Binding<BannerViewModel?>,
        dismissAfter dismissTime: TimeInterval? = nil
    ) -> some View {
        modifier(
            BannerModifier(
                viewModel: viewModel,
                modifierViewModel: BannerModifierViewModel(dismissTime: dismissTime)
            )
        )
    }
}

private final class BannerModifierViewModel: ObservableObject {
    @Published var isBannerVisible = false {
        didSet {
            if isBannerVisible {
                startDismissTimer()
            } else {
                clearDismissTimer()
            }
        }
    }

    private let dismissTime: TimeInterval?

    init(dismissTime: TimeInterval?) {
        self.dismissTime = dismissTime
    }

    private var timerCancellable: AnyCancellable?

    private func startDismissTimer() {
        guard let dismissTime = dismissTime else { return }

        timerCancellable = Timer.publish(every: dismissTime, on: .main, in: .default)
            .autoconnect()
            .first()
            .sink { [weak self] _ in
                self?.isBannerVisible = false
            }
    }

    private func clearDismissTimer() {
        timerCancellable = nil
    }
}

private struct BannerModifier: ViewModifier {
    @Binding var viewModel: BannerViewModel?
    @StateObject var modifierViewModel: BannerModifierViewModel

    @State private var displayedViewModel: BannerViewModel?
    @State private var isBannerVisible = false

    func body(content: Content) -> some View {
        content
            .overlay(
                Group {
                    if let displayedViewModel = displayedViewModel, isBannerVisible {
                        banner(with: displayedViewModel)
                    }
                },
                alignment: .top
            )
            .onChange(of: viewModel) { viewModel in
                if viewModel != nil {
                    displayedViewModel = viewModel
                    modifierViewModel.isBannerVisible = true
                } else {
                    modifierViewModel.isBannerVisible = false
                }
            }
            .onChange(of: displayedViewModel) { displayedViewModel in
                if displayedViewModel == nil {
                    viewModel = nil
                }
            }
            .onChange(of: modifierViewModel.isBannerVisible) { isBannerVisible in
                withAnimation(Animation.easeInOut(duration: 0.3)) {
                    self.isBannerVisible = isBannerVisible
                }
            }
    }

    private func banner(with viewModel: BannerViewModel) -> some View {
        Banner(viewModel: viewModel)
            .onTapGesture {
                modifierViewModel.isBannerVisible = false
            }
            .onDragGesture(
                minimumDistance: 0,
                onEnded: { gesture in
                    if gesture.translation.height < -20 {
                        modifierViewModel.isBannerVisible = false
                    }
                }
            )
            .onDisappear {
                displayedViewModel = nil
            }
            .transition(
                .move(edge: .top).combined(with: .opacity)
            )
    }
}
