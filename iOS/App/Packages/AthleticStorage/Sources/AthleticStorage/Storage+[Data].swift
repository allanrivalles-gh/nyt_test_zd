import Foundation

extension Storage {
    /// Save an array of Data objects to storage
    ///
    /// - Parameters:
    ///   - value: array of Data to store to storage
    ///   - directory: user directory to store the files in
    ///   - path: folder location to store the data files (i.e. "Folder/")
    /// - Throws: Error if there were any issues creating a folder and writing the given [Data] to files in it
    public static func save(_ value: [Data], to directory: Directory, as path: String) throws {
        do {
            let folderUrl = try createURL(for: path, in: directory)
            try createSubfoldersBeforeCreatingFile(at: folderUrl)
            try FileManager.default.createDirectory(
                at: folderUrl,
                withIntermediateDirectories: false,
                attributes: nil
            )
            for i in 0..<value.count {
                let data = value[i]
                let dataName = "\(i)"
                let dataUrl = folderUrl.appendingPathComponent(dataName, isDirectory: false)
                try data.write(to: dataUrl, options: .atomic)
            }
        } catch {
            throw error
        }
    }

    /// Append a file with Data to a folder
    ///
    /// - Parameters:
    ///   - value: Data to store to storage
    ///   - directory: user directory to store the file in
    ///   - path: folder location to store the data files (i.e. "Folder/")
    /// - Throws: Error if there were any issues writing the given data to storage
    public static func append(_ value: Data, to path: String, in directory: Directory) throws {
        do {
            if let folderUrl = try? getExistingFileURL(for: path, in: directory) {
                let fileUrls = try FileManager.default.contentsOfDirectory(
                    at: folderUrl,
                    includingPropertiesForKeys: nil,
                    options: []
                )
                var largestFileNameInt = -1
                for i in 0..<fileUrls.count {
                    let fileUrl = fileUrls[i]
                    if let fileNameInt = fileNameInt(fileUrl) {
                        if fileNameInt > largestFileNameInt {
                            largestFileNameInt = fileNameInt
                        }
                    }
                }
                let newFileNameInt = largestFileNameInt + 1
                let data = value
                let dataName = "\(newFileNameInt)"
                let dataUrl = folderUrl.appendingPathComponent(dataName, isDirectory: false)
                try data.write(to: dataUrl, options: .atomic)
            } else {
                let array = [value]
                try save(array, to: directory, as: path)
            }
        } catch {
            throw error
        }
    }

    /// Append an array of data objects as files to a folder
    ///
    /// - Parameters:
    ///   - value: array of Data to store to storage
    ///   - directory: user directory to create folder with data objects
    ///   - path: folder location to store the data files (i.e. "Folder/")
    /// - Throws: Error if there were any issues writing the given Data
    public static func append(_ value: [Data], to path: String, in directory: Directory) throws {
        do {
            if let _ = try? getExistingFileURL(for: path, in: directory) {
                for data in value {
                    try append(data, to: path, in: directory)
                }
            } else {
                try save(value, to: directory, as: path)
            }
        } catch {
            throw error
        }
    }

    /// Retrieve an array of Data objects from storage
    ///
    /// - Parameters:
    ///   - path: path of folder that's holding the Data objects' files
    ///   - directory: user directory where folder was created for holding Data objects
    ///   - type: here for Swifty generics magic, use [Data].self
    /// - Returns: [Data] from storage
    /// - Throws: Error if there were any issues retrieving the specified folder of files
    public static func retrieve(_ path: String, from directory: Directory, as type: [Data].Type)
        throws -> [Data]
    {
        do {
            let url = try getExistingFileURL(for: path, in: directory)
            let fileUrls = try FileManager.default.contentsOfDirectory(
                at: url,
                includingPropertiesForKeys: nil,
                options: []
            )
            let sortedFileUrls = fileUrls.sorted(by: { (url1, url2) -> Bool in
                if let fileNameInt1 = fileNameInt(url1), let fileNameInt2 = fileNameInt(url2) {
                    return fileNameInt1 <= fileNameInt2
                }
                return true
            })
            var dataObjects = [Data]()
            for i in 0..<sortedFileUrls.count {
                let fileUrl = sortedFileUrls[i]
                let data = try Data(contentsOf: fileUrl)
                dataObjects.append(data)
            }
            return dataObjects
        } catch {
            throw error
        }
    }
}
