//
//  TournamentBrackets.swift
//
//
//  Created by Leonardo da Silva on 01/11/22.
//

import AthleticFoundation
import SwiftUI

protocol TournamentBracketsStage: Identifiable {
    associatedtype Group: TournamentBracketsGroup

    var connected: Bool { get }
    var groups: [Group] { get }
}

protocol TournamentBracketsGroup {
    associatedtype Tile: Identifiable

    var title: String? { get }
    var tiles: [Tile] { get }
}

struct TournamentBrackets<Content: View, Stage: TournamentBracketsStage>: View {
    @State private var cellSize: CGSize?
    @State private var titleSize: CGSize?
    @State private var layout: BracketLayout<Stage.Group.Tile>?
    @State private var internalOffset: Int
    @State private var layoutProxy = BracketLayoutProxyPreferenceKey.defaultValue

    /// stages can not change after initial build or else connectors will not work properly
    private let stages: [Stage]
    private let horizontalSpacing: CGFloat
    private let verticalSpacing: CGFloat
    private let offset: Int
    private let minHeight: CGFloat?
    private let visibleColumnsCount: Int
    private let content: (Stage.Group.Tile) -> Content

    init(
        stages: [Stage],
        horizontalSpacing: CGFloat = 10,
        verticalSpacing: CGFloat = 10,
        offset: Int = 0,
        minHeight: CGFloat? = nil,
        visibleColumnsCount: Int = 1,
        @ViewBuilder content: @escaping (Stage.Group.Tile) -> Content
    ) {
        self.stages = stages
        self.horizontalSpacing = horizontalSpacing
        self.verticalSpacing = verticalSpacing
        self.offset = offset
        self.minHeight = minHeight
        self.visibleColumnsCount = visibleColumnsCount
        self.content = content
        _internalOffset = State(wrappedValue: offset)
    }

    var body: some View {
        ZStack(alignment: .topLeading) {
            if let layout {
                let _ = DuplicateIDLogger.logDuplicates(in: layout.connections)
                ForEach(layout.connections) { connection in
                    connection
                        .stroke(Color.chalk.dark400, lineWidth: 1)
                }
                let _ = DuplicateIDLogger.logDuplicates(in: layout.columns.flatMap { $0.cells })
                ForEach(layout.columns.flatMap { $0.cells }) { cell in
                    content(stages[cell.tileIndex])
                        .overlay(alignment: .topLeading) {
                            if let title = cell.title, let titleSize {
                                createTitle(title)
                                    .offset(y: -titleSize.height)
                            }
                        }
                        .offset(x: cell.offset.x, y: cell.offset.y)
                }
            } else {
                /// On the first render we will just measure the children to be able to calculate the layout.
                /// All children are rendered initially with an opacity of 0 to make sure
                /// we don't see them while they are not in the correct place.
                /// On iOS 16, this can be improved using the Layout protocol.
                if let title = stages.first?.groups.first?.title {
                    createTitle(title)
                        .padding(.bottom, 16)
                        .opacity(0)
                        .getSize { titleSize = $0 }
                } else {
                    Color.clear
                        .onAppear { titleSize = .zero }
                }
                if let tile = stages.first?.groups.first?.tiles.first {
                    content(tile)
                        .opacity(0)
                        .getSize { cellSize = $0 }
                } else {
                    Color.clear
                        .onAppear { cellSize = .zero }
                }
            }
        }
        .frame(height: effectiveHeight, alignment: .top)
        .offset(x: xOffset ?? 0)
        .onChange(of: offset) { updateLayout(offset: $0) }
        .onChange(of: cellSize) { updateLayout(cellSize: $0) }
        .onChange(of: titleSize) { updateLayout(titleSize: $0) }
        .preference(key: BracketLayoutProxyPreferenceKey.self, value: layoutProxy)
    }

    private var effectiveHeight: CGFloat? {
        guard let cellSize, let layout else { return nil }

        var height: CGFloat?
        let offsets = internalOffset..<(internalOffset + visibleColumnsCount)
        for offset in offsets where offset < layout.columns.count {
            let columnHeight = layout.columns[offset].height(cellHeight: cellSize.height)
            height = max(height ?? 0, columnHeight)
        }
        return height
    }

