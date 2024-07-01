// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticApolloNetworking",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        // Products define the executables and libraries a package produces, and make them visible to other packages.
        .library(name: "AthleticApolloNetworking", targets: ["AthleticApolloNetworking"])
    ],
    dependencies: [
        .package(name: "AthleticFoundation", path: "../AthleticFoundation"),
        .package(name: "AthleticApolloTypes", path: "../AthleticApolloTypes"),
        .package(name: "AthleticRestNetwork", path: "../AthleticRestNetwork"),
        .package(
            url: "https://github.com/apollographql/apollo-ios.git",
            .upToNextMajor(from: "0.51.2")
        ),
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .target(
            name: "AthleticApolloNetworking",
            dependencies: [
                .product(name: "Apollo", package: "apollo-ios"),
                .product(name: "ApolloSQLite", package: "apollo-ios"),
                .product(name: "ApolloWebSocket", package: "apollo-ios"),
                .product(name: "AthleticFoundation", package: "AthleticFoundation"),
                .product(name: "AthleticApolloTypes", package: "AthleticApolloTypes"),
                .product(name: "AthleticRestNetwork", package: "AthleticRestNetwork"),
            ]
        ),
        .testTarget(
            name: "AthleticApolloNetworkingTests",
            dependencies: ["AthleticApolloNetworking"]
        ),
    ]
)
