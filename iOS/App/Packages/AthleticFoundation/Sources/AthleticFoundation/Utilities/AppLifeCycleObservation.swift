//
//  AppLifeCycleObservation.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 26/7/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import UIKit

public enum LifeCycleEvent {
    case appWillForeground
    case appDidBackground
    case appDidBecomeActive
    case appWillResignActive
    case appTerminated
    case userCredentialsUpdated
    case userFollowingUpdated
}

public protocol AppLifeCycleProtocol {
    func appDidBecomeActive()
    func appWillResignActive()
    func appWillForeground()
    func appDidBackground()
    func appTerminated()
    func userCredentialsUpdated()
    func userFollowingUpdated()
}

extension AppLifeCycleProtocol where Self: AnyObject {
    // MARK: - Defaults

    public func appDidBecomeActive() {}
    public func appWillResignActive() {}
    public func appWillForeground() {}
    public func appDidBackground() {}
    public func appTerminated() {}
    public func userCredentialsUpdated() {}
    public func userFollowingUpdated() {}

}

/// Registers and deregisters life cycle observers
///
/// This class is responsible for maintaining `observer` returned from the notification center.
/// You can request this object to register/deregister life cycle events to be forwarded on to the provided `AppLifeCyleObservingObject` instance.
/// Upon deallocation of your `AppLifeCyleObservingObject` instance, you don't need to deregister the observers, this class with automatically deregister for you.
/// Upon `deinit` this object also removes any observers it has stored, so if you replace this instance with a different one
public class AppLifeCycleObservation {

    public typealias AppLifeCyleObservingObject = AppLifeCycleProtocol & AnyObject

    private var observers: [LifeCycleEvent: NSObjectProtocol] = [:]

    private weak var lifeCycleObserver: AppLifeCyleObservingObject?
    private var notificationCenter: NotificationCenter

    /// A list of the event types that are currently registered
    var registeredEvents: Set<LifeCycleEvent> {
        return Set(observers.keys)
    }

    /// Create an observation manager for the given object
    /// - Parameters:
    ///   - lifeCycleObserver: Object interested in receiving life cycle event callbacks. This is held as w weak reference, you are responsible for ensuring the instance lives for the required duration.
    ///   - notificationCenter: The notification center to register observations on.
    public init(
        _ lifeCycleObserver: AppLifeCyleObservingObject,
        notificationCenter: NotificationCenter = .default
    ) {
        self.lifeCycleObserver = lifeCycleObserver
        self.notificationCenter = notificationCenter
    }

    deinit {
        observers.keys.forEach {
            if let observer = observers.removeValue(forKey: $0) {
                notificationCenter.removeObserver(observer)
            }
        }
    }

    /// Register for the given life cycle events
    ///
    /// NB: If an event is already being observed a new observation will not be created for it.
    /// - Parameter events: Events to start observing
    public func register(for events: LifeCycleEvent...) {
        let existingEvents = observers.keys
        var newObservers: [LifeCycleEvent: NSObjectProtocol] = [:]
        for event in events where !existingEvents.contains(event) {
            switch event {
            case .appWillForeground:
                newObservers[event] =
                    notificationCenter
                    .observe(UIApplication.willEnterForegroundNotification) { [weak self] _ in
                        self?.lifeCycleObserver?.appWillForeground()
                    }
            case .appDidBackground:
                newObservers[event] =
                    notificationCenter
                    .observe(UIApplication.didEnterBackgroundNotification) { [weak self] _ in
                        self?.lifeCycleObserver?.appDidBackground()
                    }
            case .appDidBecomeActive:
                newObservers[event] =
                    notificationCenter
                    .observe(UIApplication.didBecomeActiveNotification) { [weak self] _ in
                        self?.lifeCycleObserver?.appDidBecomeActive()
                    }
            case .appWillResignActive:
                newObservers[event] =
                    notificationCenter
                    .observe(UIApplication.willResignActiveNotification) { [weak self] _ in
                        self?.lifeCycleObserver?.appWillResignActive()
                    }
            case .appTerminated:
                newObservers[event] =
                    notificationCenter
                    .observe(UIApplication.willTerminateNotification) { [weak self] _ in
                        self?.lifeCycleObserver?.appTerminated()
                    }
            case .userCredentialsUpdated:
                newObservers[event] =
                    notificationCenter
                    .observe(Notifications.UserCredentialsChanged) { [weak self] _ in
                        self?.lifeCycleObserver?.userCredentialsUpdated()
                    }
            case .userFollowingUpdated:
                newObservers[event] =
                    notificationCenter
                    .observe(Notifications.UserFollowingUpdated) { [weak self] _ in
                        self?.lifeCycleObserver?.userFollowingUpdated()
                    }
            }
        }

        observers = observers.merged(with: newObservers)
    }

    /// Deregister the given life cycle events
    ///
    /// Call this if you need to stop observing certain events during the lifetime of the object.
    /// There's no need to call this on `deinit` - when the `AppLifecycleObservation` instance deinits it will remove any registered observers.
    /// - Parameter events: Lifecycle events
    public func deregister(for events: LifeCycleEvent...) {
        events.forEach {
            if let observer = observers.removeValue(forKey: $0) {
                notificationCenter.removeObserver(observer)
            }
        }
    }

}

extension NotificationCenter {
    @discardableResult
    public func observe(
        _ name: NSNotification.Name?,
        object: Any? = nil,
        queue: OperationQueue? = nil,
        using block: @escaping (Notification) -> Void
    ) -> NSObjectProtocol {
        addObserver(forName: name, object: object, queue: queue, using: block)
    }
}

public struct Notifications {
    public static let ProductsUpdated = Notification.Name("ProductsUpdated")
    public static let UserCredentialsChanged = Notification.Name("UserCredentialsChanged")
    public static let UserProfileUpdated = Notification.Name("UserProfileUpdated")
    public static let UserFollowingUpdated = Notification.Name("UserFollowingUpdated")
    public static let DidRegisterUserNotificationSettings = Notification.Name(
        "didRegisterUserNotificationSettings"
    )
    public static let ReachabilityUpdated = Notification.Name("ReachabilityUpdated")
    public static let PodcastFileStatusChanged = Notification.Name("PodcastDownloadFinished")
    public static let DisplayPreferenceChanged = Notification.Name("DisplayPreferenceChanged")
    public static let ForceListenTabSelection = Notification.Name("ForceListenTabSelection")
    public static let WillHideTabBar = Notification.Name("WillHideTabBar")
    public static let DidUnhideTabBar = Notification.Name("WillUnhideTabBar")
    public static let SavedArticleUpdated = Notification.Name("SavedArticleUpdated")
    public static let ReadArticleUpdated = Notification.Name("ReadArticleUpdated")
}
