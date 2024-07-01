//
//  BaseballPitchZoneIcon.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 22/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import SwiftUI

struct BaseballPitchZoneIcon: View {
    let color: Color
    let pitchPosition: CGPoint

    var body: some View {
        GeometryReader { geometry in
            let circleSize: CGFloat = geometry.size.width * 4 / 20
            let lineWidth: CGFloat = geometry.size.width / 20
            let xMultiplier = geometry.size.width / 20
            let yMultiplier = geometry.size.height / 20
            let transform = CGAffineTransform(scaleX: xMultiplier, y: yMultiplier)
            let circleOffset = pitchPosition.applying(transform)

            PitchGrid(
                size: geometry.size,
                lineWidth: lineWidth,
                transform: transform
            )
            .overlay(
                Circle()
                    .fill(color)
                    .frame(width: circleSize, height: circleSize)
                    .offset(x: circleOffset.x, y: circleOffset.y)
                    .darkScheme(),
                alignment: .topLeading
            )
        }
    }
}

private struct PitchGrid: View {
    let size: CGSize
    let lineWidth: CGFloat
    let transform: CGAffineTransform

    var body: some View {
        Path { path in
            var points = outerGridPoints
            let startPoint = points.removeFirst().applying(transform)
            path.move(to: startPoint)
            points.forEach {
                path.addLine(to: $0.applying(transform))
            }
        }
        .fill(Color.chalk.dark400)
        .mask(
            ZStack {
                Color.black
                Color.black
                    .frame(width: size.width * 0.5, height: size.height * 0.5)
                    .blendMode(.destinationOut)
            }
        )
    }

    private var outerGridPoints: [CGPoint] {
        [
            CGPoint(x: 0, y: 4),
            CGPoint(x: 4, y: 4),
            CGPoint(x: 4, y: 0),
            CGPoint(x: 5, y: 0),
            CGPoint(x: 5, y: 4),
            CGPoint(x: 15, y: 4),
            CGPoint(x: 15, y: 0),
            CGPoint(x: 16, y: 0),
            CGPoint(x: 16, y: 4),
            CGPoint(x: 20, y: 4),
            CGPoint(x: 20, y: 5),
            CGPoint(x: 16, y: 5),
            CGPoint(x: 16, y: 15),
            CGPoint(x: 20, y: 15),
            CGPoint(x: 20, y: 16),
            CGPoint(x: 16, y: 16),
            CGPoint(x: 16, y: 20),
            CGPoint(x: 15, y: 20),
            CGPoint(x: 15, y: 16),
            CGPoint(x: 5, y: 16),
            CGPoint(x: 5, y: 20),
            CGPoint(x: 4, y: 20),
            CGPoint(x: 4, y: 16),
            CGPoint(x: 0, y: 16),
            CGPoint(x: 0, y: 15),
            CGPoint(x: 4, y: 15),
            CGPoint(x: 4, y: 5),
            CGPoint(x: 0, y: 5),
        ]
    }
}

struct BaseballPitchZoneIcon_Previews: PreviewProvider {
    static var previews: some View {
        let size: CGFloat = 50
        let spacing = size * 8 / 20

        VStack(spacing: spacing) {
            let _ = DuplicateIDLogger.logDuplicates(in: Array(0..<5), id: \.self)
            ForEach(0..<5, id: \.self) { rowIndex in
                HStack(spacing: spacing) {
                    let _ = DuplicateIDLogger.logDuplicates(in: Array(0..<5), id: \.self)
                    ForEach(0..<5, id: \.self) { columnIndex in
                        let zone = zoneMap[rowIndex][columnIndex]
                        BaseballPitchZoneIcon(
                            color: .blue,
                            pitchPosition: BaseballPitchZone(zone: zone).position
                        )
                        .frame(width: 50, height: 50)
                        .overlay(
                            Text("\(zone)")
                                .fontStyle(.calibreUtility.l.regular)
                                .foregroundColor(.chalk.dark800)
                                .opacity(0.8)
                        )
                        .id("\(rowIndex) \(columnIndex)")
                    }
                }
            }
        }
        .padding(spacing)
        .previewLayout(.sizeThatFits)
    }

    private static let zoneMap: [[Int]] = [
        [131, 101, 102, 103, 111],
        [132, 1, 2, 3, 112],
        [133, 4, 5, 6, 113],
        [134, 7, 8, 9, 114],
        [135, 121, 122, 123, 115],
    ]
}
