//
//  CompassExperimentsSettingsList.swift
//  theathletic-ios
//
//  Created by Kyle Browning on 12/15/22.
//  Copyright © 2022 The Athletic. All rights reserved.
//

import AthleticFoundation
import SwiftUI

struct CompassExperimentsSettingsList: View {
    @EnvironmentObject var compass: Compass

    var body: some View {
        List {
            let _ = DuplicateIDLogger.logDuplicates(
                in: compass.config.experiments,
                id: \.id
            )
            ForEach(compass.config.experiments, id: \.id) { experiment in
                let customBinding = Binding(
                    get: {
                        experiment.variant.rawValue
                    },
                    set: {
                        let newValue = CompassExperimentVariant(rawValue: $0)
                        let experimentId = experiment.id
                        self.compass.updateOverridenExperiments(id: experimentId, value: newValue)
                    }
                )
                Picker(experiment.id, selection: customBinding) {
                    // Add a blank selection option value to clear
                    Text("").tag("")
                    let _ = DuplicateIDLogger.logDuplicates(
                        in: CompassExperimentVariant.allCases,
                        id: \.self
                    )
                    ForEach(CompassExperimentVariant.allCases, id: \.self) { variant in
                        Text(variant.rawValue).tag(variant.rawValue)
                    }
                }
                .padding([.top, .bottom], 8)
            }
        }
        .fontStyle(.calibreUtility.l.regular)
        .foregroundColor(.chalk.dark800)
        .navigationTitle("Experiments")
        .navigationBarDefaultBackgroundColor()
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct CompassExperimentsSettingsList_Previews: PreviewProvider {
    static var previews: some View {
        CompassExperimentsSettingsList()
    }
}
