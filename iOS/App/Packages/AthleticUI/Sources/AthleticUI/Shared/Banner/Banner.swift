//
//  Banner.swift
//
//
//  Created by Kyle Browning on 5/11/22.
//

import SwiftUI

public struct Banner: View {
    let viewModel: BannerViewModel

    public init(viewModel: BannerViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 8) {
                if let imageConfig = viewModel.imageConfig {
                    ImageView(imageConfig: imageConfig)
                        .frame(width: 20, height: 20)
                }
                Text(viewModel.message)
                    .fontStyle(.calibreUtility.l.regular)
                    .foregroundColor(viewModel.textColor)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 22)
            DividerView()
        }
        .background(viewModel.backgroundColor.ignoresSafeArea())
    }
}

struct Banner_Previews: PreviewProvider {
    static var previews: some View {

        let viewModel = BannerViewModel(
            imageConfig: .system("airplane", .fill),
            message: "A messages is a thousand words too.",
            textColor: .chalk.dark800,
            backgroundColor: .chalk.dark300
        )

        Group {
            Banner(viewModel: viewModel)
                .previewDisplayName("Light")
            Banner(viewModel: viewModel)
                .preferredColorScheme(.dark)
                .previewDisplayName("Dark")
        }
        .previewLayout(.sizeThatFits)
        .loadCustomFonts()
    }
}
