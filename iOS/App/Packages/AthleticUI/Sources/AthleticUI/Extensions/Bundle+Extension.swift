//
//  Bundle+Extension.swift
//
//
//  Created by Kyle Browning on 5/11/22.
//

import Foundation

internal class CurrentBundleFinder {}

extension Foundation.Bundle {

    public static var athleticUI: Bundle = {
        /* The name of your local package, prepended by "LocalPackages_" */
        let bundleName = "AthleticUI_AthleticUI"
        let candidates = [
            /* Bundle should be present here when the package is linked into an App. */
            Bundle.main.resourceURL,
            /* Bundle should be present here when the package is linked into a framework. */
            Bundle(for: CurrentBundleFinder.self).resourceURL,
            /* For command-line tools. */
            Bundle.main.bundleURL,
            /* Bundle should be present here when running previews from a different package (this is the path to "…/Debug-iphonesimulator/"). */
            Bundle(for: CurrentBundleFinder.self).resourceURL?.deletingLastPathComponent()
                .deletingLastPathComponent(),
            Bundle(for: CurrentBundleFinder.self).resourceURL?.deletingLastPathComponent(),
        ]
        for candidate in candidates {
            let bundlePath = candidate?.appendingPathComponent(bundleName + ".bundle")
            if let bundle = bundlePath.flatMap(Bundle.init(url:)) {
                return bundle
            }
        }
        fatalError("unable to find bundle named \(bundleName) in \(candidates)")
    }()
}
