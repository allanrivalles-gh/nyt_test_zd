//
//  ResponseLoggingFilesListViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 10/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticApolloNetworking
import AthleticFoundation
import Combine
import SwiftUI

final class ResponseLoggingFilesListViewModel: ObservableObject {

    private static let excludeFiles = [".DS_Store"]
    private static let logger = ATHLogger(category: .network)

    struct File: Identifiable {
        let name: String
        let url: URL
        let isDirectory: Bool

        var timeStringShort: String {
            String(name.prefix(8))
        }

        var timeString: String {
            String(name.prefix(while: { $0 != "_" }))
        }

        var displayName: String {
            let noExtension = (name as NSString).deletingPathExtension
            let components = noExtension.components(separatedBy: "_")
            if components.count >= 2 {
                return components[1]
            } else {
                return noExtension
            }
        }

        var displaySubtitle: String? {
            let noExtension = (name as NSString).deletingPathExtension
            let components = noExtension.components(separatedBy: "_")

            if components.count >= 3 {
                return components[2]
            } else {
                return nil
            }
        }

        var id: String {
            name
        }
    }

    let title: String

    private var allFiles: [File] = []

    @Published private(set) var loadingState: LoadingState = .initial
    @Published private(set) var files: [File] = []
    @Published var searchText: String = ""
    @Published var shouldShowSocketMessage = false {
        didSet {
            Task {
                await updateDisplayedFiles()
            }
        }
    }

    var containsLogFiles: Bool {
        allFiles.contains(where: { !$0.isDirectory })
    }

    private let rootUrl: URL
    private var searchCancellable: AnyCancellable?

    init(root: URL?) {
        self.rootUrl = root ?? ResponseDiskWriter.rootDirectory
        self.title = String(rootUrl.path[ResponseDiskWriter.rootDirectory.path.endIndex...])

        Task {
            await updateFileList()
        }

        searchCancellable = $searchText.dropFirst().sink { [weak self] _ in
            guard let self else { return }
            Task {
                await self.updateDisplayedFiles()
            }
        }
    }

    @MainActor
    func updateFileList() async {
        do {
            guard directoryExists() else {
                loadingState = .loaded
                return
            }

            allFiles = try fetchDirectory()
            updateDisplayedFiles()

            loadingState = .loaded

        } catch {
            Self.logger.debug(
                "Couldn't read contents of directory at \(rootUrl.path)"
            )

            loadingState = .failed
        }
    }

    @MainActor
    private func updateDisplayedFiles() {
        let trimmedSearchText = searchText.trimWhitespace()
        let searchWords = trimmedSearchText.components(separatedBy: " ").filter { !$0.isEmpty }

        files =
            allFiles
            .filter { file in
                let components = file.name.components(separatedBy: "_")
                if components[safe: 1]?.hasPrefix("socket-message") == true {
                    return shouldShowSocketMessage
                }

                return searchWords.allSatisfy { word in
                    file.name.localizedCaseInsensitiveContains(word)
                }
            }
    }

    func deleteFolder() {
        try? FileManager.default.removeItem(at: rootUrl)
    }

    private func directoryExists() -> Bool {
        FileManager.default.fileExists(atPath: rootUrl.path)
    }

    private func fetchDirectory() throws -> [File] {
        try FileManager.default.contentsOfDirectory(atPath: rootUrl.path)
            .filter { !Self.excludeFiles.contains($0) }
            .sorted(by: >)
            .map { filename in
                let url = rootUrl.appendingPathComponent(filename)
                return File(name: filename, url: url, isDirectory: url.hasDirectoryPath)
            }
    }
}

extension ResponseLoggingFilesListViewModel: Transferable {
    static var transferRepresentation: some TransferRepresentation {
        FileRepresentation(exportedContentType: .zip) { fileLists in
            let zippedFileName =
                "Logs\(fileLists.title.replacingOccurrences(of: "/", with: "-"))"
            let zippedFile = try zipToTempDirectory(
                forUrlAt: fileLists.rootUrl,
                with: zippedFileName
            )
            return SentTransferredFile(zippedFile)
        }
    }

    private static func zipToTempDirectory(
        forUrlAt url: URL,
        with zippedFileName: String
    ) throws -> URL {
        let zipDestinationUrl = FileManager.default.temporaryDirectory
            .appendingPathComponent(zippedFileName)
            .appendingPathExtension("zip")

        let coordinator = NSFileCoordinator()
        var error: NSError?

        coordinator.coordinate(
            readingItemAt: url,
            options: .forUploading,
            error: &error
        ) { zippedUrl in
            do {
                _ = try FileManager.default.replaceItemAt(
                    zipDestinationUrl,
                    withItemAt: zippedUrl
                )
            } catch {
                logger.debug("\(error)")
            }
        }

        if let error {
            throw error
        }

        return zipDestinationUrl
    }
}
