//
//  BracketRoundTabBarView.swift
//  theathletic-ios
//
//  Created by Jason Xu on 11/1/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticAnalytics
import AthleticApolloTypes
import AthleticFoundation
import AthleticUI
import SwiftUI

struct BracketRoundTabBarView: View {

    let viewModel: LeagueBracketViewModel
    let tabs: [BracketTab]

    @Binding var selectedTabIndex: Int

    var body: some View {
        ScrollViewReader { scrollProxy in
            BracketsTabsScrollView(
                viewModel: viewModel,
                tabs: tabs,
                scrollProxy: scrollProxy,
                selectedTabIndex: $selectedTabIndex
            )
        }
        .frame(height: 54)
    }
}

private struct BracketsTabsScrollView: View {

    let viewModel: LeagueBracketViewModel
    let tabs: [BracketTab]
    let scrollProxy: ScrollViewProxy

    @Binding var selectedTabIndex: Int

    @State private var tabSpacing: CGFloat = 0

    @Namespace private var namespace

    var body: some View {
        let selectedTab = tabs[safe: selectedTabIndex]
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 0) {
                let _ = DuplicateIDLogger.logDuplicates(in: tabs)
                ForEach(tabs) { tab in
                    HStack(spacing: 0) {
                        Spacer(minLength: 0)
                            .getSize { spacerSize in
                                tabSpacing = spacerSize.width
                            }

                        SplitTabItem(
                            tab: tab,
                            isSelected: selectedTab == tab,
                            tabWidth: nil,
                            horizontalPadding: 16,
                            textHorizontalPadding: 0,
                            interTabSpacing: tabSpacing,
                            namespace: namespace,
                            hasSecondaryAction: false,
                            tapAction: {
                                if let tabIndex = tabs.firstIndex(of: tab) {
                                    selectedTabIndex = tabIndex
                                }
                                if let bracketRound = tab.bracketRound {
                                    viewModel.trackRoundTabClick(bracketRound: bracketRound)
                                }
                            }
                        )
                        Spacer(minLength: 0)
                    }
                    .id(tab)
                    .pagingTabUnderscoreStyle(.hidden)
                }
            }
            .animation(.easeOut(duration: 0.2), value: selectedTabIndex)
            .onChange(of: selectedTabIndex) { tabIndex in
                guard let tab = tabs[safe: tabIndex] else {
                    return
                }

                // Use DispatchQueue to fix bug with autoscroll with bracket view
                DispatchQueue.main.async {
                    withAnimation {
                        scrollProxy.scrollTo(tab, anchor: .center)
                    }
                }
            }
            .padding(.horizontal, 16)
        }
        .background(Color.chalk.dark200)
    }
}

struct BracketRoundTabBarView_Previews: PreviewProvider {

    private static var tabs = [
        BracketTab(
            id: "1",
            title: "Round Of 16",
            bracketRound: .round1,
            isLive: false
        ),
        BracketTab(
            id: "2",
            title: "Quarter-Finals",
            bracketRound: .round2,
            isLive: true
        ),
        BracketTab(
            id: "3",
            title: "Semi-Finals",
            bracketRound: .round3,
            isLive: false
        ),
        BracketTab(
            id: "4",
            title: "Final",
            bracketRound: .round4,
            isLive: false
        ),
    ]

    static var previews: some View {
        BracketRoundTabBarView(
            viewModel: LeagueBracketViewModel(
                network: BracketsPreviewHelper.network,
                leagueId: "44",
                leagueCode: GQL.LeagueCode.uwc,
                seasonId: nil,
                teamId: nil,
                analyticsDefaults: PreviewAnalyticDefaults(),
                tbdString: "TBD",
                getGamePhase: { _ in .postGame }
            ),
            tabs: tabs,
            selectedTabIndex: .constant(0)
        )
        .loadCustomFonts()
    }
}
