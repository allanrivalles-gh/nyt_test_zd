//
//  CommentInputLabel.swift
//
//
//  Created by Jason Leyrer on 11/21/22.
//

import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

public struct CommentInputLabel: View {
    public struct AuthorInformation {
        let avatarInitial: String
        let avatarBackgroundColorHex: String
        let avatarSize: CGFloat
        let gameFlairs: [CommentFlair]
        let isStaff: Bool
        let imageUrl: URL?
    }

    public enum LabelType {
        case topLevel(title: String)
        case reply(
            title: String,
            authorInformation: AuthorInformation
        )
        case replyTheAthletic
        case play(play: CommentingPlay)
        case edit
        case banned

        var isBannedLabel: Bool {
            switch self {
            case .banned:
                return true
            default:
                return false
            }
        }

        var isStaff: Bool {
            switch self {
            case let .reply(_, authorInformation):
                return authorInformation.isStaff
            case .replyTheAthletic:
                return true
            default:
                return false
            }
        }

        var handle: String {
            switch self {
            case .topLevel:
                return Strings.commentingOn.localized
            case .reply, .replyTheAthletic:
                return Strings.replyingTo.localized
            case .play:
                return Strings.commentingPlayLabelTitle.localized
            case .edit:
                return Strings.editingYourComment.localized
            case .banned:
                return Strings.disabledComments.localized
            }
        }

        var title: String? {
            switch self {
            case .topLevel(let title):
                return title
            case .reply(let title, _):
                return title
            case .replyTheAthletic:
                return Strings.athleticStaff.localized
            case .play(let play):
                return play.commentDrawerDescription
            default:
                return nil
            }
        }

        var flairs: [CommentFlair]? {
            switch self {
            case .reply(_, let authorInformation):
                let gameFlairs = authorInformation.gameFlairs
                guard !gameFlairs.isEmpty else { return nil }
                return gameFlairs
            default:
                return nil
            }
        }
    }

    let type: LabelType

    public init(type: LabelType) {
        self.type = type
    }

    public var body: some View {

        HStack(spacing: 6) {

            HStack(spacing: 0) {

                if type.isBannedLabel {
                    Image("icon_exclamation_circle")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 30, height: 30)
                }

                Text(type.handle)
                    .fontStyle(.calibreUtility.s.regular)
                    .foregroundColor(!type.isBannedLabel ? .chalk.dark500 : .chalk.red)
                    .kerning(0.14)

            }

            HStack(spacing: 4) {

                if case .replyTheAthletic = type {
                    Image("staff_avatar")
                        .resizable()
                        .frame(width: 16, height: 16)
                        .background(Circle().fill(Color.chalk.dark300))
                }

                if case .reply(_, let authorInformation) = type {
                    if let imageUrl = authorInformation.imageUrl {
                        PlaceholderLazyImage(
                            imageUrl: imageUrl,
                            modifyImage: {
                                $0.aspectRatio(contentMode: .fill)
                            }
                        )
                        .frame(
                            width: authorInformation.avatarSize,
                            height: authorInformation.avatarSize
                        )
                        .clipShape(Circle())
                    } else {
                        CommentAvatar(
                            initial: authorInformation.avatarInitial,
                            backgroundColorHex: authorInformation.avatarBackgroundColorHex,
                            size: authorInformation.avatarSize
                        )
                    }
                }

                if let title = type.title {
                    Text(title)
                        .lineLimit(1)
                        .fontStyle(.calibreUtility.s.regular)
                        .foregroundColor(type.isStaff ? .chalk.dark800 : .chalk.dark700)
                        .kerning(0.14)
                }
            }

            if type.isStaff {
                CommentStaffLabel()
                    .padding(.leading, 2)
            }

            if let flairs = type.flairs {
                ForEach(flairs) {
                    CommentFlairLabel(flair: $0)

                }
            }
        }
    }
}
