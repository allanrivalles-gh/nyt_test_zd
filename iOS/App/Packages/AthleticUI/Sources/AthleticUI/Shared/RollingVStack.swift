//
//  RollingVStack.swift
//
//
//  Created by Mark Corbyn on 20/6/2023.
//

import Algorithms
import AthleticFoundation
import SwiftUI

/// A `VStack` that animates the insert of new rows from the top and pushes deleted rows off the bottom.
public struct RollingVStack<Item, Content>: View
where Item: Identifiable & Equatable, Content: View {

    private struct TransitionModel {
        let previousItems: [Item]
        let updatedItems: [Item]
    }

    var items: [Item]
    var duration: TimeInterval = 0.3

    /// Builds the row UI for the provided item. These items should have an opaque background to prevent overlapping content during a transition.
    @ViewBuilder var content: (Item) -> Content

    @State private var transitionModel: TransitionModel?

    public init(
        items: [Item],
        duration: TimeInterval,
        @ViewBuilder content: @escaping (Item) -> Content
    ) {
        self.items = items
        self.duration = duration
        self.content = content
    }

    public var body: some View {
        VStack(spacing: 0) {
            /// This is the main UI that will be displayed after the transition has finished.
            /// It is rendered regardless of whether transition is in progress so that the view is sized based on the final size it will be.
            ForEach(items) { item in
                content(item)
            }
            .opacity(transitionModel == nil ? 1 : 0)
        }
        .frame(maxWidth: .infinity)
        .animation(nil, value: transitionModel == nil)
        .overlay(alignment: .topTrailing) {
            if let transitionModel {
                /// When there's a transition in progress, we overlay this view to perform the animation, then remove it upon completion.
                AnimatedInsertStack(
                    previousItems: transitionModel.previousItems,
                    updatedItems: transitionModel.updatedItems,
                    duration: duration,
                    content: content
                )
                .transition(.identity)
            }
        }
        .clipped()
        .onChange(of: items) { [previousItems = items] updatedItems in
            transitionModel = TransitionModel(
                previousItems: previousItems,
                updatedItems: updatedItems
            )

            Task { @MainActor in
                try await Task.sleep(nanoseconds: UInt64(duration * 1_000_000_000))

                transitionModel = nil
            }
        }
    }
}

private struct AnimatedInsertStack<Item, Content>: View where Item: Identifiable, Content: View {

    private struct ItemGroup: Identifiable {
        let isNew: Bool
        let items: [Item]

        var id: AnyHashable { items.ids }
    }

    /// The groups of items to insert. Items are grouped into batches of new and existing items.
    private let itemGroups: [ItemGroup]

    /// The total duration of the insert, if multiple items are being inseted, each item will take a fraction of the duration.
    private let duration: TimeInterval

    @ViewBuilder private let content: (Item) -> Content

    @State private var beginInsert: Bool = false

    init(
        previousItems: [Item],
        updatedItems: [Item],
        duration: TimeInterval,
        content: @escaping (Item) -> Content
    ) {
        let previousIds = previousItems.ids
        let updatedIds = updatedItems.ids

        /// Some new items may be mixed between existing items, so figure which are new and existing.
        var mergedItems: [(isNew: Bool, item: Item)] = []
        for item in updatedItems {
            let isNew = !previousIds.contains(item.id)
            mergedItems.append((isNew, item))
        }

        /// Add the deleted items to the end of the list (NB: We're assuming item's won't be deleted from the middle).
        for item in previousItems {
            if !updatedIds.contains(item.id) {
                mergedItems.append((false, item))
            }
        }

        itemGroups = mergedItems.chunked { $0.isNew == $1.isNew }
            .map { Array($0) }
            .map { tupleGroup in
                let isNew = tupleGroup[0].isNew
                return ItemGroup(isNew: isNew, items: tupleGroup.map { $0.item })
            }

        self.duration = duration
        self.content = content
    }

    var body: some View {
        VStack(spacing: 0) {
            ForEach(indexed: itemGroups) { index, group in
                /// Views further up the list have a greater `zIndex`. This is so that when an item is inserted in the middle of the list, it slides down from underneath the item above it.
                let zIndex = Double(itemGroups.count - index)

                if group.isNew && beginInsert {
                    VStack(spacing: 0) {
                        ForEach(group.items) { item in
                            content(item)
                        }
                    }
                    .zIndex(zIndex)
                    .transition(.move(edge: .top))
                } else if !group.isNew {
                    ForEach(group.items) { item in
                        content(item)
                    }
                    .zIndex(zIndex)
                }
            }

            Spacer(minLength: 0)
        }
        .frame(maxWidth: .infinity)
        .fixedSize(horizontal: false, vertical: true)
        .onAppear {
            withAnimation(.easeIn(duration: duration)) {
                beginInsert = true
            }
        }
    }

}
