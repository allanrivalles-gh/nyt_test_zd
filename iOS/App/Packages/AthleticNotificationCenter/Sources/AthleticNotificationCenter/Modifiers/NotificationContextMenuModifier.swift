//
//  File.swift
//
//
//  Created by Jason Leyrer on 9/13/23.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

extension View {
    func notificationContextMenu(model: NotificationCenterRowViewModel) -> some View {
        ModifiedContent(
            content: self,
            modifier: NotificationContextMenuModifier(model: model)
        )
    }
}

private struct NotificationContextMenuModifier: ViewModifier {
    @ObservedObject var model: NotificationCenterRowViewModel

    func body(content: Content) -> some View {
        content
            .contextMenu {
                NotificationContentActionsProvider(model: model).menuButtons
            }
    }
}

private struct NotificationContentActionsProvider {
    @ObservedObject var model: NotificationCenterRowViewModel

    enum Action: Int, Identifiable {
        case share
        case markContentRead
        case markContentUnread
        case saveContent
        case unsaveContent

        var id: Int {
            rawValue
        }

        var title: String {
            switch self {
            case .share:
                return Strings.share.localized
            case .markContentRead:
                return Strings.markContentRead.localized
            case .markContentUnread:
                return Strings.markContentUnread.localized
            case .saveContent:
                return Strings.saveContent.localized
            case .unsaveContent:
                return Strings.unsaveContent.localized
            }
        }

        var menuIconTitle: String {
            switch self {
            case .share:
                return "square.and.arrow.up"
            case .markContentRead:
                return "checkmark.circle"
            case .markContentUnread:
                return "xmark.circle"
            case .saveContent:
                return "icn_save"
            case .unsaveContent:
                return "icn_saved"
            }
        }
    }

    @ViewBuilder
    var menuButtons: some View {
        let _ = DuplicateIDLogger.logDuplicates(in: availableActions)
        ForEach(availableActions) {
            menuButton(type: $0)
        }
    }

    private var availableActions: [Action] {
        var actions: [Action] = [.share]

        if model.isReadableContent {
            let readAction: Action = model.isContentRead ? .markContentUnread : .markContentRead
            let saveAction: Action = model.isContentSaved ? .unsaveContent : .saveContent

            actions.append(contentsOf: [readAction, saveAction])
        }

        return actions
    }

    @ViewBuilder
    private func menuButton(type: Action) -> some View {
        Group {
            switch type {
            case .share:
                if let shareItem = model.shareItem {
                    ShareLink(item: shareItem.url, message: shareItem.titleText) {
                        Label(type.title, systemImage: type.menuIconTitle)
                    }
                }
            case .saveContent, .unsaveContent:
                Button(action: buttonAction(type: type)) {
                    Label(type.title, image: type.menuIconTitle)
                }
            default:
                Button(action: buttonAction(type: type)) {
                    Label(type.title, systemImage: type.menuIconTitle)
                }
            }
        }
        .foregroundColor(.chalk.dark800)
    }

    private func buttonAction(type: Action) -> VoidClosure {
        switch type {
        case .share:
            return {}
        case .markContentRead, .markContentUnread:
            return {
                guard let contentId = model.contentId else { return }

                Task {
                    await MainActor.run {
                        model.isContentRead.toggle()
                    }

                    await model.network.updateArticleReadState(
                        id: contentId,
                        isRead: model.isContentRead,
                        percentRead: nil
                    )
                }
            }
        case .saveContent, .unsaveContent:
            return {
                guard let contentId = model.contentId else { return }

                Task {
                    await MainActor.run {
                        model.isContentSaved.toggle()
                    }

                    do {
                        try await model.network.updateArticleSaveState(
                            id: contentId,
                            isSaved: model.isContentSaved
                        )
                    } catch {
                        await MainActor.run {
                            model.isContentSaved.toggle()
                        }
                    }
                }
            }
        }
    }
}
