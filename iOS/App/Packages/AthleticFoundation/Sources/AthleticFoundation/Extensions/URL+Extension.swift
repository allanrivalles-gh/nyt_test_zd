//
//  URL+Extension.swift
//  theathletic-ios
//
//  Created by Jan Remes on 10/09/2018.
//  Copyright © 2018 The Athletic. All rights reserved.
//

import Foundation
import UIKit

extension URL {

    public enum CdnScaleMode: String {
        /// Similar to contain, but the image is never enlarged. If the image is larger than given width or height, it will be resized. Otherwise its original size will be kept.
        case scaleDown = "scale-down"

        /// Image will be resized (shrunk or enlarged) to be as large as possible within the given width or height while preserving the aspect ratio. If you only provide a single dimension (for example, only width), the image will be shrunk or enlarged to exactly match that dimension.
        case contain = "contain"

        /// Resizes (shrinks or enlarges) to fill the entire area of width and height. If the image has an aspect ratio different from the ratio of width and height, it will be cropped to fit.
        case cover = "cover"

        /// Image will be shrunk and cropped to fit within the area specified by width and height. The image will not be enlarged. For images smaller than the given dimensions, it is the same as scale-down. For images larger than the given dimensions, it is the same as cover.
        case crop = "crop"
    }

    public var queryItems: [String: String] {

        if let comps = URLComponents(url: self, resolvingAgainstBaseURL: false),
            let queryItems = comps.queryItems
        {
            var dict: [String: String] = [:]

            for item in queryItems {
                dict[item.name] = item.value ?? ""
            }

            return dict
        }

        return [:]
    }

    public func append(parameters: [String: String]) -> URL {
        guard var comps = URLComponents(url: self, resolvingAgainstBaseURL: false) else {
            return self
        }

        var queryItems = comps.queryItems ?? []

        for (key, value) in parameters {
            queryItems.append(URLQueryItem(name: key, value: value))
        }

        comps.queryItems = queryItems
        return comps.url ?? self
    }

    public func replacingEmptyScheme(withScheme replacementScheme: String = "https") -> URL {
        guard
            let components = URLComponents(url: self, resolvingAgainstBaseURL: false),
            components.scheme == nil
        else {
            return self
        }

        /// URLs that do not have a scheme are parsed as an opaque path without a host. When they're reconstructed from the
        /// components without the host they're missing the slashes following the scheme. These aren't valid and aren't handled as
        /// having valid schemes by the universal link parser. Therefore to get a valid URL we use string concatenation rather than the
        /// components' constructor.
        return URL(string: "\(replacementScheme)://\(absoluteString)") ?? self
    }

    public init?(string: String?) {
        self.init(string: string ?? "")
    }

    /// Constructs an image URL to request the current image URL at the given width or height.
    /// At least one dimention MUST be provided. If both width AND height are provided, the CDN fits the image into the provided
    /// bounds while maintaining the original aspect ratio.
    ///
    /// NB: This should only be called on the original URL, not a `cdn-cgi` CloudFront variation of the URL.
    ///
    /// - Parameters:
    ///   - width: Preferred width in pixels of the image. NB: The image may not be served at the requested width.
    ///   - height: Preferred height in pixels of the image. NB: The image may not be served at the requested height.
    /// - Returns: URL to download the image
    public func cdnImageUrl(
        pixelWidth width: Int? = nil,
        pixelHeight height: Int? = nil,
        scaleMode: CdnScaleMode? = nil
    ) -> URL {
        guard
            var components = URLComponents(url: self, resolvingAgainstBaseURL: false),
            host?
                .components(separatedBy: ".")
                .suffix(2)
                .joined(separator: ".") == Global.General.athleticDomainString
        else {
            return self
        }

        guard !components.path.starts(with: "/cdn-cgi/") else {
            assertionFailure("A url was passed in that was already cdn'ified.")
            return self
        }

        guard width != nil || height != nil else {
            assertionFailure("At least one dimension is required, both width and height were `nil`")
            return self
        }

        let resizeParameters: [String] = [
            width.map { "width=\($0)" },
            height.map { "height=\($0)" },
            scaleMode.map { "fit=\($0.rawValue)" },
        ]
        .compactMap { $0 }

        let dimensionsString = resizeParameters.joined(separator: ",")
        components.path = "/cdn-cgi/image/quality=100,\(dimensionsString)" + components.path
        return components.url ?? self
    }

    public func cdnImageUrl(
        pointWidth width: CGFloat,
        pointHeight height: CGFloat,
        screenScale: CGFloat = UIScreen.main.scale,
        scaleMode: CdnScaleMode? = nil
    ) -> URL {
        cdnImageUrl(
            pixelWidth: Int(width * screenScale),
            pixelHeight: Int(height * screenScale),
            scaleMode: scaleMode
        )
    }

    public func cdnImageUrl(
        pointWidth width: CGFloat,
        screenScale: CGFloat = UIScreen.main.scale,
        scaleMode: CdnScaleMode? = nil
    ) -> URL {
        cdnImageUrl(pixelWidth: Int(width * screenScale), scaleMode: scaleMode)
    }

    public func cdnImageUrl(
        pointHeight height: CGFloat,
        screenScale: CGFloat = UIScreen.main.scale,
        scaleMode: CdnScaleMode? = nil
    ) -> URL {
        cdnImageUrl(pixelHeight: Int(height * screenScale), scaleMode: scaleMode)
    }

    public var canOpenInSafari: Bool {
        guard let scheme = scheme?.lowercased() else {
            return false
        }

        return scheme == "http" || scheme == "https"
    }
}

extension URL: Identifiable {
    public var id: String {
        absoluteString
    }
}
