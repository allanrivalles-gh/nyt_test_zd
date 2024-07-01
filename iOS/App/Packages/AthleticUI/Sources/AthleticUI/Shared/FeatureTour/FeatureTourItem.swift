//
//  FeatureTourItem.swift
//
//
//  Created by Kyle Browning on 5/13/22.
//

import AthleticFoundation
import SwiftUI

public struct FeatureTourItem: View {
    @State private var isSubmitting: Bool = false
    public let viewModel: FeatureTourItemViewModel
    public let fallbackAction: FeatureTourItemViewModel.CallToAction

    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            ZStack {
                Rectangle()
                    .fill(Color.chalk.dark300)

                ImageView(imageConfig: viewModel.imageConfig)
                    .frame(maxWidth: .infinity, alignment: .center)
            }

            Text(viewModel.title)
                .fontStyle(.slab.m.bold)
                .foregroundColor(.chalk.dark700)

            Text(viewModel.subTitle)
                .fontStyle(.tiemposBody.m.regular)
                .lineSpacing(6)
                .foregroundColor(.chalk.dark500)

            Spacer()

            let buttonConfig = viewModel.callToAction ?? fallbackAction
            Button {
                Task {
                    isSubmitting = true
                    await buttonConfig.action()
                    isSubmitting = false
                }
            } label: {
                ZStack {
                    Text(buttonConfig.title)
                        .opacity(isSubmitting ? 0 : 1)
                    if isSubmitting {
                        ProgressView()
                            .tint(.chalk.dark200)
                    }
                }
            }
            .disabled(isSubmitting)
            .buttonStyle(.core(size: .regular, level: .primary))
            .padding(.bottom, 50)
        }
        .padding(.horizontal, 16)
        .onAppear {
            viewModel.onAppear()
        }
    }
}

struct FeatureTourItem_Previews: PreviewProvider {
    static var previews: some View {
        let _ = AthleticUI.registerFonts()

        let viewModel: FeatureTourItemViewModel = .init(
            id: "1",
            title: "Easy access to favorites",
            subTitle: "Tap logos to instantly view followed team, league and author content",
            imageConfig: .system("star", .fit),
            callToAction: .init(title: "View Settings", action: {}),
            onAppear: {},
            onDismiss: {}
        )

        Group {
            FeatureTourItem(viewModel: viewModel, fallbackAction: .init(title: "Next", action: {}))
                .preferredColorScheme(.light)
                .previewLayout(.sizeThatFits)
            FeatureTourItem(viewModel: viewModel, fallbackAction: .init(title: "Done", action: {}))
                .preferredColorScheme(.dark)
                .previewLayout(.sizeThatFits)
        }
    }
}
