//
//  CommentInfoBanner.swift
//
//
//  Created by Jason Leyrer on 11/21/22.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

public struct CommentInfoBanner: View {
    public enum BannerType {
        case flair([CommentFlair])
        case temporaryBan(temporaryBanViewModel: TemporaryBanViewModel)

        var messageText: Text {
            switch self {
            case .flair:
                return Text(Strings.visibleFlairsMessage.localized)
            case .temporaryBan:
                let linkFormatString = String(
                    format: Strings.temporaryBanCodeOfConduct.localized,
                    Global.General.codeOfConductUrl.absoluteString
                )

                return Text(Strings.temporaryBanMessagePrefix.localized)
                    + Text(LocalizedStringKey(linkFormatString)).underline()
                    + Text(".")
            }
        }

        var fontStyle: AthleticFont.Style {
            switch self {
            case .flair:
                return .calibreUtility.xs.regular
            case .temporaryBan:
                return .calibreUtility.l.medium
            }
        }

        var fontColor: Color {
            switch self {
            case .flair:
                return .chalk.dark500
            case .temporaryBan:
                return .chalk.dark800
            }
        }
    }

    var type: BannerType

    public init(type: BannerType) {
        self.type = type
    }

    public var body: some View {
        switch type {

        case .flair(let flairs):
            HStack(spacing: 5) {
                type.messageText
                    .fontStyle(type.fontStyle)
                    .foregroundColor(type.fontColor)

                let _ = DuplicateIDLogger.logDuplicates(in: flairs)
                ForEach(flairs) {
                    CommentFlairLabel(flair: $0)
                }
            }
        case .temporaryBan(let viewModel):
            VStack(alignment: .leading, spacing: 8) {
                type.messageText
                Text(viewModel.daysRemainingText)
            }.fontStyle(type.fontStyle)
        }
    }
}

struct CommentListInfoBanner_Previews: PreviewProvider {
    static var previews: some View {
        CommentInfoBanner(type: .temporaryBan(temporaryBanViewModel: .init(daysRemaining: 0)))
    }
}
