//
//  ResponseLoggingFileViewModel.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 10/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

final class ResponseLoggingFileViewModel: ObservableObject {

    @Published private(set) var state: LoadingState = .initial
    @Published private(set) var contents: [String] = []

    private let fileUrl: URL

    init(fileUrl: URL) {
        self.fileUrl = fileUrl
    }

    @MainActor
    func load() async {
        guard state == .initial else {
            return
        }

        state = .loading()

        if let contents = await readFileContents() {
            self.contents = await splitIntoChunks(string: contents)
            state = .loaded
        } else {
            state = .failed
        }
    }

    private func readFileContents() async -> String? {
        guard
            let data = FileManager.default.contents(atPath: fileUrl.path),
            let string = String(data: data, encoding: .utf8)
        else {
            return nil
        }

        return string
    }

    private func splitIntoChunks(string: String) async -> [String] {
        string.components(separatedBy: .newlines)
            .chunks(ofCount: 20)
            .map { Array($0) }
            .map { chunk in
                chunk.joined(separator: "\n")
            }
    }
}

extension ResponseLoggingFileViewModel: Transferable {
    static var transferRepresentation: some TransferRepresentation {
        FileRepresentation(exportedContentType: .json) { fileDetails in
            return SentTransferredFile(fileDetails.fileUrl)
        }
    }
}
