//
//  CtaButton.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/18/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

public struct CtaButton: View {
    public typealias Action = VoidClosure

    public struct ViewModel {
        public enum ButtonSize {
            case large, small
        }

        private struct Constants {
            struct Large {
                static let buttonImageSize: CGFloat = 36
                static let buttonHeight: CGFloat = 64
                static let font: Font = .font(for: .slab.m.bold)
            }
            struct Small {
                static let buttonImageSize: CGFloat = 20
                static let buttonHeight: CGFloat = 56
                static let font: Font = .font(for: .slab.s.bold)
            }
        }

        public enum ButtonType {
            case createAccount
            case logIn
            case subscribeSmall
            case subscribePaywall
            case subscribeLarge
            case freeTrial
            case startReading
            case articlePaywall
            case next
            case done
            case submit
        }

        var isEnabled: Binding<Bool>
        let type: ButtonType
        let action: Action?

        public init(
            type: ButtonType,
            isEnabled: Binding<Bool> = .constant(true),
            action: Action? = nil
        ) {
            self.type = type
            self.action = action
            self.isEnabled = isEnabled
        }

        var title: String {
            switch type {
            case .createAccount:
                return Strings.createAnAccount.localized
            case .subscribeSmall, .subscribeLarge, .subscribePaywall:
                return Strings.subscribe.localized
            case .articlePaywall:
                return Strings.paywallButtonTitle.localized
            case .freeTrial:
                return Strings.profileStartFreeTrial.localized
            case .startReading:
                return Strings.startReading.localized
            case .logIn:
                return Strings.login.localized
            case .next:
                return Strings.next.localized
            case .done:
                return Strings.done.localized
            case .submit:
                return Strings.submit.localized
            }
        }

        private var buttonSize: ButtonSize {
            switch type {
            case .startReading, .subscribeLarge:
                return .large
            case .createAccount, .freeTrial, .subscribeSmall, .logIn, .articlePaywall, .next,
                .done, .subscribePaywall, .submit:
                return .small
            }
        }

        var buttonImageSize: CGFloat {
            switch buttonSize {
            case .large:
                return Constants.Large.buttonImageSize
            case .small:
                return Constants.Small.buttonImageSize
            }
        }

        var buttonHeight: CGFloat {
            switch buttonSize {
            case .large:
                return Constants.Large.buttonHeight
            case .small:
                return Constants.Small.buttonHeight
            }
        }

        var buttonFont: Font {
            switch buttonSize {
            case .large:
                return Constants.Large.font
            case .small:
                return Constants.Small.font
            }
        }
    }

    private var foregroundColor: Color {
        switch viewModel.type {
        case .subscribeLarge, .createAccount, .startReading, .logIn, .next, .done, .submit:
            return viewModel.isEnabled.wrappedValue ? .chalk.dark100 : .chalk.dark400
        case .subscribeSmall, .freeTrial, .articlePaywall, .subscribePaywall:
            return viewModel.isEnabled.wrappedValue ? .chalk.dark800 : .chalk.dark400
        }
    }

    private var backgroundColor: Color {
        switch viewModel.type {
        case .subscribeLarge, .createAccount, .startReading, .logIn, .next, .done, .submit:
            return viewModel.isEnabled.wrappedValue ? .chalk.dark800 : .chalk.dark300
        case .subscribeSmall, .freeTrial:
            return viewModel.isEnabled.wrappedValue ? .chalk.dark300 : .chalk.dark800
        case .subscribePaywall, .articlePaywall:
            /// when disabled we use the same color as .subscribeSmall
            if !viewModel.isEnabled.wrappedValue {
                return .chalk.dark800
            }
            /// we want pure white (#FFFFFF) and pure black (#000000)
            return colorScheme == .light ? .chalk.dark200 : .chalk.dark100
        }
    }

    @Environment(\.colorScheme) private var colorScheme
    let viewModel: ViewModel

    public init(viewModel: ViewModel) {
        self.viewModel = viewModel
    }

    // MARK: - Views

    public var body: some View {
        if let action = viewModel.action {
            Button(action: action) {
                contentView
            }
            .disabled(!viewModel.isEnabled.wrappedValue)
            .foregroundColor(foregroundColor)
        } else {
            contentView
        }
    }

    private var contentView: some View {
        HStack {
            Text(viewModel.title)
                .font(viewModel.buttonFont)
                .foregroundColor(foregroundColor)

            Spacer()

            Image("athletic_right", bundle: .athleticUI)
                .renderingMode(.template)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(foregroundColor)
                .frame(
                    width: viewModel.buttonImageSize,
                    height: viewModel.buttonImageSize
                )
        }
        .padding(.horizontal, 24)
        .frame(height: viewModel.buttonHeight)
        .background(backgroundColor)
    }
}

struct CtaButtonPreviews: PreviewProvider {
    static var previews: some View {
        Group {
            VStack {
                CtaButton(viewModel: .init(type: .startReading))
                CtaButton(viewModel: .init(type: .freeTrial))
                CtaButton(viewModel: .init(type: .createAccount))
                CtaButton(viewModel: .init(type: .articlePaywall))
                CtaButton(viewModel: .init(type: .subscribeSmall))
                CtaButton(viewModel: .init(type: .subscribeLarge))
                CtaButton(viewModel: .init(type: .submit))
                CtaButton(viewModel: .init(type: .logIn, isEnabled: .constant(false)))
            }
            .padding(.all, 28)
            .preferredColorScheme(.dark)
            .previewLayout(.sizeThatFits)
            .previewDisplayName("Dark")
            VStack {
                CtaButton(viewModel: .init(type: .startReading))
                CtaButton(viewModel: .init(type: .freeTrial))
                CtaButton(viewModel: .init(type: .createAccount))
                CtaButton(viewModel: .init(type: .subscribeSmall))
                CtaButton(viewModel: .init(type: .subscribeLarge))
                CtaButton(viewModel: .init(type: .submit))
                CtaButton(viewModel: .init(type: .logIn, isEnabled: .constant(false)))
            }
            .padding(.all, 28)
            .preferredColorScheme(.light)
            .previewLayout(.sizeThatFits)
            .previewDisplayName("Light")
        }
        .loadCustomFonts()
    }
}
