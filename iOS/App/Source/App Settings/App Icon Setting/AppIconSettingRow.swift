//
//  AppIconSettingRow.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 8/19/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import AthleticAnalytics
import SwiftUI

struct AppIconSettingRow: View {
    let setting: AppIconSetting
    @Binding var selectedIcon: AppIconSetting?

    var body: some View {
        Button {
            let appIconName: String
            if setting.isDefault {
                UIApplication.shared.setAlternateIconName(nil)
                appIconName = "default"
            } else {
                UIApplication.shared.setAlternateIconName(setting.iconImageName)
                appIconName = setting.iconImageName
            }
            Analytics.track(
                event: .init(
                    verb: .click,
                    view: .appIconSetting,
                    element: .appIconSetting,
                    objectType: .appIconName,
                    objectIdentifier: appIconName
                )
            )
            self.selectedIcon = setting
        } label: {
            HStack {
                AppIcon(iconName: setting.iconImageName)
                    .frame(width: 54, height: 54)
                    .cornerRadius(5)
                Text(setting.title)
                    .padding(.leading, 8)
                Spacer()
                if selectedIcon == setting {
                    Image(systemName: "checkmark")
                }
            }
            .padding([.top, .bottom], 8)
        }
        .accentColor(Color.chalk.dark800)
    }
}

struct AppIconSettingRow_Previews: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            List {
                AppIconSettingRow(setting: .original, selectedIcon: .constant(nil))
            }
        }
    }
}