    private var xOffset: CGFloat? {
        guard let layout, !layout.columns.isEmpty else {
            return nil
        }

        return layout.columns[internalOffset].xOffset.map { -$0 }
    }

    /// Separated into a method to make sure both places the title is created don't get out of sync over time.
    /// Because if it did get out of sync, the layout calculation would produce incorrect result.
    private func createTitle(_ title: String) -> some View {
        Text(title)
            .fontStyle(.slab.s.bold)
            .foregroundColor(.chalk.dark800)
    }

    /// we need to inject the most up to the value, because if we just capture, it will be outdated
    private func updateLayout(
        offset: Int? = nil,
        cellSize: CGSize? = nil,
        titleSize: CGSize? = nil
    ) {
        let offset = offset ?? self.offset
        let cellSize = cellSize ?? self.cellSize
        let titleSize = titleSize ?? self.titleSize

        guard let cellSize, let titleSize else { return }

        let infos = stages.enumerated().map { (stageIndex, stage) in
            var cells = [BracketColumnLayout<Stage.Group.Tile>.CellInfo]()
            for (groupIndex, group) in stage.groups.enumerated() {
                for (tileIndex, tile) in group.tiles.enumerated() {
                    let index = TileIndex(stage: stageIndex, group: groupIndex, tile: tileIndex)
                    cells.append(
                        BracketColumnLayout<Stage.Group.Tile>.CellInfo(
                            tileId: tile.id,
                            tileIndex: index,
                            title: tileIndex == 0 ? group.title : nil
                        )
                    )
                }
            }
            return BracketLayout<Stage.Group.Tile>.ColumnInfo(
                /// We always want for the focused column to have the default spacing,
                /// that is why we only make the following columns relative.
                relative: stageIndex > offset,
                connected: stage.connected,
                cells: cells
            )
        }

        /// we don't want to animate the first time layout is set to avoid alpha fade in
        withAnimation(layout == nil ? nil : .default) {
            let layout = BracketLayout(
                infos,
                horizontalSpacing: horizontalSpacing,
                verticalSpacing: verticalSpacing,
                cellSize: cellSize,
                titleSize: titleSize
            )
            self.layout = layout
            internalOffset = offset

            /// the layout proxy needs to be updated whenever the layout changes
            /// luckily, at the moment, this is the only place it is being changed
            layoutProxy = BracketLayoutProxy { offset, collapsed in
                layout.columns[offset].height(
                    cellHeight: cellSize.height,
                    queryType: collapsed
                        ? .collapsed(
                            titleHeight: titleSize.height,
                            verticalSpacing: verticalSpacing
                        )
                        : .currentState
                )
            }
        }
    }
}

private struct TileIndex {
    let stage: Int
    let group: Int
    let tile: Int
}

extension Array where Element: TournamentBracketsStage {
    fileprivate subscript(_ index: TileIndex) -> Element.Group.Tile {
        self[index.stage].groups[index.group].tiles[index.tile]
    }
}

private struct BracketLayout<Tile: Identifiable> {
    struct ColumnInfo {
        let relative: Bool
        let connected: Bool
        let cells: [BracketColumnLayout<Tile>.CellInfo]
    }

    let columns: [BracketColumnLayout<Tile>]
    let connections: [Connection<Tile.ID>]

