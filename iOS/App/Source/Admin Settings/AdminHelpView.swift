//
//  AdminHelpView.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 8/13/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

struct AdminHelpView: View {
    @Binding var showingHelp: Bool

    var body: some View {

        VStack(alignment: .leading) {
            Text("Changes are automatically applied. Some require a restart")
                .font(.body)
            HStack {
                Circle()
                    .frame(width: 4, height: 4)
                    .foregroundColor(.chalk.red)
                Text("Setting requires restart")
            }.font(.callout)

            Spacer()
        }
        .padding()
    }
}
