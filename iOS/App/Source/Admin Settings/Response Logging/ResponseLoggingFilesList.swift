//
//  ResponseLoggingFilesList.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 8/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct ResponseLoggingFilesList: View {

    @Environment(\.dismiss) private var dismiss

    @StateObject var viewModel: ResponseLoggingFilesListViewModel

    var body: some View {
        if viewModel.containsLogFiles {
            content
                .searchable(
                    text: $viewModel.searchText,
                    placement: .navigationBarDrawer(displayMode: .always)
                )
        } else {
            content
        }
    }

    var content: some View {
        VStack {
            if viewModel.loadingState == .loaded {
                filter
                list
            }
        }
        .foregroundColor(.chalk.dark700)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .navigationBarDefaultBackgroundColor()
        .navigationTitle(viewModel.title)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(
                    action: {
                        Task {
                            await viewModel.updateFileList()
                        }
                    }
                ) {
                    Image(systemName: "arrow.clockwise")
                }
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(
                    action: {
                        viewModel.deleteFolder()
                        dismiss()
                    }
                ) {
                    Image(systemName: "trash")
                }
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                ShareLink(
                    item: viewModel,
                    preview: SharePreview(
                        "Network Diagnostic Logs - Zipped",
                        image: Image(systemName: "square.and.arrow.up")
                    )
                )
            }
        }
        .overlay {
            EmptyContent(
                state: viewModel.loadingState,
                errorMessage: "Failed to read log directory"
            )
        }
        .task {
            await viewModel.updateFileList()
        }
    }

    private var filter: some View {
        Group {
            if viewModel.containsLogFiles {
                Toggle(isOn: $viewModel.shouldShowSocketMessage) {
                    Text("Show Socket Messages")
                        .fontStyle(.calibreUtility.l.regular)
                }
                .padding(.horizontal, 20)
            }
        }
    }

    private var list: some View {
        List {
            if viewModel.files.isEmpty {
                VStack {
                    Text(
                        viewModel.searchText.isEmpty
                            ? "No Logs"
                            : "No Search Results for \"\(viewModel.searchText)\""
                    )
                    .fontStyle(.calibreUtility.l.medium)
                    Spacer()
                }
                .padding(.vertical, 16)
            } else {
                let _ = DuplicateIDLogger.logDuplicates(in: viewModel.files)
                ForEach(viewModel.files) { file in
                    FileRow(file: file)
                }
            }
        }
    }
}

private struct FileRow: View {
    let file: ResponseLoggingFilesListViewModel.File

    var body: some View {
        NavigationLink(
            screen: .account(
                .adminSettings(
                    .diagnostics(
                        file.isDirectory ? .filesList(file.url) : .fileDetail(file.url)
                    )
                )
            )
        ) {
            if file.isDirectory {
                Text(file.name)
                    .fontStyle(.calibreUtility.xl.regular)
            } else {
                HStack(spacing: 10) {
                    ZStack {
                        timeStringShortTextSizingGuide.opacity(0)
                        timeStringShortText(file.timeStringShort)
                    }
                    VStack(alignment: .leading) {
                        Text(file.displayName)
                            .fontStyle(.calibreUtility.xl.medium)
                        Text(file.timeString)
                            .fontStyle(.calibreUtility.xs.regular)
                            .foregroundColor(.chalk.dark600)
                        if let subtitle = file.displaySubtitle {
                            Text(subtitle)
                                .fontStyle(.calibreUtility.s.regular)
                                .foregroundColor(.chalk.dark600)
                        }
                    }
                }
            }
        }
    }

    private var timeStringShortTextSizingGuide: some View {
        timeStringShortText("00:00:00")
    }

    private func timeStringShortText(_ text: String) -> some View {
        Text(text)
            .fontStyle(.calibreHeadline.s.medium)
            .foregroundColor(.chalk.yellow)
    }

}

struct ResponseLoggingFilesList_Previews: PreviewProvider {
    static var previews: some View {
        ResponseLoggingFilesList(viewModel: ResponseLoggingFilesListViewModel(root: nil))
    }
}
