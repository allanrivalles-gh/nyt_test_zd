import Combine
import Foundation

/// Execute the given closure on the main thread.
///
/// If already on the main thread the code is executed syncronously, otherwise it is dispatched async to the main thread.
/// This is useful for executing code on the UI thread without unnecessarily dispatching asyncronously to the next run loop.
/// - Parameter closure: Closure to execute on the main thread
public func onMain(_ closure: @escaping VoidClosure) {
    if Thread.isMainThread {
        closure()
    } else {
        DispatchQueue.main.async(execute: closure)
    }
}

public typealias VoidClosure = () -> Void
public typealias VoidCompletion = (VoidResult) -> Void
public typealias CompletionResult<T> = (Result<T, Error>) -> Void
public typealias Cancellables = Set<AnyCancellable>

public enum VoidResult {
    case success
    case failure(Error)
}

// MARK: - Notifications
public func addNotificationObserverWithObject(
    _ observer: AnyObject,
    selector: Selector,
    name: Notification.Name,
    object: AnyObject? = nil
) {
    NotificationCenter.default.addObserver(observer, selector: selector, name: name, object: object)
}

public func removeNotificationObserver(
    _ observer: AnyObject,
    name: NSNotification.Name? = nil,
    object: AnyObject? = nil
) {
    NotificationCenter.default.removeObserver(observer, name: name, object: object)
}

public func postNotification(
    _ name: Notification.Name,
    object: AnyObject? = nil,
    info: [AnyHashable: Any]? = nil
) {
    NotificationCenter.default.post(name: name, object: object, userInfo: info)
}
