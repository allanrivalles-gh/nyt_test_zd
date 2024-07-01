//
//  ResponseDiskWriter.swift
//
//
//  Created by Mark Corbyn on 12/1/2023.
//

import Apollo
import AthleticApolloTypes
import AthleticFoundation
import Foundation

public struct ResponseDiskWriter {

    private struct FileComponents {
        let url: URL
        let timeString: String
        let timeZoneString: String
    }

    private static var logger = ATHLogger(category: .apollo)

    public static let rootDirectory = URL.documentsDirectory
        .appending(component: "gqlResponses", directoryHint: .isDirectory)

    public static func directory(for date: Date) -> URL {
        fileComponents(for: date).url
    }

    static func write<Operation>(
        request: HTTPRequest<Operation>,
        response: HTTPResponse<Operation>
    ) {
        let variablesSummary = request.operation.variables?.compactMap({ key, value in
            if let value = value as? String {
                return "\(key)=\(value.replacingOccurrences(of: "/", with: "~"))"
            } else if let value = value as? Int {
                return "\(key)=\(value)"
            } else if let value = value as? Bool {
                return "\(key)=\(value)"
            } else if let value = value as? Date {
                return "\(key)=\(value)"
            } else if let value = value as? GQL.LeagueCode {
                return "\(key)=\(value.rawValue)"
            } else {
                return value != nil ? "\(key)=Object" : "\(key)=nil"
            }
        })
        .joined(separator: ",")

        let fileName = [
            request.operation.operationName,
            variablesSummary,
        ]
        .compactMap { $0 }
        .joined(separator: "_")

        let variablesDetails: String?
        if let variables = request.operation.variables,
            let serialized = try? JSONSerializationFormat.serialize(value: variables)
        {
            variablesDetails = serialized.prettyJson
        } else {
            variablesDetails = nil
        }

        let filePath = prepareDirectoriesAndFilePath(name: fileName)
        let stringData = String(bytes: response.rawData, encoding: .utf8) ?? "Invalid data"
        let contentsToWrite = [
            "Operation: \(request.operation.operationName)",
            "HTTP Request Variables:\n\(variablesDetails ?? "nil")",
            "HTTP Response:\n\(response.httpResponse)",
            "JSON:\n\(response.rawData.prettyJson ?? "nil")",
            "RawString:\n\(stringData)",
        ]
        .joined(separator: "\n\n===================================\n\n")

        do {
            try contentsToWrite.write(to: filePath, atomically: true, encoding: .utf8)
        } catch {
            logger.debug("\(error)")
        }
    }

    static func write(socketText: String) {
        let filePath: URL
        if let data = socketText.data(using: .utf8),
            let object = try? JSONSerialization.jsonObject(with: data, options: [])
                as? [String: Any],
            let messageType = object["type"] as? String,
            messageType == "data",
            let payload = object["payload"] as? [String: Any],
            let payloadData = payload["data"] as? [String: Any]
        {
            filePath = prepareDirectoriesAndFilePath(
                name: "socket-" + payloadData.keys.joined(separator: "-")
            )
        } else {
            filePath = prepareDirectoriesAndFilePath(name: "socket-message")
        }

        let contentsToWrite = [
            "JSON:\n\(socketText.data(using: .utf8)?.prettyJson ?? "nil")",
            "RawString:\n\(socketText)",
        ]
        .joined(separator: "\n===================================\n")

        do {
            try contentsToWrite.write(to: filePath, atomically: true, encoding: .utf8)
        } catch {
            logger.debug("\(error)")
        }
    }

    private static func prepareDirectoriesAndFilePath(name: String) -> URL {
        createRootDirectoryIfNeeded()

        let fileComponents = fileComponents(for: Date())

        if !FileManager.default.fileExists(atPath: fileComponents.url.path) {
            try? FileManager.default.createDirectory(
                at: fileComponents.url,
                withIntermediateDirectories: true,
                attributes: nil
            )
        }

        return fileComponents.url.appending(
            path: "\(fileComponents.timeString)\(fileComponents.timeZoneString)_\(name).json",
            directoryHint: .notDirectory
        )
    }

    /// Creates the root directory for storing the responses and sets the flag to not sync the content to iCloud.
    private static func createRootDirectoryIfNeeded() {
        guard !FileManager.default.fileExists(atPath: Self.rootDirectory.path) else {
            return
        }

        try? FileManager.default.createDirectory(
            at: Self.rootDirectory,
            withIntermediateDirectories: true,
            attributes: nil
        )

        do {
            try (Self.rootDirectory as NSURL).setResourceValue(
                NSNumber(value: true),
                forKey: .isExcludedFromBackupKey
            )
        } catch {
            logger.debug("Failed to exclude response directory from iCloud backup")
        }
    }

    private static func fileComponents(for date: Date) -> FileComponents {
        let dateComponents = Calendar.current.dateComponents(
            [.year, .month, .day, .hour, .minute, .second, .nanosecond, .timeZone],
            from: date
        )

        let year = dateComponents.year?.string ?? "YYYY"
        let month = dateComponents.month.map { String(format: "%02d", $0) } ?? "MM"
        let day = dateComponents.day.map { String(format: "%02d", $0) } ?? "DD"
        let hour = dateComponents.hour.map { String(format: "%02d", $0) } ?? "HH"
        let minute = dateComponents.minute.map { String(format: "%02d", $0) } ?? "mm"
        let second = dateComponents.second.map { String(format: "%02d", $0) } ?? "ss"
        let nanosecond = dateComponents.nanosecond?.string ?? "nnnn"
        let timeZoneOffset = dateComponents.timeZone?.secondsFromGMT() ?? 0
        let timeZonePlusMinus = timeZoneOffset >= 0 ? "+" : "-"
        let timeZoneOffsetHour = String(format: "%02d", abs(timeZoneOffset) / 3600)
        let timeZoneOffsetMinute = String(format: "%02d", (abs(timeZoneOffset) % 3600) / 60)

        let directoryPath =
            rootDirectory
            .appending(component: year, directoryHint: .isDirectory)
            .appending(component: month, directoryHint: .isDirectory)
            .appending(component: day, directoryHint: .isDirectory)
        let timeString = "\(hour):\(minute):\(second).\(nanosecond)"
        let timeZoneString = "\(timeZonePlusMinus)\(timeZoneOffsetHour):\(timeZoneOffsetMinute)"

        return FileComponents(
            url: directoryPath,
            timeString: timeString,
            timeZoneString: timeZoneString
        )
    }
}

extension Data {
    fileprivate var prettyJson: String? {
        guard
            let object = try? JSONSerialization.jsonObject(with: self, options: []),
            let data = try? JSONSerialization.data(
                withJSONObject: object,
                options: [.prettyPrinted]
            ),
            let prettyPrintedString = String(data: data, encoding: .utf8)
        else {
            return nil
        }

        return prettyPrintedString
    }
}
