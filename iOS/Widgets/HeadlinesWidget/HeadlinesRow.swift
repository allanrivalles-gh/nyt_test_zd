//
//  HeadlinesRow.swift
//  HeadlinesWidgetExtension
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticUI
import SwiftUI
import WidgetKit

struct HeadlinesRow: View {
    let headline: Headline
    let style: AthleticFont.Style

    var body: some View {
        GeometryReader { proxy in
            HStack(alignment: .top, spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    Text(headline.title)
                        .kerning(0.5)
                        .multilineTextAlignment(.leading)
                        .lineLimit(3)
                        .fontStyle(style)
                    Spacer(minLength: 0)
                    TimeTagView(
                        tag: headline.tag,
                        date: headline.date,
                        color: .chalk.dark400
                    )
                }
                Spacer(minLength: 16)
                if let imageURI = headline.imageURI,
                    let url = URL(string: imageURI),
                    let imageData = try? Data(contentsOf: url),
                    let uiImage = UIImage(data: imageData)
                {
                    Image(uiImage: uiImage)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: proxy.size.height, height: proxy.size.height)
                        .cornerRadius(2.0)
                        .clipped()

                } else {
                    Rectangle()
                        .frame(width: proxy.size.height, height: proxy.size.height)
                        .cornerRadius(2.0)
                }
            }
            .padding(.horizontal)
            .foregroundColor(.chalk.dark700)
        }
    }
}

struct HeadlinesRow_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            HeadlinesRow(
                headline: sampleHeadlines[0],
                style: .tiemposBody.xs.regular
            )
            HeadlinesRow(
                headline: sampleHeadlines[1],
                style: .tiemposBody.xs.regular
            )
            HeadlinesRow(
                headline: sampleHeadlines[2],
                style: .tiemposBody.xs.regular
            )
            HeadlinesRow(
                headline: sampleHeadlines[3],
                style: .tiemposBody.xs.regular
            )
        }
        .previewContext(WidgetPreviewContext(family: .systemLarge))
    }
}
