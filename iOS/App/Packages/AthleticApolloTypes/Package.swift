// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AthleticApolloTypes",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .executable(name: "ApolloCodegen", targets: ["ApolloCodegen"]),
        .library(name: "AthleticApolloTypes", targets: ["AthleticApolloTypes"]),
    ],
    dependencies: [
        .package(
            url: "https://github.com/apollographql/apollo-ios.git",
            .upToNextMajor(from: "0.51.2")
        ),
        .package(
            url: "https://github.com/apple/swift-argument-parser.git",
            .upToNextMinor(from: "0.3.0")
        ),
    ],
    targets: [
        .executableTarget(
            name: "ApolloCodegen",
            dependencies: [
                .product(name: "ApolloCodegenLib", package: "apollo-ios"),
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
            ]
        ),
        .target(
            name: "AthleticApolloTypes",
            dependencies: [
                .product(name: "Apollo", package: "apollo-ios")
            ],
            exclude: [
                "Generated/operationIDs.json",
                "Fragments/",
                "Operations/",
            ]
        ),
    ]
)