    init(
        _ infos: [ColumnInfo],
        horizontalSpacing: CGFloat,
        verticalSpacing: CGFloat,
        cellSize: CGSize,
        titleSize: CGSize
    ) {
        var lastColumn: BracketColumnLayout<Tile>?
        var wasLastColumnConnected = false
        var columns = [BracketColumnLayout<Tile>]()
        var connections = [Connection<Tile.ID>]()

        for info in infos {
            let column: BracketColumnLayout<Tile>
            /// we make sure both columns want to be connected to count as connected
            let isConnected = info.connected && wasLastColumnConnected
            /// if the column is not connected, we don't want to be relative to it
            if info.relative && isConnected, let lastColumn {
                column = BracketColumnLayout(
                    info.cells,
                    cellSize: cellSize,
                    relativeTo: lastColumn,
                    horizontalSpacing: horizontalSpacing
                )
            } else {
                column = BracketColumnLayout(
                    info.cells,
                    cellSize: cellSize,
                    previous: lastColumn,
                    horizontalSpacing: horizontalSpacing,
                    verticalSpacing: verticalSpacing,
                    titleSize: titleSize
                )
            }
            columns.append(column)
            if isConnected, let lastColumn {
                connections.append(
                    contentsOf: Self.createConnections(
                        from: column,
                        relativeTo: lastColumn,
                        cellSize: cellSize
                    )
                )
            }
            lastColumn = column
            wasLastColumnConnected = info.connected
        }

        self.columns = columns
        self.connections = connections
    }

    static private func createConnections(
        from column: BracketColumnLayout<Tile>,
        relativeTo other: BracketColumnLayout<Tile>,
        cellSize: CGSize
    ) -> [Connection<Tile.ID>] {
        column.cells.enumerated().map { (index, cell) in
            let previous = other.previousCells(forIndex: index)
            return Connection(
                id: cell.id,
                fromFirst: CGPoint(
                    x: previous.0.offset.x + cellSize.width,
                    y: previous.0.offset.y + cellSize.height / 2
                ),
                fromSecond: CGPoint(
                    x: previous.1.offset.x + cellSize.width,
                    y: previous.1.offset.y + cellSize.height / 2
                ),
                to: CGPoint(
                    x: cell.offset.x,
                    y: cell.offset.y + cellSize.height / 2
                )
            )
        }
    }
}

private struct BracketColumnLayout<Tile: Identifiable> {
    struct CellInfo {
        let tileId: Tile.ID
        let tileIndex: TileIndex
        let title: String?
    }

    struct Cell: Identifiable {
        var id: Tile.ID { tileId }

        let tileId: Tile.ID
        let tileIndex: TileIndex
        let title: String?
        let offset: CGPoint
    }

    let cells: [Cell]

    fileprivate enum HeightQueryType {
        case currentState
        case collapsed(titleHeight: CGFloat, verticalSpacing: CGFloat)
    }

    fileprivate func height(
        cellHeight: CGFloat,
        queryType: HeightQueryType = .currentState
    ) -> CGFloat {
        switch queryType {
        case .currentState:
            guard let cell = cells.last else { return 0 }
            return cell.offset.y + cellHeight
        case .collapsed(let titleHeight, let verticalSpacing):
            let cellsCount = CGFloat(cells.count)
            let titleCount = CGFloat(cells.filter { $0.title != nil }.count)
            var height: CGFloat = 0
            height += cellsCount * cellHeight
            height += titleCount * titleHeight
            height += (cellsCount - 1) * verticalSpacing
            return height
        }
    }

    var xOffset: CGFloat? {
        cells.first?.offset.x
    }

    /// `titleSize` should also include the spacing between the title and the cell
    init(
        _ infos: [CellInfo],
        cellSize: CGSize,
        previous other: BracketColumnLayout<Tile>?,
        horizontalSpacing: CGFloat,
        verticalSpacing: CGFloat,
        titleSize: CGSize
    ) {
        var lastCell: Cell?
        var cells = [Cell]()

        let minX =
            other.map { other in
                (other.cells.first.map { cell in
                    cell.offset.x + cellSize.width
                } ?? 0) + horizontalSpacing
            } ?? 0

        for info in infos {
            var minY = lastCell.map { $0.offset.y + cellSize.height + verticalSpacing } ?? 0
            if info.title != nil {
                minY += titleSize.height
            }
            let cell = Cell(
                tileId: info.tileId,
                tileIndex: info.tileIndex,
                title: info.title,
                offset: CGPoint(x: minX, y: minY)
            )
            cells.append(cell)
            lastCell = cell
        }

        self.cells = cells
    }

