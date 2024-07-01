//
//  PlaceholderLazyImage.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 7/10/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation
import NukeUI
import SwiftUI

/// Lazily loads an image URL while displaying a placeholder view during loading and upon failure.
public struct PlaceholderLazyImage<
    LoadingPlaceholder: View,
    FailedPlaceholder: View,
    ModifiedImage: View
>:
    View
{

    /// Image URL to load
    public let imageUrl: URL?

    /// Placeholder view to display during loading
    @ViewBuilder public let loading: () -> LoadingPlaceholder

    /// Placeholder view to display upon failure
    @ViewBuilder public let failed: () -> FailedPlaceholder

    /// Called when the image is loaded either from network or cache, inputting a SwiftUI image.
    public var onLoadImage: ((SwiftUI.Image) -> Void)?

    /// Modify the loaded image prior to displaying it
    @ViewBuilder public let modifyImage: (SwiftUI.Image) -> ModifiedImage

    public init(
        imageUrl: URL?,
        @ViewBuilder loading: @escaping () -> LoadingPlaceholder,
        @ViewBuilder failed: @escaping () -> FailedPlaceholder,
        onLoadImage: ((SwiftUI.Image) -> Void)? = nil,
        modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) {
        self.imageUrl = imageUrl
        self.loading = loading
        self.failed = failed
        self.onLoadImage = onLoadImage
        self.modifyImage = modifyImage
    }

    public var body: some View {
        if let container = ImagePipeline.shared.cache.cachedImage(for: ImageRequest(url: imageUrl))
        {
            if let onLoadImage = onLoadImage {
                let _ = onLoadImage(SwiftUI.Image(uiImage: container.image).resizable())
            }
            modifyImage(
                SwiftUI.Image(uiImage: container.image).resizable()
            )
        } else {
            LazyImage(url: imageUrl) { state in
                if let image = state.imageContainer?.image {
                    if let onLoadImage = onLoadImage {
                        let _ = onLoadImage(SwiftUI.Image(uiImage: image))
                    }
                    modifyImage(
                        SwiftUI.Image(uiImage: image).resizable()
                    )
                } else if state.error != nil {
                    failed()
                } else {
                    loading()
                }
            }
        }
    }
}

// MARK: - Convenience Initializers
extension PlaceholderLazyImage {

    /// A convenience initializer with no post-processing of the image
    public init(
        imageUrl: URL?,
        @ViewBuilder loading: @escaping () -> LoadingPlaceholder,
        @ViewBuilder failed: @escaping () -> FailedPlaceholder
    ) where ModifiedImage == SwiftUI.Image {
        self.init(
            imageUrl: imageUrl,
            loading: loading,
            failed: failed,
            modifyImage: { $0 }
        )
    }

    /// A convenience initializer, with no placeholder view during loading/failure and no post processing of the image.
    public init(imageUrl: URL?)
    where
        LoadingPlaceholder == EmptyView,
        FailedPlaceholder == EmptyView,
        ModifiedImage == SwiftUI.Image
    {
        self.init(
            imageUrl: imageUrl,
            loading: { EmptyView() },
            failed: { EmptyView() },
            modifyImage: { $0 }
        )
    }

    /// A convenience initializer, with no placeholder view during loading/failure.
    public init(
        imageUrl: URL?,
        @ViewBuilder modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) where LoadingPlaceholder == EmptyView, FailedPlaceholder == EmptyView {
        self.init(
            imageUrl: imageUrl,
            loading: { EmptyView() },
            failed: { EmptyView() },
            modifyImage: modifyImage
        )
    }

    /// A convenience initializer, using the same placeholder for loading and failed state, and upon success displaying the image as
    /// received without additional image processing.
    public init(
        imageUrl: URL?,
        @ViewBuilder placeholder: @escaping () -> LoadingPlaceholder
    ) where ModifiedImage == SwiftUI.Image, FailedPlaceholder == LoadingPlaceholder {
        self.init(
            imageUrl: imageUrl,
            loading: placeholder,
            failed: placeholder,
            modifyImage: { $0 }
        )
    }

    /// A convenience initializer, using the same placeholder for loading and failed state.
    public init(
        imageUrl: URL?,
        @ViewBuilder placeholder: @escaping () -> LoadingPlaceholder,
        @ViewBuilder modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) where FailedPlaceholder == LoadingPlaceholder {
        self.init(
            imageUrl: imageUrl,
            loading: placeholder,
            failed: placeholder,
            modifyImage: modifyImage
        )
    }

    public init(
        imageUrl: URL?,
        allPlaceholder: @autoclosure @escaping () -> LoadingPlaceholder
    ) where LoadingPlaceholder == FailedPlaceholder, ModifiedImage == SwiftUI.Image {
        self.init(
            imageUrl: imageUrl,
            loading: allPlaceholder,
            failed: allPlaceholder,
            modifyImage: { $0 }
        )
    }
}

public struct HeadshotLazyImage<Content: View>: View {
    public let size: CGFloat
    public let resources: [ATHImageResource]
    public var contentMode: ContentMode = .fit
    public var backgroundColor: Color? = nil
    public var placeHolderView: (() -> Content)?

    @Environment(\.colorScheme) private var colorScheme

    public init(
        size: CGFloat,
        resources: [ATHImageResource],
        contentMode: ContentMode = .fit,
        backgroundColor: Color? = nil,
        placeholderView: (() -> Content)?
    ) {
        self.size = size
        self.resources = resources
        self.contentMode = contentMode
        self.backgroundColor = backgroundColor
        self.placeHolderView = placeholderView
    }

