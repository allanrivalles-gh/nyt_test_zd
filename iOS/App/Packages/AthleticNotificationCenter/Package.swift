// swift-tools-version: 5.8
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticNotificationCenter",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(name: "AthleticNotificationCenter", targets: ["AthleticNotificationCenter"])
    ],
    dependencies: [
        .package(name: "AthleticAnalytics", path: "../AthleticAnalytics"),
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticApolloNetworking", path: "../AthleticApolloNetworking"),
        .package(name: "AthleticApolloTypes", path: "../AthleticApolloTypes"),
        .package(name: "AthleticUI", path: "../AthleticUI"),
        .package(name: "AthleticNavigation", path: "../AthleticNavigation"),
        .package(name: "AthleticTestUtils", path: "../AthleticTestUtils"),
    ],
    targets: [
        .target(
            name: "AthleticNotificationCenter",
            dependencies: [
                .product(name: "AthleticAnalytics", package: "AthleticAnalytics"),
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "AthleticApolloNetworking", package: "AthleticApolloNetworking"),
                .product(name: "AthleticApolloTypes", package: "AthleticApolloTypes"),
                .product(name: "AthleticUI", package: "AthleticUI"),
                .product(name: "AthleticNavigation", package: "AthleticNavigation"),
            ],
            exclude: ["GQL/"]
        ),
        .testTarget(
            name: "AthleticNotificationCenterTests",
            dependencies: [
                "AthleticNotificationCenter",
                "AthleticTestUtils",
            ]
        ),
    ]
)
