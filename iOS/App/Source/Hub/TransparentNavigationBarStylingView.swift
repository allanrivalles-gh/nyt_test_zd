//
//  TransparentNavigationBarStylingView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 28/7/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI
import UIKit

struct TransparentNavigationBarStylingView: UIViewControllerRepresentable {

    let foregroundColor: UIColor

    /// Whether the original style should be re-applied when the view disappears.
    /// Note, the view may disappear when pushing forwards to a new view or popping backwards to the previous view.
    let shouldRestoreOnDisappear: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = NavigationBarStylingViewController()
        viewController.navigationForegroundColor = foregroundColor
        viewController.shouldRestoreOnDisappear = shouldRestoreOnDisappear
        return viewController
    }
}

private final class NavigationBarStylingViewController: UIViewController {

    fileprivate struct Style {
        let standardAppearance: UINavigationBarAppearance
        let compactAppearance: UINavigationBarAppearance?
        let scrollEdgeAppearance: UINavigationBarAppearance?
        let tintColor: UIColor
    }

    var navigationForegroundColor: UIColor!
    var shouldRestoreOnDisappear: Bool!

    /// A cache of the style of the navigation bar before this view controller appeared
    private var originalStyle: Style?

    private var style: Style {
        let appearance = UINavigationBar.makeAppearance(
            foregroundColor: navigationForegroundColor
        )

        return Style(
            standardAppearance: appearance,
            compactAppearance: appearance,
            scrollEdgeAppearance: appearance,
            tintColor: navigationForegroundColor
        )
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .clear
        preferredContentSize = .zero
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        guard let navigationBar = navigationController?.navigationBar else {
            return
        }

        if originalStyle == nil {
            /// Only capture the original style once.
            /// We don't want to capture the style when this view appears after navigation back from a deeper screen.
            originalStyle = navigationBar.style
        }

        apply(style: style, to: navigationBar)
    }

    override func viewWillDisappear(_ animated: Bool) {
        defer {
            super.viewWillDisappear(animated)
        }

        guard
            shouldRestoreOnDisappear,
            let navigationBar = navigationController?.navigationBar,
            let originalStyle = originalStyle
        else {
            return
        }

        apply(style: originalStyle, to: navigationBar)
    }

    private func apply(style: Style, to navigationBar: UINavigationBar) {
        navigationBar.standardAppearance = style.standardAppearance
        navigationBar.compactAppearance = style.compactAppearance
        navigationBar.scrollEdgeAppearance = style.scrollEdgeAppearance

        navigationBar.tintColor = style.tintColor
    }
}

extension UINavigationBar {
    fileprivate var style: NavigationBarStylingViewController.Style {
        .init(
            standardAppearance: standardAppearance,
            compactAppearance: compactAppearance,
            scrollEdgeAppearance: scrollEdgeAppearance,
            tintColor: tintColor
        )
    }
}
