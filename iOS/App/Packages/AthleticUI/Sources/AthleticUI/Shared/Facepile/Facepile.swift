//
//  Facepile.swift
//
//  Created by Mark Corbyn on 11/1/2024.
//

import AthleticFoundation
import SwiftUI

public struct Facepile: View {

    private let imageUrls: [URL]
    private let avatarSize: CGFloat
    private let strokeWidth: CGFloat
    private let strokeColor: Color
    private let shouldStrokeSingleAvatar: Bool

    public init(
        imageUrls: [URL],
        avatarSize: CGFloat = 24,
        strokeWidth: CGFloat = 2,
        strokeColor: Color = .chalk.dark300,
        shouldStrokeSingleAvatar: Bool = false
    ) {
        self.imageUrls = imageUrls
        self.avatarSize = avatarSize
        self.strokeWidth = strokeWidth
        self.strokeColor = strokeColor
        self.shouldStrokeSingleAvatar = shouldStrokeSingleAvatar
    }

    public var body: some View {
        HStack(spacing: 0) {
            let _ = DuplicateIDLogger.logDuplicates(in: imageUrls, id: \.self)
            ForEach(imageUrls, id: \.self) { url in
                PlaceholderLazyImage(
                    imageUrl: url.cdnImageUrl(
                        pointWidth: avatarSize,
                        pointHeight: avatarSize,
                        scaleMode: .crop
                    ),
                    modifyImage: {
                        $0.aspectRatio(contentMode: .fit)
                    }
                )
                .frame(width: avatarSize, height: avatarSize)
                .cornerRadius(avatarSize / 2)
                .overlay(
                    Circle().stroke(
                        strokeColor,
                        lineWidth:
                            imageUrls.count > 1 || shouldStrokeSingleAvatar
                            ? strokeWidth
                            : 0
                    )
                )
                /// if multiple authors, place each avatar slightly under the previous one
                .padding(
                    [.leading],
                    url == imageUrls.first ? 0 : -8
                )
                .zIndex(Double(imageUrls.count - (imageUrls.firstIndex(of: url) ?? 0)))
            }
        }
        .frame(height: avatarSize)
    }
}

#Preview {
    Facepile(
        imageUrls: [
            "https://cdn.theathletic.com/app/uploads/2018/09/21183443/Jones-Tony-Headshot-091418.jpg"
                .url!,

            "https://cdn.theathletic.com/app/uploads/2020/04/16232759/HS_Square_0060_20200227Kamrani-Chris0152_bw.jpg"
                .url!,
        ])
}
