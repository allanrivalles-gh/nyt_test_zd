//
//  FeatureTour.swift
//
//
//  Created by Kyle Browning on 5/09/22.
//

import AthleticFoundation
import SwiftUI

public struct FeatureTour: View {

    @State private var selectedId: String
    var viewModel: FeatureTourViewModel
    @Binding private var isShowing: Bool

    public init(viewModel: FeatureTourViewModel, isShowing: Binding<Bool>) {
        self.viewModel = viewModel
        self._isShowing = isShowing
        guard let firstItemViewModel = viewModel.items.first else {
            fatalError("Must supply at least one ViewModel")
        }
        selectedId = firstItemViewModel.id
    }

    public var body: some View {
        GeometryReader { geometry in
            NavigationView {
                VStack(alignment: .leading, spacing: 0) {
                    TabView(selection: $selectedId) {
                        let _ = DuplicateIDLogger.logDuplicates(
                            in: viewModel.items,
                            id: \.id
                        )
                        ForEach(viewModel.items, id: \.id) { item in
                            ScrollView {
                                HStack {
                                    Spacer()
                                    FeatureTourItem(
                                        viewModel: item,
                                        fallbackAction: fallbackAction(for: item)
                                    )
                                    .frame(
                                        maxWidth: geometry.size.width >= 768 ? 600 : nil,
                                        maxHeight: geometry.size.width >= 1000 ? 600 : nil,
                                        alignment: .center
                                    )
                                    Spacer()
                                }
                            }
                        }
                        .padding(.top, 18)
                    }
                    .tabViewStyle(.page)
                    .indexViewStyle(.page(backgroundDisplayMode: .always))
                    Spacer()
                }
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Text(Strings.featureTourNewIndicator.localized)
                            .fontStyle(.calibreUtility.s.medium)
                            .foregroundColor(.chalk.dark100)
                            .padding(.horizontal, 17)
                            .padding(.vertical, 6)
                            .background(Capsule().fill(Color.chalk.red))
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: close) {
                            Image(systemName: "xmark")
                        }
                        .foregroundColor(.chalk.dark800)
                    }
                }
                .navigationBarTitleDisplayMode(.inline)
                .background(Color.chalk.dark200.edgesIgnoringSafeArea(.all))
            }
        }
    }

    private func close() {
        isShowing.toggle()
        viewModel.items
            .first { $0.id == selectedId }?
            .onDismiss()
    }

    private func fallbackAction(
        for item: FeatureTourItemViewModel
    ) -> FeatureTourItemViewModel.CallToAction {
        guard let nextId = viewModel.itemId(afterId: item.id) else {
            return doneAction(for: item)
        }

        return FeatureTourItemViewModel.CallToAction(
            title: Strings.next.localized,
            action: {
                item.onNext?()

                withAnimation {
                    selectedId = nextId
                }
            }
        )
    }

    private func doneAction(
        for item: FeatureTourItemViewModel
    ) -> FeatureTourItemViewModel.CallToAction {
        .init(
            title: Strings.done.localized,
            action: {
                item.onDismiss()
                close()
            }
        )
    }
}

struct FeatureTour_Previews: PreviewProvider {
    static var previews: some View {
        let _ = AthleticUI.registerFonts()
        let viewModel = FeatureTourViewModel(
            items: [
                .init(
                    id: "1",
                    title: "Easy access to favorites",
                    subTitle:
                        "Tap logos to instantly view followed team, league and author content",
                    imageConfig: .custom("nav-feature-1", .fit),
                    callToAction: .init(
                        title: Strings.startReading.localized,
                        action: {}
                    ),
                    onAppear: {},
                    onDismiss: {}
                ),
                .init(
                    id: "2",
                    title: "Customize display order",
                    subTitle: "Use Edit button to easily add, remove, or reorder your follows",
                    imageConfig: .system("airplane", .fit),
                    callToAction: .init(
                        title: Strings.startReading.localized,
                        action: {}
                    ),
                    onAppear: {},
                    onDismiss: {}
                ),
                .init(
                    id: "3",
                    title: "Explore top sports stories",
                    subTitle: "Search and browse trending news and content in the Discover tab",
                    imageConfig: .system("bicycle", .fit),
                    callToAction: .init(
                        title: Strings.startReading.localized,
                        action: {}
                    ),
                    onAppear: {},
                    onDismiss: {}
                ),
                .init(
                    id: "4",
                    title: "Convenient profile access",
                    subTitle: "View and customize preferences in a new Account tab location",
                    imageConfig: .system("bus", .fit),
                    callToAction: .init(
                        title: Strings.startReading.localized,
                        action: {}
                    ),
                    onAppear: {},
                    onDismiss: {}
                ),
            ]
        )
        Group {
            FeatureTour(viewModel: viewModel, isShowing: .constant(true))
                .preferredColorScheme(.light)
                .previewDevice("iPhone 8")
        }
    }
}
