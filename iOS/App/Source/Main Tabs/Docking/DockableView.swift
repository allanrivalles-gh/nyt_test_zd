//
//  DockableView.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 5/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import UIKit

protocol DockableView: UIView {

    /// Delegate for handling behaviour of the dockable view
    var lifeCycleDelegate: DockableViewLifeCycleDelegate? { get }

    /// The bottom anchor point of the view that should be be docked to the host
    var dockBaselineAnchor: NSLayoutYAxisAnchor { get }

    /// The docked item height when docked at the given position.
    /// This should return the height of the view that will be above the dock baseline, not including the "unsafe" area below it.
    /// - Parameter dockPosition: Dock position
    func height(for dockPosition: DockPosition) -> CGFloat

    /// Called when the docked view changes position e.g. if the tab bar gets hidden
    /// - Parameter newPosition: Where it is now positioned
    func dockPositionDidChange(_ newPosition: DockPosition)
}

/// Default implementation
extension DockableView {
    var lifeCycleDelegate: DockableViewLifeCycleDelegate? { nil }
}

protocol DockableViewLifeCycleDelegate: AnyObject {

    /// Whether this view should allow the other view to replace it
    /// - Parameters:
    ///   - view: Docked view
    ///   - shouldAllowOverwriteBy: Proposed view to replace it
    func dockableView(_ view: DockableView, shouldAllowOverwriteBy: DockableView) -> Bool

    /// Called before the view gets docked, prior starting the docking process
    /// - Parameters:
    ///   - view: View to be docked
    ///   - animated: Whether docking will be animated
    func dockableViewWillDock(_ view: DockableView, animated: Bool)

    /// Called after the view has finished being docked
    /// - Parameters:
    ///   - view: View that was docked
    ///   - animated: Whether the docking was animated
    func dockableViewDidDock(_ view: DockableView, animated: Bool)

    /// Called before the view gets undocked, prior starting the undocking process
    /// - Parameters:
    ///   - view: View to be undocked
    ///   - animated: Whether undocking will be animated
    func dockableViewWillUndock(_ view: DockableView, animated: Bool)

    /// Called after the view has finished being undocked
    /// - Parameters:
    ///   - view: View that was undocked
    ///   - animated: Whether the undocking was animated
    func dockableViewDidUndock(_ view: DockableView, animated: Bool)

}

/// Default implementation
extension DockableViewLifeCycleDelegate {
    func dockableView(_ view: DockableView, shouldAllowOverwriteBy: DockableView) -> Bool { true }
    func dockableViewWillDock(_ view: DockableView, animated: Bool) {}
    func dockableViewDidDock(_ view: DockableView, animated: Bool) {}
    func dockableViewWillUndock(_ view: DockableView, animated: Bool) {}
    func dockableViewDidUndock(_ view: DockableView, animated: Bool) {}
}

enum DockPosition {
    case tabBar
    case homeIndicator
    case screenBottom
}
