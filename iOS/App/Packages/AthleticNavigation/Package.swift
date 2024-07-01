// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticNavigation",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(name: "AthleticNavigation", targets: ["AthleticNavigation"])
    ],
    dependencies: [
        .package(name: "AthleticAnalytics", path: "../AthleticAnalytics"),
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticApolloTypes", path: "../AthleticApolloTypes"),
        .package(name: "AthleticTestUtils", path: "../AthleticTestUtils"),
        .package(name: "AthleticUI", path: "../AthleticUI"),
    ],
    targets: [
        .target(
            name: "AthleticNavigation",
            dependencies: [
                .product(name: "AthleticApolloTypes", package: "AthleticApolloTypes"),
                .product(name: "AthleticAnalytics", package: "AthleticAnalytics"),
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "AthleticUI", package: "AthleticUI"),
            ]
        ),
        .testTarget(
            name: "AthleticNavigationTests",
            dependencies: [
                "AthleticNavigation",
                "AthleticTestUtils",
            ]
        ),
    ]
)
