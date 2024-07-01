// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticAnalytics",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(
            name: "AthleticAnalytics",
            targets: ["AthleticAnalytics"]
        )
    ],
    dependencies: [
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticUI", path: "../AthleticUI"),
    ],
    targets: [
        .target(
            name: "AthleticAnalytics",
            dependencies: [
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "AthleticUI", package: "AthleticUI"),
            ]
        ),
        .testTarget(
            name: "AthleticAnalyticsTests",
            dependencies: ["AthleticAnalytics"]
        ),
    ]
)
