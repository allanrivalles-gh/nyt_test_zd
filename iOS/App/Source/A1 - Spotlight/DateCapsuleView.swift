//
//  DateCapsuleView.swift
//  theathletic-ios
//
//  Created by Jason Leyrer on 12/9/21.
//  Copyright © 2021 The Athletic. All rights reserved.
//

import SwiftUI

struct DateCapsuleView: View {

    let date: Date

    var body: some View {
        Text(date.feedMonthDayString.uppercased())
            .fontStyle(.calibreUtility.xs.regular)
            .foregroundColor(.chalk.constant.gray700)
            .padding(.vertical, 2)
            .padding(.horizontal, 8)
            .background(Capsule().fill(Color.chalk.constant.gray400))
    }
}

struct DateCapsuleView_Previews: PreviewProvider {
    static var previews: some View {
        DateCapsuleView(date: Date())
    }
}
