// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticBrackets",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(name: "AthleticBrackets", targets: ["AthleticBrackets"])
    ],
    dependencies: [
        .package(name: "AthleticAnalytics", path: "../AthleticAnalytics"),
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticApolloNetworking", path: "../AthleticApolloNetworking"),
        .package(name: "AthleticApolloTypes", path: "../AthleticApolloTypes"),
        .package(name: "AthleticTestUtils", path: "../AthleticTestUtils"),
        .package(name: "AthleticUI", path: "../AthleticUI"),
        .package(name: "AthleticScoresFoundation", path: "../AthleticScoresFoundation"),
    ],
    targets: [
        .target(
            name: "AthleticBrackets",
            dependencies: [
                .product(name: "AthleticAnalytics", package: "AthleticAnalytics"),
                .product(name: "AthleticApolloNetworking", package: "AthleticApolloNetworking"),
                .product(name: "AthleticApolloTypes", package: "AthleticApolloTypes"),
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "AthleticUI", package: "AthleticUI"),
                .product(name: "AthleticScoresFoundation", package: "AthleticScoresFoundation"),
            ],
            exclude: ["GQL/"]
        ),
        .testTarget(
            name: "AthleticBracketsTests",
            dependencies: [
                "AthleticBrackets",
                "AthleticTestUtils",
            ]
        ),
    ]
)
