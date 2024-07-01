//
//  Paywall.swift
//
//
//  Created by Kyle Browning on 8/14/22.
//

import AthleticFoundation
import Datadog
import SwiftUI

public struct Paywall: View {
    private let onAppear: VoidClosure
    private let subscribeTapped: VoidClosure

    public init(onAppear: @escaping VoidClosure, subscribeTapped: @escaping VoidClosure) {
        self.onAppear = onAppear
        self.subscribeTapped = subscribeTapped
    }

    public var body: some View {
        VStack(alignment: .center, spacing: 0) {
            Text(Strings.paywallTitle.localized)
                .fontStyle(.slab.m.bold)
                .foregroundColor(.chalk.dark200)
                .multilineTextAlignment(.center)
                .padding(.bottom, 8)
            Text(Strings.paywallSubtitle.localized)
                .fontStyle(.tiemposBody.s.regular)
                .foregroundColor(.chalk.dark400)
                .padding(.bottom, 24)
                .multilineTextAlignment(.center)
            CtaButton(viewModel: .init(type: .articlePaywall, action: subscribeTapped))
        }
        .padding(.horizontal, 40)
        .padding(.vertical, 48)
        .background(Color.chalk.dark800)
        .cornerRadius(2)
        .shadow(radius: 10)
        .shadow(radius: 10)
        .shadow(radius: 10)
        .shadow(color: .chalk.dark200, radius: 32)
        .shadow(color: .chalk.dark200, radius: 32)
        .shadow(color: .chalk.dark200, radius: 32)
        .trackRUMView(name: "Paywall")
        .onAppear {
            self.onAppear()
        }
    }
}

struct SwiftUIView_Previews: PreviewProvider {
    static var previews: some View {
        Paywall {
        } subscribeTapped: {
        }
        .padding(16)
        .loadCustomFonts()
    }
}
