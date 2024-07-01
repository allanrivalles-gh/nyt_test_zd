//
//  TeamLogo+Size.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 31/8/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticApolloTypes
import UIKit

public struct ATHImageResource: Hashable, Codable {
    public let id: String
    public let url: URL?
    public let width: Int
    public let height: Int

    public init(id: String, url: URL? = nil, width: Int, height: Int) {
        self.id = id
        self.url = url
        self.width = width
        self.height = height
    }

    public init(entity: GQL.TeamLogo) {
        id = entity.id
        url = URL(string: entity.uri)
        width = entity.width
        height = entity.height
    }

    public init(entity: GQL.PlayerHeadshot) {
        id = entity.id
        url = URL(string: entity.uri)
        width = entity.width
        height = entity.height
    }

    public init(url: URL) {
        self.id = url.absoluteString
        self.url = url
        self.width = 0
        self.height = 0
    }
}

extension Array where Element == ATHImageResource {

    /// The most appropriate image URL to use for this given point size
    /// NB: This is the size in points, not pixels. The best URL will be returned based on the device pixel density.
    ///
    /// If there's a perfect match it will return that size, otherwise it will return the next best that is larger than the requested size.
    /// Failing that it will return the largest one that is smaller than the requested size.
    /// - Parameters:
    ///   - size: The preferred logo size in points
    ///   - screenScale: Screen scale, defaults to the main screen scale
    /// - Returns: The most appropriate logo URL to use
    public func bestUrl(for size: CGSize, screenScale: CGFloat = UIScreen.main.scale) -> URL? {
        bestUri(
            for: size,
            screenScale: screenScale
        )
    }

    /// The most appropriate image URL to use for a square logo with the provided dimension.
    /// NB: This is the size in points, not pixels. The best URL will be returned based on the device pixel density.
    ///
    /// If there's a perfect match it will return that size, otherwise it will return the next best that is larger than the requested size.
    /// Failing that it will return the largest one that is smaller than the requested size.
    /// - Parameters:
    ///   - size: The preferred logo size in points
    ///   - screenScale: Screen scale, defaults to the main screen scale
    /// - Returns: The most appropriate logo URL to use
    public func bestUrl(
        forSquareSize dimension: CGFloat,
        screenScale: CGFloat = UIScreen.main.scale
    )
        -> URL?
    {
        bestUri(
            for: CGSize(width: dimension, height: dimension),
            screenScale: screenScale
        )
    }

    private func bestUri(for size: CGSize, screenScale: CGFloat) -> URL? {
        let size = size.pixelSize(screenScale: screenScale)

        if let downscaledUri = bestUri(forPixelSize: size, scaling: .downscale) {
            return downscaledUri

        } else if let upscaledUri = bestUri(forPixelSize: size, scaling: .upscale) {
            return upscaledUri

        } else {
            return first?.url
        }
    }

    private func bestUri(
        forPixelSize targetSize: PixelSize,
        scaling: Scaling
    ) -> URL? {
        var best: (logo: Element, size: PixelSize)?
        for logo in self {
            guard logo.isPossibleMatch(forTargetSize: targetSize, scaling: scaling) else {
                continue
            }
            let pixelSize = PixelSize(width: logo.width, height: logo.height)

            /// If it's a perfect match for at least one axis we're done, stop searching
            if pixelSize.width == targetSize.width || pixelSize.height == targetSize.height {
                best = (logo: logo, size: pixelSize)
                break
            }

            /// If it's the first one, it's the best so far
            guard let currentBest = best else {
                best = (logo: logo, size: pixelSize)
                continue
            }

            /// If it's a better fit for the target size than the previous best then it's a better match
            if pixelSize.isBetterThan(currentBest.size, forScaling: scaling) {
                best = (logo: logo, size: pixelSize)
            }
        }

        return best?.logo.url
    }
}

private enum Scaling {
    case downscale
    case upscale
}

extension ATHImageResource {

    fileprivate func isPossibleMatch(
        forTargetSize targetSize: PixelSize,
        scaling: Scaling
    ) -> Bool {
        switch scaling {
        case .downscale:
            /// If at least one axis is big enough then it is a possible match for downscaling assuming "Aspect Fit" mode is used.
            return width >= targetSize.width || height >= targetSize.height

        case .upscale:
            /// It is suitable for upscaling if both dimensions are smaller or equal to the required target size
            return width <= targetSize.width && height <= targetSize.height
        }
    }
}

private struct PixelSize: Equatable {
    let width: Int
    let height: Int

    /// The surface area of this size
    var area: Int {
        width * height
    }

    /// Note, assumes prior logic has already verified it is at least big/small enough for target size with the given scaling mode.
    /// Considered to be better if it comes closer to filling the bounds than the `other`. We could be a bit smarter about this by
    /// considering aspect ratio too, but just comparing the area keeps things simple.
    func isBetterThan(_ other: Self, forScaling scaling: Scaling) -> Bool {
        switch scaling {
        case .downscale:
            return area < other.area
        case .upscale:
            return area > other.area
        }
    }
}

extension CGSize {

    fileprivate func pixelSize(screenScale: CGFloat) -> PixelSize {
        return .init(width: Int(width * screenScale), height: Int(height * screenScale))
    }
}
