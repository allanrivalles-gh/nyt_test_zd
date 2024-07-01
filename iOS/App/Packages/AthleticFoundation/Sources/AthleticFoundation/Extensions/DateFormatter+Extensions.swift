//
//  DateFormatter+Extension.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 2/22/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import Foundation

extension DateFormatter {

    public static func localizedDateFormat(template: String) -> String {
        return DateFormatter.dateFormat(
            fromTemplate: template,
            options: 0,
            locale: Locale.current
        ) ?? template
    }

    public static var regionCode: String {
        guard let regionCode = Locale.current.language.region?.identifier else {
            return "US"
        }

        return regionCode
    }
}
