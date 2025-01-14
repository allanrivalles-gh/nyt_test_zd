import Foundation

extension Storage {
    /// Construct URL for a potentially existing or non-existent file
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory (set nil for entire directory)
    ///   - directory: directory for the specified path
    /// - Returns: URL for either an existing or non-existing file
    /// - Throws: Error if URL creation failed
    public static func url(for path: String?, in directory: Directory) throws -> URL {
        do {
            let url = try createURL(for: path, in: directory)
            return url
        } catch {
            throw error
        }
    }

    /// Clear directory by removing all files
    ///
    /// - Parameter directory: directory to clear
    /// - Throws: Error if FileManager cannot access a directory
    public static func clear(_ directory: Directory) throws {
        do {
            let url = try createURL(for: nil, in: directory)
            let contents = try FileManager.default.contentsOfDirectory(
                at: url,
                includingPropertiesForKeys: nil,
                options: []
            )
            for fileUrl in contents {
                try? FileManager.default.removeItem(at: fileUrl)
            }
        } catch {
            throw error
        }
    }

    /// Clear all files that were created before a given time interval
    ///
    /// - Parameter olderThan: `TimeInterval` for age comparison
    /// - Parameter folderPath: subfolder to clear
    /// - Parameter directory: root directory
    /// - Throws: Error if FileManager cannot access a directory
    public static func expire(
        lastModifiedPriorTo timeInterval: TimeInterval,
        folderPath: String,
        directory: Directory
    ) throws {
        do {
            let url = try createURL(for: folderPath, in: directory)
            let contents = try FileManager.default.contentsOfDirectory(
                at: url,
                includingPropertiesForKeys: nil,
                options: []
            )
            for fileUrl in contents {
                let attributes =
                    try FileManager.default.attributesOfItem(atPath: fileUrl.path)
                    as [FileAttributeKey: Any]

                if let modificationDate = attributes[.modificationDate] as? Date,
                    modificationDate.timeIntervalSinceNow < -timeInterval
                {
                    try FileManager.default.removeItem(at: fileUrl)
                }
            }
        } catch {
            throw error
        }
    }

    /// Remove file from the file system
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory where file is located
    /// - Throws: Error if file could not be removed
    public static func remove(_ path: String, from directory: Directory) throws {
        do {
            let url = try getExistingFileURL(for: path, in: directory)
            try FileManager.default.removeItem(at: url)
        } catch {
            throw error
        }
    }

    /// Remove file from the file system
    ///
    /// - Parameters:
    ///   - url: URL of file in filesystem
    /// - Throws: Error if file could not be removed
    public static func remove(_ url: URL) throws {
        do {
            try FileManager.default.removeItem(at: url)
        } catch {
            throw error
        }
    }

    /// Checks if a file exists
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory where file is located
    /// - Returns: Bool indicating whether file exists
    public static func exists(_ path: String, in directory: Directory) -> Bool {
        if let _ = try? getExistingFileURL(for: path, in: directory) {
            return true
        }
        return false
    }

    /// Checks if a file exists
    ///
    /// - Parameters:
    ///   - url: URL of file in filesystem
    /// - Returns: Bool indicating whether file exists
    public static func exists(_ url: URL) -> Bool {
        if FileManager.default.fileExists(atPath: url.path) {
            return true
        }
        return false
    }

    /// Sets the 'do not backup' attribute of the file or folder on storage to true. This ensures that the file holding the object data does not get deleted when the user's device has low storage, but prevents this file from being stored in any backups made of the device on iTunes or iCloud.
    /// This is only useful for excluding cache and other application support files which are not needed in a backup. Some operations commonly made to user documents will cause the 'do not backup' property to be reset to false and so this should not be used on user documents.
    /// Warning: You must ensure that you will purge and handle any files created with this attribute appropriately, as these files will persist on the user's storage even in low storage situtations. If you don't handle these files appropriately, then you aren't following Apple's file system guidlines and can face App Store rejection.
    /// Ideally, you should let iOS handle deletion of files in low storage situations, and you yourself handle missing files appropriately (i.e. retrieving an image from the web again if it does not exist on storage anymore.)
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory where file is located
    /// - Throws: Error if file could not set its 'isExcludedFromBackup' property
    public static func doNotBackup(_ path: String, in directory: Directory) throws {
        do {
            try setIsExcludedFromBackup(to: true, for: path, in: directory)
        } catch {
            throw error
        }
    }

