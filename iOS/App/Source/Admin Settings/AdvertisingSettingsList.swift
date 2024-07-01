//
//  AdvertisingSettingsList.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import SwiftUI

struct AdvertisingSettingsList: View {
    @Preference(\.adminAdKeyword) private var adKeyword
    @Preference(\.adminOverrideGeoLocation) private var overrideGeoLocation
    @Preference(\.adminGeoLocationCountryCode) private var geoLocationCountryCode
    @Preference(\.adminGeoLocationState) private var geoLocationState
    @Preference(\.adminUseTestAdUnitPath) private var adminUseTestPath
    @Preference(\.adminRedirectMainFrameNav) private var adminRedirectMainFrameNav
    var body: some View {
        VStack {
            Text("Changes to these settings require an app restart.")
                .fontStyle(.calibreUtility.xs.regular)

            List {
                Section("Keyword") {
                    TextField("Keyword", text: $adKeyword)
                }

                Section("GeoLocation") {
                    Toggle(isOn: $overrideGeoLocation.animation()) {
                        Text("Override GeoLocation")
                    }
                }

                if overrideGeoLocation {
                    Section("GeoLocation Country Code") {
                        TextField("US", text: $geoLocationCountryCode)
                    }
                    Section("GeoLocation State") {
                        TextField("GeoLocation State", text: $geoLocationState)
                    }
                }

                Section("Ad Testing") {
                    Toggle(isOn: $adminUseTestPath) {
                        Text("Use testing version of adUnitPath")
                    }
                    Toggle(isOn: $adminRedirectMainFrameNav) {
                        Text("Redirect to Browser")
                    }
                }
            }
            .fontStyle(.calibreUtility.l.regular)
            .foregroundColor(.chalk.dark800)
        }
        .navigationTitle("Advertising")
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct AdvertisingSettingsList_Previews: PreviewProvider {
    static var previews: some View {
        AdvertisingSettingsList()
    }
}
