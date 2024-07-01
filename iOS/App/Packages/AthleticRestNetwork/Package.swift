// swift-tools-version:5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let sharedDeps: [Target.Dependency] = [
    .product(name: "AthleticFoundation", package: "AthleticFoundation"),
    .product(name: "Logging", package: "swift-log"),
]

let package = Package(
    name: "AthleticRestNetwork",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .library(name: "AthleticRestNetwork", targets: ["AthleticRestNetwork"])
    ],
    dependencies: [
        .package(url: "https://github.com/apple/swift-log.git", from: "1.0.0"),
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
    ],
    targets: [
        .target(name: "AthleticRestNetwork", dependencies: sharedDeps)
    ]
)
