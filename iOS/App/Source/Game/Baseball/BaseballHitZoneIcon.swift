//
//  BaseballHitZoneIcon.swift
//  theathletic-ios
//
//  Created by Tim Korotky on 20/6/2022.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import Foundation
import SwiftUI

struct BaseballHitZoneIcon: View {
    private struct Constants {
        static let scaleFactor: CGFloat = 20
    }

    let color: Color

    /// Position of the hit. Expected values are within a 20x20 grid and are scaled accordingly.
    let hitPosition: CGPoint

    var body: some View {
        GeometryReader { geometry in
            let circleSize: CGFloat = geometry.size.width / 5
            let positionTransform = CGAffineTransform(
                scaleX: geometry.size.width / Constants.scaleFactor,
                y: geometry.size.height / Constants.scaleFactor
            )
            let position = hitPosition.applying(positionTransform)

            FieldCone(size: geometry.size)
                .overlay(
                    Circle()
                        .fill(color)
                        .frame(width: circleSize, height: circleSize)
                        .offset(x: position.x, y: position.y)
                        .darkScheme(),
                    alignment: .topLeading
                )
        }
    }
}

private struct FieldCone: View {
    let size: CGSize

    var body: some View {
        Path { path in
            path.addArc(
                center: CGPoint(x: size.width / 2, y: size.width / 2),
                radius: size.width / 2,
                startAngle: Angle(degrees: 180),
                endAngle: Angle(degrees: 360),
                clockwise: false
            )
            path.addLine(to: CGPoint(x: size.width / 2, y: size.width))
        }
        .fill(Color.chalk.dark400)
    }
}

struct BaseballHitZoneIcon_Previews: PreviewProvider {
    static var previews: some View {
        let size: CGFloat = 50
        let spacing = size * 8 / 20

        VStack(spacing: spacing) {
            let _ = DuplicateIDLogger.logDuplicates(in: Array(1...7), id: \.self)
            ForEach(1...7, id: \.self) { rowIndex in
                HStack(spacing: spacing) {
                    let _ = DuplicateIDLogger.logDuplicates(in: Array(1...5), id: \.self)
                    ForEach(1...5, id: \.self) { columnIndex in
                        let zone = 35 - 5 * rowIndex + columnIndex
                        BaseballHitZoneIcon(
                            color: .chalk.blue,
                            hitPosition: BaseballHitZone(zone: zone).position
                        )
                        .frame(width: 50, height: 50)
                        .overlay(
                            Text("\(zone)")
                                .fontStyle(.calibreUtility.l.regular)
                                .foregroundColor(.chalk.dark100)
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
}
