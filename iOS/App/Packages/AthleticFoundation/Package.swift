// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticFoundation",
    defaultLocalization: "en",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        // Products define the executables and libraries a package produces, and make them visible to other packages.
        .library(name: "AthleticFoundation", targets: ["AthleticFoundation"])
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        // .package(url: /* package url */, from: "1.0.0"),
        .package(name: "AthleticTestUtils", path: "../AthleticTestUtils"),
        .package(
            url: "https://github.com/Datadog/dd-sdk-ios.git",
            .upToNextMajor(from: "1.12.1")
        ),
        .package(
            url: "https://github.com/evgenyneu/keychain-swift.git",
            .upToNextMajor(from: "17.0.0")
        ),
        .package(name: "AthleticStorage", path: "../AthleticStorage"),
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .target(
            name: "AthleticFoundation",
            dependencies: [
                .product(name: "Datadog", package: "dd-sdk-ios"),
                .product(name: "KeychainSwift", package: "keychain-swift"),
                .product(name: "AthleticStorage", package: "AthleticStorage"),
            ]
        ),
        .testTarget(
            name: "AthleticFoundationTests",
            dependencies: [
                "AthleticFoundation",
                "AthleticTestUtils",
            ]
        ),
    ]
)