    init(
        _ infos: [CellInfo],
        cellSize: CGSize,
        relativeTo other: BracketColumnLayout<Tile>,
        horizontalSpacing: CGFloat
    ) {
        cells = infos.enumerated().map { (index, info) in
            let previous = other.previousCells(forIndex: index)
            let offset = CGPoint(
                x: previous.0.offset.x + cellSize.width + horizontalSpacing,
                y: (previous.0.offset.y + previous.1.offset.y) / 2
            )
            return Cell(
                tileId: info.tileId,
                tileIndex: info.tileIndex,
                title: info.title,
                offset: offset
            )
        }
    }

    fileprivate func previousCells(forIndex index: Int) -> (Cell, Cell) {
        let weightedIndex = index * 2
        return (cells[weightedIndex], cells[weightedIndex + 1])
    }
}

private struct Connection<TileID: Hashable & Sendable>: Shape, Identifiable, Animatable {
    let id: TileID
    var fromFirst: CGPoint
    var fromSecond: CGPoint
    var to: CGPoint

    func path(in rect: CGRect) -> Path {
        var path = Path()

        /// 1/3 of the way
        /// we do it this way to make sure the from cells connector is not visible after changing the page
        let midX = (fromFirst.x - to.x) / 3 * 2 + to.x

        path.move(to: to)
        path.addLine(to: CGPoint(x: midX, y: to.y))

        /// make sure to only render the from cells connector while the to cell is still reachable
        if fromFirst.y < to.y && fromSecond.y > to.y {
            path.move(to: fromFirst)
            path.addLine(to: CGPoint(x: midX, y: fromFirst.y))
            path.addLine(to: CGPoint(x: midX, y: fromSecond.y))
            path.addLine(to: fromSecond)
        }

        return path
    }

    typealias AnimatableData = AnimatablePair<
        AnimatablePair<
            AnimatablePair<CGFloat, CGFloat>,
            AnimatablePair<CGFloat, CGFloat>
        >,
        AnimatablePair<CGFloat, CGFloat>
    >

    var animatableData: AnimatableData {
        get {
            AnimatablePair(
                AnimatablePair(
                    AnimatablePair(fromFirst.x, fromFirst.y),
                    AnimatablePair(fromSecond.x, fromSecond.y)
                ),
                AnimatablePair(to.x, to.y)
            )
        }
        set {
            fromFirst.x = newValue.first.first.first
            fromFirst.y = newValue.first.first.second
            fromSecond.x = newValue.first.second.first
            fromSecond.y = newValue.first.second.second
            to.x = newValue.second.first
            to.y = newValue.second.second
        }
    }
}

private struct BracketLayoutProxyPreferenceKey: PreferenceKey {
    static let defaultValue = BracketLayoutProxy(constant: 0)

    static func reduce(value: inout BracketLayoutProxy, nextValue: () -> BracketLayoutProxy) {
        value = nextValue()
    }
}

struct BracketLayoutProxy: Equatable {
    /// Closures don't have identity in Swift, so we need to create a custom identifier.
    private let id = UUID()
    private var heightForColumn: (Int, Bool) -> CGFloat

    fileprivate init(heightForColumn: @escaping (Int, Bool) -> CGFloat) {
        self.heightForColumn = heightForColumn
    }

    fileprivate init(constant: CGFloat) {
        self.init(heightForColumn: { _, _ in constant })
    }

    func height(forColumn column: Int, collapsed: Bool = false) -> CGFloat {
        heightForColumn(column, collapsed)
    }

    static func == (lhs: BracketLayoutProxy, rhs: BracketLayoutProxy) -> Bool {
        lhs.id == rhs.id
    }
}

struct BracketLayoutReader<Content: View>: View {
    @State private var proxy = BracketLayoutProxyPreferenceKey.defaultValue
    private let content: (BracketLayoutProxy) -> Content

    init(@ViewBuilder content: @escaping (BracketLayoutProxy) -> Content) {
        self.content = content
    }

