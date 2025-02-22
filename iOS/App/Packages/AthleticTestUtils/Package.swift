// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticTestUtils",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(
            name: "AthleticTestUtils",
            targets: ["AthleticTestUtils"]
        )
    ],
    dependencies: [],
    targets: [
        .target(
            name: "AthleticTestUtils",
            dependencies: []
        ),
        .testTarget(
            name: "AthleticTestUtilsTests",
            dependencies: ["AthleticTestUtils"]
        ),
    ]
)
