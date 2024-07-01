//
//  CustomUILabelView.swift
//
//
//  Created by Jason Xu on 1/30/23.
//

import Foundation
import SwiftUI
import UIKit

/// SwiftUI Wrapped UILabel
/// This view is used to access properties of `UILabel` that aren't currently available in SwiftUI `Text` view.
public struct CustomUILabelView: UIViewRepresentable {

    let text: String
    let font: UIFont
    let lineHeightMultiple: Double
    let numberOfLines: Int
    let textColor: UIColor?
    let kerning: CGFloat?
    @Binding var dynamicHeight: CGFloat

    public init(
        text: String,
        font: UIFont,
        lineHeightMultiple: Double = 1,
        numberOfLines: Int = 0,
        textColor: Color? = nil,
        kerning: CGFloat? = nil,
        dynamicHeight: Binding<CGFloat> = .constant(.zero)
    ) {
        self.text = text
        self.font = font
        self.lineHeightMultiple = lineHeightMultiple
        self.numberOfLines = numberOfLines
        self.textColor = textColor.map { UIColor($0) }
        self.kerning = kerning
        self._dynamicHeight = dynamicHeight
    }

    public func makeUIView(context: Context) -> UILabel {
        let label = UILabel()

        label.font = font
        label.numberOfLines = numberOfLines
        label.lineBreakMode = .byWordWrapping
        label.textColor = textColor
        label.setContentHuggingPriority(.defaultLow, for: .horizontal)
        label.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)

        let attributedString = NSMutableAttributedString(string: text)
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineHeightMultiple = lineHeightMultiple
        paragraphStyle.lineBreakMode = .byTruncatingTail

        attributedString.addAttribute(
            .paragraphStyle,
            value: paragraphStyle,
            range: NSMakeRange(0, attributedString.length)
        )

        if let kerning {
            attributedString.addAttribute(
                .kern,
                value: kerning,
                range: NSMakeRange(0, attributedString.length)
            )
        }

        label.attributedText = attributedString

        return label
    }

    public func updateUIView(_ uiView: UILabel, context: Context) {
        DispatchQueue.main.async {
            dynamicHeight =
                uiView.sizeThatFits(
                    CGSize(width: uiView.bounds.width, height: CGFloat.greatestFiniteMagnitude)
                ).height
        }
    }
}
