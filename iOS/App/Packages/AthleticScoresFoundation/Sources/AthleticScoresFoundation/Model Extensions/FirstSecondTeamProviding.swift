//
//  FirstSecondTeamProviding.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 27/5/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import Foundation

// MARK: - Protocols for First / Second Teams

/// Provides the home and away fragments for this type, normalized to a shared fragment type.
///
/// GQL types often have a `homeTeam` and `awayTeam` prop, but these are returned as different Apollo class types
/// such as `GQL.GameV2.HomeTeam` and `GQL.GameV2.AwayTeam`.  This protocol is intended to expose those props as the
/// same type, via a fragment of the `AwayTeam` and `HomeTeam` types.
public protocol HomeAwayTeamFragmentProviding {
    associatedtype Fragment

    var homeFragment: Fragment? { get }
    var awayFragment: Fragment? { get }
}

/// Describes a type that can provide a first team and a second team.
public protocol FirstSecondTeamProviding: HomeAwayTeamFragmentProviding {
    func firstTeam(for sport: GQL.Sport) -> Fragment?
    func secondTeam(for sport: GQL.Sport) -> Fragment?
}

/// Default implementation which can return the first/second team if provided with a sport.
extension FirstSecondTeamProviding {

    /// Returns the first team (home or away) based on the sport provided.
    public func firstTeam(for sport: GQL.Sport) -> Fragment? {
        sport == .soccer ? homeFragment : awayFragment
    }

    /// Returns the second team (home or away) based on the sport provided.
    public func secondTeam(for sport: GQL.Sport) -> Fragment? {
        sport == .soccer ? awayFragment : homeFragment
    }
}

/// Describes a type that can indicate the sport it represents.
public protocol SportProviding {
    var sport: GQL.Sport { get }
}

/// Describes a type that can return a first and second team based on the sport it represents.
public protocol SportAwareFirstSecondTeamProviding: FirstSecondTeamProviding, SportProviding {}

/// Default implementation to return first/second team based on the sport type of the instance.
extension SportAwareFirstSecondTeamProviding {
    public var firstTeam: Fragment? { firstTeam(for: sport) }
    public var secondTeam: Fragment? { secondTeam(for: sport) }
}