    public var body: some View {
        SizedLazyImage(
            dimension: size,
            resources: resources,
            placeholder: {
                if let placeHolderView {
                    placeHolderView()
                } else {
                    Image.playerHeadshotPlaceholder(ofSize: size)
                }
            },
            modifyImage: { image in
                image.aspectRatio(contentMode: contentMode)
            }
        )
        .background(
            backgroundColor ?? defaultBackgroundColor
        )
        .clipShape(Circle())
    }

    private var defaultBackgroundColor: Color {
        Color(dark: .chalk.constant.gray300, light: .chalk.constant.gray500)
    }
}

extension HeadshotLazyImage {
    /// init to handle nil generic placeholderView
    public init(
        size: CGFloat,
        resources: [ATHImageResource],
        contentMode: ContentMode = .fit,
        backgroundColor: Color? = nil
    ) where Content == EmptyView {
        self.init(
            size: size,
            resources: resources,
            contentMode: contentMode,
            backgroundColor: backgroundColor,
            placeholderView: nil
        )
    }
}

public struct TeamLogoLazyImage: View {
    public let size: CGFloat
    public let resources: [ATHImageResource]
    public var contentMode: ContentMode

    public var body: some View {
        SizedLazyImage(
            dimension: size,
            resources: resources,
            placeholder: {
                Image.teamLogoPlaceholder(ofSize: size)
            },
            modifyImage: {
                $0.aspectRatio(contentMode: contentMode)
            }
        )
    }

    public init(size: CGFloat, resources: [ATHImageResource], contentMode: ContentMode = .fit) {
        self.size = size
        self.resources = resources
        self.contentMode = contentMode
    }
}

public struct SizedLazyImage<
    LoadingPlaceholder: View,
    FailedPlaceholder: View,
    ModifiedImage: View
>: View {
    public let size: CGSize
    public let resources: [ATHImageResource]
    public var alignment: Alignment

    @ViewBuilder public var loading: () -> LoadingPlaceholder
    @ViewBuilder public var failed: () -> FailedPlaceholder
    @ViewBuilder public var modifyImage: (SwiftUI.Image) -> ModifiedImage

    public init(
        size: CGSize,
        resources: [ATHImageResource],
        alignment: Alignment = .center,
        @ViewBuilder loading: @escaping () -> LoadingPlaceholder,
        @ViewBuilder failed: @escaping () -> FailedPlaceholder,
        @ViewBuilder modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) {
        self.size = size
        self.resources = resources
        self.alignment = alignment
        self.loading = loading
        self.failed = failed
        self.modifyImage = modifyImage
    }

    public init(
        dimension: CGFloat,
        resources: [ATHImageResource],
        alignment: Alignment = .center,
        @ViewBuilder loading: @escaping () -> LoadingPlaceholder,
        @ViewBuilder failed: @escaping () -> FailedPlaceholder,
        @ViewBuilder modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) {
        self.init(
            size: CGSize(width: dimension, height: dimension),
            resources: resources,
            alignment: alignment,
            loading: loading,
            failed: failed,
            modifyImage: modifyImage
        )
    }

    public var body: some View {
        PlaceholderLazyImage(
            imageUrl: resources.bestUrl(for: size),
            loading: loading,
            failed: failed,
            modifyImage: modifyImage
        )
        .frame(width: size.width, height: size.height, alignment: alignment)
    }
}

extension SizedLazyImage {

    /// A convenience initializer, with no placeholder view during loading/failure and no post processing of the image.
    public init(
        dimension: CGFloat,
        resources: [ATHImageResource]
    )
    where
        LoadingPlaceholder == EmptyView,
        FailedPlaceholder == EmptyView,
        ModifiedImage == SwiftUI.Image
    {
        self.init(
            dimension: dimension,
            resources: resources,
            loading: { EmptyView() },
            failed: { EmptyView() },
            modifyImage: { $0 }
        )
    }

    /// A convenience initializer, using the same placeholder for loading and failed state, and upon success displaying the image as
    /// received without additional image processing.
    public init(
        dimension: CGFloat,
        resources: [ATHImageResource],
        @ViewBuilder placeholder: @escaping () -> LoadingPlaceholder
    ) where ModifiedImage == SwiftUI.Image, FailedPlaceholder == LoadingPlaceholder {
        self.init(
            dimension: dimension,
            resources: resources,
            loading: placeholder,
            failed: placeholder,
            modifyImage: { $0 }
        )
    }

    /// A convenience initializer, using the same placeholder for loading and failed state.
    public init(
        dimension: CGFloat,
        resources: [ATHImageResource],
        @ViewBuilder placeholder: @escaping () -> LoadingPlaceholder,
        @ViewBuilder modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) where FailedPlaceholder == LoadingPlaceholder {
        self.init(
            dimension: dimension,
            resources: resources,
            loading: placeholder,
            failed: placeholder,
            modifyImage: modifyImage
        )
    }

    /// A convenience initializer, with no placeholder view during loading/failure and no post processing of the image.
    public init(
        dimension: CGFloat,
        resources: [ATHImageResource],
        @ViewBuilder modifyImage: @escaping (SwiftUI.Image) -> ModifiedImage
    ) where LoadingPlaceholder == EmptyView, FailedPlaceholder == EmptyView {
        self.init(
            dimension: dimension,
            resources: resources,
            loading: { EmptyView() },
            failed: { EmptyView() },
            modifyImage: modifyImage
        )
    }
}
