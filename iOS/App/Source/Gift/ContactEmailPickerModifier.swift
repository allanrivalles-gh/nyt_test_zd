//
//  ContactEmailPickerModifier.swift
//  theathletic-ios
//
//  Created by Leonardo da Silva on 28/09/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import ContactsUI
import SwiftUI

extension View {
    func contactEmailPicker(
        isPresented: Binding<Bool>,
        onPicked: @escaping (String?) -> Void
    ) -> some View {
        deeplinkListeningSheet(isPresented: isPresented) {
            ContactEmailPicker { email in
                onPicked(email)
                isPresented.wrappedValue = false
            }
        }
    }
}

private struct ContactEmailPicker: UIViewControllerRepresentable {
    let onContactEmailPicked: (String?) -> Void

    func makeCoordinator() -> Coordinator {
        return Coordinator(onContactEmailPicked: onContactEmailPicked)
    }

    func makeUIViewController(context: Context) -> some UIViewController {
        let controller = CNContactPickerViewController()
        controller.delegate = context.coordinator
        controller.predicateForEnablingContact = NSPredicate(
            format: "emailAddresses.@count > 0"
        )
        return controller
    }
}

extension ContactEmailPicker {
    final class Coordinator: NSObject, CNContactPickerDelegate {
        let onContactEmailPicked: ((String?) -> Void)

        init(onContactEmailPicked: @escaping (String?) -> Void) {
            self.onContactEmailPicked = onContactEmailPicked
        }

        func contactPicker(
            _ picker: CNContactPickerViewController,
            didSelect contact: CNContact
        ) {
            let email = contact.emailAddresses.first?.value as String?
            onContactEmailPicked(email)
        }
    }
}
