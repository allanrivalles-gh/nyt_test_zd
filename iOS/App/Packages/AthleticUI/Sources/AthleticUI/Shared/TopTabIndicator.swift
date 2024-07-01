//
//  TopTabIndicator.swift
//
//
//  Created by Jason Xu on 12/8/22.
//

import AthleticFoundation
import Foundation
import SwiftUI

struct TapTabIndicatorView: View {

    let pageCount: Int
    let currentPage: Int

    var body: some View {
        HStack(spacing: 5) {
            let _ = DuplicateIDLogger.logDuplicates(in: Array(0..<pageCount), id: \.self)
            ForEach(0..<pageCount, id: \.self) { page in
                Rectangle()
                    .fill(
                        page == currentPage ? Color.chalk.dark800 : Color.chalk.dark100
                    )
                    .frame(height: 1)
                    .padding(.vertical, 12)
            }
        }
        .padding(.horizontal, 15)
    }
}

private struct TopTabIndicator: ViewModifier {

    let pageCount: Int
    let currentPage: Int

    func body(content: Content) -> some View {
        content
            .overlay(alignment: .top) {
                if pageCount > 1 {
                    TapTabIndicatorView(pageCount: pageCount, currentPage: currentPage)
                }
            }
    }
}

extension View {
    public func topTabIndicator(pageCount: Int, currentPage: Int) -> some View {
        modifier(TopTabIndicator(pageCount: pageCount, currentPage: currentPage))
    }
}
