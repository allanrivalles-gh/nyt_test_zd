// swift-tools-version:5.7

import PackageDescription

let package = Package(
    name: "AthleticStorage",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(name: "AthleticStorage", targets: ["AthleticStorage"])
    ],
    dependencies: [],
    targets: [
        .target(name: "AthleticStorage", dependencies: []),
        .testTarget(
            name: "AthleticStorageTests",
            dependencies: [
                .target(name: "AthleticStorage")
            ]
        ),
    ]
)
