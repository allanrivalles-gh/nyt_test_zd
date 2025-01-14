#if os(macOS)
#else
    import Foundation
    import UIKit

    extension Storage {
        /// Save image to storage
        ///
        /// - Parameters:
        ///   - value: image to store to storage
        ///   - directory: user directory to store the image file in
        ///   - path: file location to store the data (i.e. "Folder/file.png")
        /// - Throws: Error if there were any issues writing the image to storage
        public static func save(_ value: UIImage, to directory: Directory, as path: String) throws {
            do {
                var imageData: Data
                if path.suffix(4).lowercased() == ".png" {
                    if let data = value.pngData() {
                        imageData = data
                    } else {
                        throw createError(
                            .serialization,
                            description: "Could not serialize UIImage to PNG.",
                            failureReason: "Data conversion failed.",
                            recoverySuggestion:
                                "Try saving this image as a .jpg or without an extension at all."
                        )
                    }
                } else if path.suffix(4).lowercased() == ".jpg"
                    || path.suffix(5).lowercased() == ".jpeg"
                {
                    if let data = value.jpegData(compressionQuality: 1) {
                        imageData = data
                    } else {
                        throw createError(
                            .serialization,
                            description: "Could not serialize UIImage to JPEG.",
                            failureReason: "Data conversion failed.",
                            recoverySuggestion:
                                "Try saving this image as a .png or without an extension at all."
                        )
                    }
                } else {
                    if let pngData = value.pngData() {
                        imageData = pngData
                    } else if let jpegData = value.jpegData(compressionQuality: 1) {
                        imageData = jpegData
                    } else {
                        throw createError(
                            .serialization,
                            description: "Could not serialize UIImage to Data.",
                            failureReason: "UIImage could not serialize to PNG or JPEG data.",
                            recoverySuggestion:
                                "Make sure image is not corrupt or try saving without an extension at all."
                        )
                    }
                }
                let url = try createURL(for: path, in: directory)
                try createSubfoldersBeforeCreatingFile(at: url)
                try imageData.write(to: url, options: .atomic)
            } catch {
                throw error
            }
        }

        /// Retrieve image from storage
        ///
        /// - Parameters:
        ///   - path: path where image is stored
        ///   - directory: user directory to retrieve the image file from
        ///   - type: here for Swifty generics magic, use UIImage.self
        /// - Returns: UIImage from storage
        /// - Throws: Error if there were any issues retrieving the specified image
        public static func retrieve(
            _ path: String,
            from directory: Directory,
            as type: UIImage.Type
        ) throws -> UIImage {
            do {
                let url = try getExistingFileURL(for: path, in: directory)
                let data = try Data(contentsOf: url)
                if let image = UIImage(data: data) {
                    return image
                } else {
                    throw createError(
                        .deserialization,
                        description: "Could not decode UIImage from \(url.path).",
                        failureReason:
                            "A UIImage could not be created out of the data in \(url.path).",
                        recoverySuggestion:
                            "Try deserializing \(url.path) manually after retrieving it as Data."
                    )
                }
            } catch {
                throw error
            }
        }
    }

#endif
