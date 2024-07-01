//
//  ResponseLoggingFileDetail.swift
//  theathletic-ios
//
//  Created by Mark Corbyn on 8/2/2023.
//  Copyright © 2023 The Athletic. All rights reserved.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct ResponseLoggingFileDetail: View {

    private let topLeftId = "top-left"

    @StateObject var viewModel: ResponseLoggingFileViewModel

    @State private var isTextWrappingEnabled: Bool = true

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView(isTextWrappingEnabled ? [.vertical] : [.vertical, .horizontal]) {
                VStack(alignment: .leading) {
                    Rectangle()
                        .frame(width: 1, height: 1)
                        .foregroundColor(.clear)
                        .id(topLeftId)

                    if viewModel.state == .loaded {
                        VStack(alignment: .leading) {
                            let _ = DuplicateIDLogger.logDuplicates(
                                in: Array(viewModel.contents.indices),
                                id: \.self
                            )
                            ForEach(viewModel.contents.indices, id: \.self) { index in
                                Text(viewModel.contents[index])
                                    .multilineTextAlignment(.leading)
                                    .fontStyle(.sohneData)
                                    .textSelection(.enabled)
                                    .overlay(alignment: .bottom) {
                                        DividerView(color: .chalk.dark300)
                                    }
                            }
                        }
                        .onAppear {
                            withAnimation {
                                proxy.scrollTo(topLeftId)
                            }
                        }
                        .onChange(of: isTextWrappingEnabled) { _ in
                            withAnimation {
                                proxy.scrollTo(topLeftId)
                            }
                        }
                    }
                }
            }
            .overlay {
                EmptyContent(
                    state: viewModel.state,
                    errorMessage: Strings.loadingFailed.localized
                )
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .foregroundColor(.chalk.dark700)
            .background(Color.chalk.dark100)
            .navigationBarDefaultBackgroundColor()
        }
        .task {
            await viewModel.load()
        }
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: { isTextWrappingEnabled.toggle() }) {
                    Image(
                        systemName: isTextWrappingEnabled
                            ? "arrow.left.arrow.right.square"
                            : "arrow.up.arrow.down.square"
                    )
                }
            }

            ToolbarItem(placement: .navigationBarTrailing) {
                ShareLink(
                    item: viewModel,
                    preview: SharePreview(
                        "Network Diagnostic Log File",
                        image: Image(systemName: "square.and.arrow.up")
                    )
                )
            }
        }
    }
}

struct ResponseLoggingFileDetail_Previews: PreviewProvider {
    static var previews: some View {
        ResponseLoggingFileDetail(
            viewModel: ResponseLoggingFileViewModel(fileUrl: URL(string: "")!)
        )
    }
}