    /// Sets the 'do not backup' attribute of the file or folder on storage to true. This ensures that the file holding the object data does not get deleted when the user's device has low storage, but prevents this file from being stored in any backups made of the device on iTunes or iCloud.
    /// This is only useful for excluding cache and other application support files which are not needed in a backup. Some operations commonly made to user documents will cause the 'do not backup' property to be reset to false and so this should not be used on user documents.
    /// Warning: You must ensure that you will purge and handle any files created with this attribute appropriately, as these files will persist on the user's storage even in low storage situtations. If you don't handle these files appropriately, then you aren't following Apple's file system guidlines and can face App Store rejection.
    /// Ideally, you should let iOS handle deletion of files in low storage situations, and you yourself handle missing files appropriately (i.e. retrieving an image from the web again if it does not exist on storage anymore.)
    ///
    /// - Parameters:
    ///   - url: URL of file in filesystem
    /// - Throws: Error if file could not set its 'isExcludedFromBackup' property
    public static func doNotBackup(_ url: URL) throws {
        do {
            try setIsExcludedFromBackup(to: true, for: url)
        } catch {
            throw error
        }
    }

    /// Sets the 'do not backup' attribute of the file or folder on storage to false. This is the default behaviour so you don't have to use this function unless you already called doNotBackup(name:directory:) on a specific file.
    /// This default backing up behaviour allows anything in the .documents and .caches directories to be stored in backups made of the user's device (on iCloud or iTunes)
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory where file is located
    /// - Throws: Error if file could not set its 'isExcludedFromBackup' property
    public static func backup(_ path: String, in directory: Directory) throws {
        do {
            try setIsExcludedFromBackup(to: false, for: path, in: directory)
        } catch {
            throw error
        }
    }

    /// Sets the 'do not backup' attribute of the file or folder on storage to false. This is the default behaviour so you don't have to use this function unless you already called doNotBackup(name:directory:) on a specific file.
    /// This default backing up behaviour allows anything in the .documents and .caches directories to be stored in backups made of the user's device (on iCloud or iTunes)
    ///
    /// - Parameters:
    ///   - url: URL of file in filesystem
    /// - Throws: Error if file could not set its 'isExcludedFromBackup' property
    public static func backup(_ url: URL) throws {
        do {
            try setIsExcludedFromBackup(to: false, for: url)
        } catch {
            throw error
        }
    }

    /// Move file to a new directory
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory the file is currently in
    ///   - newDirectory: new directory to store file in
    /// - Throws: Error if file could not be moved
    public static func move(_ path: String, in directory: Directory, to newDirectory: Directory)
        throws
    {
        do {
            let currentUrl = try getExistingFileURL(for: path, in: directory)
            let justDirectoryPath = try createURL(for: nil, in: directory).absoluteString
            let filePath = currentUrl.absoluteString.replacingOccurrences(
                of: justDirectoryPath,
                with: ""
            )
            let newUrl = try createURL(for: filePath, in: newDirectory)
            try createSubfoldersBeforeCreatingFile(at: newUrl)
            try FileManager.default.moveItem(at: currentUrl, to: newUrl)
        } catch {
            throw error
        }
    }

    /// Move file to a new directory
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory the file is currently in
    ///   - newDirectory: new directory to store file in
    /// - Throws: Error if file could not be moved
    public static func move(_ originalURL: URL, to newURL: URL) throws {
        do {
            try createSubfoldersBeforeCreatingFile(at: newURL)
            try FileManager.default.moveItem(at: originalURL, to: newURL)
        } catch {
            throw error
        }
    }

    /// Rename a file
    ///
    /// - Parameters:
    ///   - path: path of file relative to directory
    ///   - directory: directory the file is in
    ///   - newName: new name to give to file
    /// - Throws: Error if object could not be renamed
    public static func rename(_ path: String, in directory: Directory, to newPath: String) throws {
        do {
            let currentUrl = try getExistingFileURL(for: path, in: directory)
            let justDirectoryPath = try createURL(for: nil, in: directory).absoluteString
            var currentFilePath = currentUrl.absoluteString.replacingOccurrences(
                of: justDirectoryPath,
                with: ""
            )
            if isFolder(currentUrl) && currentFilePath.suffix(1) != "/" {
                currentFilePath = currentFilePath + "/"
            }
            let currentValidFilePath = try getValidFilePath(from: path)
            let newValidFilePath = try getValidFilePath(from: newPath)
            let newFilePath = currentFilePath.replacingOccurrences(
                of: currentValidFilePath,
                with: newValidFilePath
            )
            let newUrl = try createURL(for: newFilePath, in: directory)
            try createSubfoldersBeforeCreatingFile(at: newUrl)
            try FileManager.default.moveItem(at: currentUrl, to: newUrl)
        } catch {
            throw error
        }
    }

    /// Check if file at a URL is a folder
    public static func isFolder(_ url: URL) -> Bool {
        var isDirectory: ObjCBool = false
        if FileManager.default.fileExists(atPath: url.path, isDirectory: &isDirectory) {
            if isDirectory.boolValue {
                return true
            }
        }
        return false
    }

    public static func invalidateStore(
        for keyName: String,
        in directory: Directory = .applicationSupport
    ) throws {
        let filePath = StorageMetadata.getMetadataFilepath(for: keyName)
        if var metaData = try? Storage.retrieve(filePath, from: directory, as: StorageMetadata.self)
        {
            metaData.dateUpdated = 0
            try Storage.save(metaData, to: directory, as: filePath)
        }
    }
}
