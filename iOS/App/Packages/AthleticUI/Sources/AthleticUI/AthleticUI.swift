import SwiftUI

public struct AthleticUI {
    public init() {
        AthleticUI.registerFonts()
    }

    public static func registerFonts() {
        AthleticFont.Name.allCases.forEach {
            registerFont(bundle: .module, fontName: $0.rawValue, fontExtension: "otf")
        }
    }

    fileprivate static func registerFont(bundle: Bundle, fontName: String, fontExtension: String) {
        guard let fontURL = bundle.url(forResource: fontName, withExtension: fontExtension)
        else {
            print(
                "Couldn't create font from filename: \(fontName) with extension \(fontExtension)"
            )
            return
        }
        var error: Unmanaged<CFError>?
        if !CTFontManagerRegisterFontsForURL(fontURL as CFURL, .process, &error) {
            print("unable to register font: \(error!.takeUnretainedValue())")
        }
    }
}
