//
//  MoreUnreadButton.swift
//
//
//  Created by Jason Leyrer on 8/10/23.
//

import AthleticFoundation
import AthleticUI
import SwiftUI

struct MoreUnreadButton: View {
    let action: VoidClosure

    var body: some View {
        Button {
            action()
        } label: {
            HStack(spacing: 8) {
                Group {
                    Image("action_button_arrow_down_dark")
                        .renderingMode(.template)
                        .frame(width: 16, height: 16)

                    Text(Strings.moreUnread.localized)
                }
                .foregroundColor(.chalk.dark200)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
        .buttonStyle(UpdateCapsuleButtonStyle())
    }
}

struct MoreUpdatesButton_Previews: PreviewProvider {
    static var previews: some View {
        MoreUnreadButton(action: {})
    }
}
