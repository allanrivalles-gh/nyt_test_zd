//
//  FeatureTourItemViewModel.swift
//
//
//  Created by Kyle Browning on 5/13/22.
//

import AthleticFoundation
import Foundation

public struct FeatureTourItemViewModel {
    public let id: String
    public let title: String
    public let subTitle: String
    public let imageConfig: ImageConfig
    public let callToAction: CallToAction?
    public let onNext: VoidClosure?
    public let onAppear: VoidClosure
    public let onDismiss: VoidClosure

    public struct CallToAction {
        public let title: String
        public let action: @MainActor () async -> Void

        public init(title: String, action: @MainActor @escaping () async -> Void) {
            self.title = title
            self.action = action
        }
    }

    public init(
        id: String,
        title: String,
        subTitle: String,
        imageConfig: ImageConfig,
        callToAction: CallToAction?,
        onNext: VoidClosure? = nil,
        onAppear: @escaping VoidClosure,
        onDismiss: @escaping VoidClosure
    ) {
        self.id = id
        self.title = title
        self.subTitle = subTitle
        self.imageConfig = imageConfig
        self.callToAction = callToAction
        self.onNext = onNext
        self.onAppear = onAppear
        self.onDismiss = onDismiss
    }
}