    var body: some View {
        content(proxy)
            .onPreferenceChange(BracketLayoutProxyPreferenceKey.self) { proxy in
                self.proxy = proxy
            }
    }
}

struct TournamentBrackets_Previews: PreviewProvider {
    private struct MinimalStage: TournamentBracketsStage {
        let id: Int
        let groups: [MinimalGroup]
        var connected: Bool { true }
    }

    private struct MinimalGroup: TournamentBracketsGroup {
        let title: String?
        let tiles: [MinimalTile]
    }

    private struct MinimalTile: Identifiable {
        var id: String { "\(stageId)-\(tileId)" }
        let stageId: Int
        let tileId: Int
    }

    static var previews: some View {
        Group {
            Preview()
            Preview()
                .darkScheme()
        }
    }

    struct Preview: View {
        var body: some View {
            TournamentBrackets(
                stages: [
                    MinimalStage(
                        id: -1,
                        groups: [
                            MinimalGroup(
                                title: nil,
                                tiles: [
                                    MinimalTile(stageId: -1, tileId: 0),
                                    MinimalTile(stageId: -1, tileId: 1),
                                    MinimalTile(stageId: -1, tileId: 2),
                                    MinimalTile(stageId: -1, tileId: 3),
                                    MinimalTile(stageId: -1, tileId: 4),
                                    MinimalTile(stageId: -1, tileId: 5),
                                    MinimalTile(stageId: -1, tileId: 6),
                                    MinimalTile(stageId: -1, tileId: 7),
                                    MinimalTile(stageId: -1, tileId: 8),
                                    MinimalTile(stageId: -1, tileId: 9),
                                    MinimalTile(stageId: -1, tileId: 10),
                                    MinimalTile(stageId: -1, tileId: 11),
                                    MinimalTile(stageId: -1, tileId: 12),
                                    MinimalTile(stageId: -1, tileId: 13),
                                    MinimalTile(stageId: -1, tileId: 14),
                                    MinimalTile(stageId: -1, tileId: 15),
                                ]
                            )
                        ]
                    ),
                    MinimalStage(
                        id: 0,
                        groups: [
                            MinimalGroup(
                                title: nil,
                                tiles: [
                                    MinimalTile(stageId: 0, tileId: 0),
                                    MinimalTile(stageId: 0, tileId: 1),
                                    MinimalTile(stageId: 0, tileId: 2),
                                    MinimalTile(stageId: 0, tileId: 3),
                                    MinimalTile(stageId: 0, tileId: 4),
                                    MinimalTile(stageId: 0, tileId: 5),
                                    MinimalTile(stageId: 0, tileId: 6),
                                    MinimalTile(stageId: 0, tileId: 7),
                                ]
                            )
                        ]
                    ),
                    MinimalStage(
                        id: 1,
                        groups: [
                            MinimalGroup(
                                title: nil,
                                tiles: [
                                    MinimalTile(stageId: 1, tileId: 0),
                                    MinimalTile(stageId: 1, tileId: 1),
                                    MinimalTile(stageId: 1, tileId: 2),
                                    MinimalTile(stageId: 1, tileId: 3),
                                ]
                            )
                        ]
                    ),
                    MinimalStage(
                        id: 2,
                        groups: [
                            MinimalGroup(
                                title: nil,
                                tiles: [
                                    MinimalTile(stageId: 2, tileId: 0),
                                    MinimalTile(stageId: 2, tileId: 1),
                                ]
                            )
                        ]
                    ),
                    MinimalStage(
                        id: 3,
                        groups: [
                            MinimalGroup(
                                title: nil,
                                tiles: [
                                    MinimalTile(stageId: 3, tileId: 0)
                                ]
                            )
                        ]
                    ),
                ],
                horizontalSpacing: 26,
                verticalSpacing: 18
            ) { tile in
                Text(String(tile.tileId))
                    .frame(width: 154, height: 100)
                    .background(Color.chalk.dark200)
            }
            .padding(16)
            .background(Color.chalk.dark100)
        }
    }
}
