//
//  DatePickerSheetModifier.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 28/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

extension View {
    func datePickerSheet(
        _ title: String,
        isPresented: Binding<Bool>,
        selection: Binding<Date>
    ) -> some View {
        bottomSheet(
            isPresented: isPresented,
            prefersGrabberVisible: false,
            detents: [.medium]
        ) {
            VStack {
                Text(title)
                    .fontStyle(.calibreUtility.s.regular)
                    .foregroundColor(.chalk.dark400)
                Spacer()
                DatePicker(
                    selection: selection,
                    in: Date()...Date().add(years: 1),
                    displayedComponents: [.date]
                ) {
                    EmptyView()
                }
                .datePickerStyle(.wheel)
                Spacer()
                Button(Strings.ok.localized) {
                    isPresented.wrappedValue = false
                }
                .buttonStyle(.core(size: .regular, level: .primary))
            }
            .padding(24)
            .globalEnvironment()
        }
    }
}
