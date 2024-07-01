//
//  ImageView.swift
//
//
//  Created by Kyle Browning on 5/13/22.
//

import SwiftUI

public struct ImageView: View {
    let imageConfig: ImageConfig

    public var body: some View {
        switch imageConfig {
        case .system(let name, let contentMode):
            Image(systemName: name)
                .resizable()
                .aspectRatio(contentMode: contentMode)
        case .custom(let name, let contentMode):
            Image(name)
                .resizable()
                .aspectRatio(contentMode: contentMode)
        case .customWithBundle(let name, let bundle, let contentMode):
            Image(name, bundle: bundle)
                .resizable()
                .aspectRatio(contentMode: contentMode)
        }
    }
}
