// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticUI",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "AthleticUI",
            targets: ["AthleticUI"]
        )
    ],
    dependencies: [
        .package(name: "AthleticApolloTypes", path: "../AthleticApolloTypes"),
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticTestUtils", path: "../AthleticTestUtils"),
        .package(url: "https://github.com/kean/Nuke.git", from: "11.0.0"),
        .package(url: "https://github.com/apple/swift-algorithms", from: "1.0.0"),
    ],
    targets: [
        .target(
            name: "AthleticUI",
            dependencies: [
                .product(name: "Algorithms", package: "swift-algorithms"),
                .product(name: "AthleticApolloTypes", package: "AthleticApolloTypes"),
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "Nuke", package: "Nuke"),
                .product(name: "NukeExtensions", package: "Nuke"),
                .product(name: "NukeUI", package: "Nuke"),
            ],
            resources: [
                .process("Resources")
            ]
        ),
        .testTarget(
            name: "AthleticUITests",
            dependencies: [
                "AthleticUI",
                "AthleticTestUtils",
            ]
        ),
    ]
)
