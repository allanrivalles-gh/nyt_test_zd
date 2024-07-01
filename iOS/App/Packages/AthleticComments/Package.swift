// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticComments",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(name: "AthleticComments", targets: ["AthleticComments"])
    ],
    dependencies: [
        .package(name: "AthleticAnalytics", path: "../AthleticAnalytics"),
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticApolloNetworking", path: "../AthleticApolloNetworking"),
        .package(name: "AthleticApolloTypes", path: "../AthleticApolloTypes"),
        .package(name: "AthleticTestUtils", path: "../AthleticTestUtils"),
        .package(name: "AthleticUI", path: "../AthleticUI"),
    ],
    targets: [
        .target(
            name: "AthleticComments",
            dependencies: [
                .product(name: "AthleticAnalytics", package: "AthleticAnalytics"),
                .product(name: "AthleticApolloNetworking", package: "AthleticApolloNetworking"),
                .product(name: "AthleticApolloTypes", package: "AthleticApolloTypes"),
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "AthleticUI", package: "AthleticUI"),
            ],
            exclude: ["GQL/"]
        ),
        .testTarget(
            name: "AthleticCommentsTests",
            dependencies: [
                "AthleticComments",
                "AthleticTestUtils",
            ]
        ),
    ]
)
