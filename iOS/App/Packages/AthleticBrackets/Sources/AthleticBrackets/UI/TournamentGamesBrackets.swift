//
//  TournamentGamesBrackets.swift
//
//
//  Created by Leonardo da Silva on 02/11/22.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

public struct TournamentStage: TournamentBracketsStage {
    public let id: String
    public let groups: [TournamentStageGroup]
    public let connected: Bool

    init(id: String, groups: [TournamentStageGroup], connected: Bool = true) {
        self.id = id
        self.groups = groups
        self.connected = connected
    }
}

public struct TournamentStageGroup: TournamentBracketsGroup {
    public let title: String?
    public let tiles: [TournamentTile]
}

public struct TournamentGamesBrackets: View {
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    @Binding private var offset: Int
    @State private var yOffset: CGFloat = 0
    private let stages: [TournamentStage]
    private let tbdString: String
    private let liveString: String
    private let refresh: () async -> Void
    private let onCellTap: ((TournamentTile) -> Void)?
    @Namespace private var scrollTargetId

    public init(
        offset: Binding<Int> = .constant(0),
        stages: [TournamentStage],
        tbdString: String,
        liveString: String,
        refresh: @escaping () async -> Void,
        onCellTap: ((TournamentTile) -> Void)? = nil
    ) {
        _offset = offset
        self.stages = stages
        self.tbdString = tbdString
        self.liveString = liveString
        self.refresh = refresh
        self.onCellTap = onCellTap
    }

    public var body: some View {
        GeometryReader { geometry in
            let containerWidth = geometry.size.width
            let padding: CGFloat = 16
            let scrollTargetHeight: CGFloat = 1
            let horizontalSpacing: CGFloat = 26
            /// These expressions for `cellWidth` simply removes the space which does not contain a cell
            /// from the `containerWidth` and then divides by the number of cells.
            /// - Layout:
            /// iPad: padding | cell | horizontalSpacing | cell | horizontalSpacing | cell | padding
            /// iPhone: padding | cell | horizontalSpacing | (cell / 2)
            let cellWidth =
                horizontalSizeClass == .regular
                ? (containerWidth - padding * 2 - horizontalSpacing * 2) / 3
                : (containerWidth - padding - horizontalSpacing) / 1.5
            /// Using the layout above, we can determine the total count of fully visible columns as:
            let visibleColumnsCount = horizontalSizeClass == .regular ? 3 : 1
            ScrollViewReader { scrollView in
                BracketLayoutReader { bracket in
                    RefreshableScrollView(.vertical) { offset in
                        yOffset = offset.y
                    } refreshAction: {
                        await refresh()
                    } content: {
                        ZStack(alignment: .top) {
                            Color.clear
                                .frame(height: scrollTargetHeight)
                                .id(scrollTargetId)
                            TournamentBrackets(
                                stages: stages,
                                horizontalSpacing: horizontalSpacing,
                                verticalSpacing: 18,
                                offset: offset,
                                /// we need to subtract the vertical padding
                                minHeight: geometry.size.height - padding * 2,
                                visibleColumnsCount: visibleColumnsCount
                            ) { tile in
                                let cell = TournamentTileCell(
                                    tile: tile,
                                    tbdString: tbdString,
                                    liveString: liveString,
                                    suggestedWidth: cellWidth
                                )
                                if let onCellTap {
                                    cell
                                        .onTapGesture {
                                            onCellTap(tile)
                                        }
                                } else {
                                    cell
                                }
                            }
                            .padding(padding)
                        }
                    }
                    .onChange(of: offset) { [offset] newOffset in
                        let oldOffset = offset
                        let oldHeight = bracket.height(forColumn: oldOffset, collapsed: true)
                        let newHeight = bracket.height(forColumn: newOffset, collapsed: true)
                        let newYOffset = yOffset / oldHeight * newHeight
                        withAnimation {
                            scrollView.scrollToVerticalDistanceFromTarget(
                                id: scrollTargetId,
                                distance: -newYOffset,
                                containerHeight: geometry.size.height,
                                targetHeight: scrollTargetHeight
                            )
                        }
                    }
                }
            }
        }
        .background(Color.chalk.dark100)
        .onSwipeDetected { direction in
            switch direction {
            case .left:
                if offset < stages.count - 1 {
                    offset += 1
                }
            case .right:
                if offset > 0 {
                    offset -= 1
                }
            }
        }
    }
}

private enum SwipeDirection {
    case left
    case right
}

extension View {
    fileprivate func onSwipeDetected(
        _ onSwipeDetected: @escaping (SwipeDirection) -> Void
    ) -> some View {
        modifier(OnSwipeDetectedViewModifier(onSwipeDetected: onSwipeDetected))
    }
}

/// this gesture detector is triggered before the touch ends, which helps it feel more responsive
private struct OnSwipeDetectedViewModifier: ViewModifier {
    @State private var detected = false

    let onSwipeDetected: ((SwipeDirection) -> Void)

