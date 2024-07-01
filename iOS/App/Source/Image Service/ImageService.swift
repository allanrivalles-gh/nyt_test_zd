//
//  ImageService.swift
//  iOS-StartingFive
//
//  Created by Jan Remes on 04.12.15.
//  Copyright © 2015 STRV. All rights reserved.
//

import AthleticFoundation
import Foundation
import Nuke
import NukeExtensions
import UIKit

enum TeamImageSize {
    case small
    case medium
    case unchanged
}

extension UIImageView {

    func ath_setImage(
        with url: URL?,
        size: CGSize? = nil,
        placeholder: UIImage? = nil,
        processors: [ImageProcessing] = [],
        completed: ((Result<ImageResponse, ImagePipeline.Error>) -> Void)? = nil
    ) {
        guard let url = url else {
            image = placeholder
            return
        }

        let request = ImageRequest(
            url: url,
            processors: processors,
            priority: .high
        )

        let options = ImageLoadingOptions(
            placeholder: placeholder
        )

        NukeExtensions.loadImage(
            with: request,
            options: options,
            into: self,
            completion: completed
        )

        accessibilityIgnoresInvertColors = true
    }
}

struct ImageService {
    private static let prefetcher = ImagePrefetcher()
    static func preheatImages(urls: [URL]) {
        let requests = urls.map { url in
            // Build a request that generates the grayscale version of the image as well
            // and cache it.
            return ImageRequest(
                url: url,
                processors: [
                    ImageProcessors.CoreImageFilter(name: "CIPhotoEffectMono")
                ],
                priority: .veryHigh
            )
        }
        ImageService.prefetcher.startPrefetching(with: requests)
    }

    static func preheatImages(urls: [URL?]) {
        preheatImages(urls: urls.compactMap { $0 })
    }

    static func preheatImages(urls: [String]) {
        ImageService.preheatImages(urls: urls.compactMap { URL(string: $0) })
    }

    static func preheatImage(url: URL?) {
        guard let url = url else { return }

        preheatImages(urls: [url])
    }

    static func teamImageUrl(teamId: Any) -> URL {
        return URL(string: "\(Global.teamLogosUrl)team-logo-\(teamId)-300x300.png")!
    }

    static func teamImageMediumUrl(teamId: Any) -> URL {
        return URL(string: "\(Global.teamLogosUrl)team-logo-\(teamId)-100x100.png")!
    }

    static func teamImageSmallUrl(teamId: Any) -> URL {
        return URL(string: "\(Global.teamLogosUrl)team-logo-\(teamId)-50x50.png")!
    }

    static func leagueColorImageUrl(leagueId: Any) -> URL? {
        return URL(
            string:
                "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-\(leagueId)-color@2x.png"
        )
    }

    static func leagueColorSmallImageUrl(leagueId: Any) -> URL? {
        return URL(
            string:
                "https://s3-us-west-2.amazonaws.com/theathletic-league-logos/league-\(leagueId)-color.png"
        )
    }

    static func getTeamImage(
        _ teamId: Any?,
        imageSize: TeamImageSize = .unchanged,
        completion: ((_ image: UIImage?) -> Void)?
    ) {

        guard let teamId = teamId else {
            return
        }

        if let image = UIImage(named: "team-logo-\(teamId)") {
            completion?(image)
            return
        }

        let url: URL

        switch imageSize {
        case .small:
            url = ImageService.teamImageSmallUrl(teamId: teamId)
        case .medium:
            url = ImageService.teamImageMediumUrl(teamId: teamId)
        case .unchanged:
            url = ImageService.teamImageUrl(teamId: teamId)
        }

        ImageService.downloadImageWithUrl(url) { result in
            switch result {
            case .success(let image):
                completion?(image)
            case .failure:
                completion?(nil)
            }
        }
    }

    static func downloadImageWithUrl(
        _ imageUrl: URL?,
        with size: CGSize? = nil,
        completion: CompletionResult<UIImage>? = nil
    ) {
        guard let url = imageUrl else {
            return
        }

        var processors: [ImageProcessing] = []
        if let resizeToSize = size {
            processors = [ImageProcessors.Resize(size: resizeToSize)]
        }
        let request = ImageRequest(
            url: url,
            processors: processors,
            priority: .high
        )
        ImagePipeline.shared.loadImage(
            with: request,
            completion: { result in
                switch result {
                case .success(let value):
                    completion?(.success(value.image))
                case .failure(let error):
                    ATHLogger(category: .network).error(
                        "\(String(describing: url.absoluteString))  \(String(describing: error))"
                    )
                    completion?(.failure(error))
                }
            }
        )
    }

    static func downloadImage(with url: URL) async throws -> UIImage {
        try await withCheckedThrowingContinuation { continuation in
            downloadImageWithUrl(url) { result in
                continuation.resume(with: result)
            }
        }
    }
}