    func body(content: Content) -> some View {
        content
            .onSimultaneousDragGesture(
                onChanged: { value in
                    guard !detected else { return }

                    let horizontalAmount = value.translation.width
                    let verticalAmount = value.translation.height

                    /// we make sure the horizontal distance travelled by the finger is greater than the vertical distance
                    if abs(horizontalAmount) > abs(verticalAmount) {
                        let direction: SwipeDirection = horizontalAmount < 0 ? .left : .right
                        onSwipeDetected(direction)
                        detected = true
                    }
                },
                onEnded: { _ in
                    detected = false
                }
            )
    }
}

extension ScrollViewProxy {
    fileprivate func scrollToVerticalDistanceFromTarget<ID: Hashable>(
        id targetId: ID,
        distance: CGFloat,
        containerHeight: CGFloat,
        targetHeight: CGFloat
    ) {
        scrollTo(
            targetId,
            anchor: .init(
                x: 0.5,
                y: distance / (containerHeight - targetHeight)
            )
        )
    }
}

struct TournamentGamesBrackets_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            Preview()
            Preview()
                .darkScheme()
        }
        .loadCustomFonts()
    }

    private struct Preview: View {
        @State private var offset: Int = 0

        var body: some View {
            let england = TournamentTile.TeamData(
                id: "1",
                legacyId: nil,
                logos: [
                    ATHImageResource(
                        url: URL(
                            string:
                                "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-710-50x50.png"
                        )!
                    )
                ],
                alias: "ENG",
                accentColor: nil
            )
            let portugal = TournamentTile.TeamData(
                id: "2",
                legacyId: nil,
                logos: [
                    ATHImageResource(
                        url: URL(
                            string:
                                "https://s3-us-west-2.amazonaws.com/theathletic-team-logos/team-logo-785-50x50.png"
                        )!
                    )
                ],
                alias: "POR",
                accentColor: nil
            )
            TournamentGamesBrackets(
                offset: $offset,
                stages: [
                    TournamentStage(
                        id: "stage-0",
                        groups: [
                            TournamentStageGroup(
                                title: nil,
                                tiles: [
                                    TournamentTile(
                                        title: "FT, Nov 20, Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-0",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(england),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 2
                                                    )
                                                ),
                                                awayTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(portugal),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 1
                                                    )
                                                )
                                            )
                                        ),
                                        isHighlighted: false
                                    ),
                                    TournamentTile(
                                        title: "FT, Nov 20, Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-1",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(england),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 2
                                                    )
                                                ),
                                                awayTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(portugal),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 1
                                                    )
                                                )
                                            )
                                        ),
                                        isHighlighted: false
                                    ),
                                    TournamentTile(
                                        title: "FT, Nov 20, Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-2",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(england),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 2
                                                    )
                                                ),
                                                awayTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(portugal),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 1
                                                    )
                                                )
                                            )
                                        ),
                                        isHighlighted: false
                                    ),
                                    TournamentTile(
                                        title: "FT, Nov 20, Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-3",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(england),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 2
                                                    )
                                                ),
                                                awayTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(portugal),
                                                    scores: TournamentTile.TeamScore(
                                                        score: 1
                                                    )
                                                )
                                            )
                                        ),
                                        isHighlighted: false
                                    ),
                                ]
                            )
                        ]
                    ),
                    TournamentStage(
                        id: "stage-1",
                        groups: [
                            TournamentStageGroup(
                                title: nil,
                                tiles: [
                                    TournamentTile(
                                        title: "Sun, 12:00pm. Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-4",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(england)
                                                ),
                                                awayTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(portugal)
                                                )
                                            )
                                        ),
                                        isHighlighted: false
                                    ),
                                    TournamentTile(
                                        title: "Sun, 12:00pm. Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-5",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(england)
                                                ),
                                                awayTeam: TournamentTile.GameTeam(
                                                    details: .confirmed(portugal)
                                                )
                                            )
                                        ),
                                        isHighlighted: false
                                    ),
                                ]
                            )
                        ]
                    ),
                    TournamentStage(
                        id: "stage-2",
                        groups: [
                            TournamentStageGroup(
                                title: nil,
                                tiles: [
                                    TournamentTile(
                                        title: "Sun, 12:00pm. Lusail Stadium",
                                        data: .game(
                                            TournamentTile.Game(
                                                id: "game-6",
                                                phase: nil,
                                                status: nil,
                                                sport: nil,
                                                ticketViewModel: nil,
                                                homeTeam: nil,
                                                awayTeam: nil
                                            )
                                        ),
                                        isHighlighted: false
                                    )
                                ]
                            )
                        ]
                    ),
                ],
                tbdString: "TBD",
                liveString: "Live",
                refresh: {
                    /// sleep for 2 seconds to simulate a request being made to the server
                    try? await Task.sleep(seconds: 2)
                }
            )
            .onTapGesture {
                offset = (offset + 1) % 3
            }
        }
    }
}
