import ApolloCodegenLib
import ArgumentParser
import Foundation

struct SwiftScript: ParsableCommand {

    static let parentFolderOfScriptFile = FileFinder.findParentFolder()

    static let rootFolderURL = parentFolderOfScriptFile
        .apollo.parentFolderURL()
        .apollo.parentFolderURL()
        .apollo.parentFolderURL()

    static let outputURL = rootFolderURL
        .apollo.childFolderURL(folderName: "AthleticApolloTypes")
        .apollo.childFolderURL(folderName: "Sources")
        .apollo.childFolderURL(folderName: "AthleticApolloTypes")
        .apollo.childFolderURL(folderName: "Generated")

    static let schemaURL = rootFolderURL
        .apollo.childFolderURL(folderName: "AthleticApolloTypes")
        .apollo.childFolderURL(folderName: "Schema")

    static let operationIdsURL =
        outputURL
        .appendingPathComponent("operationIDs.json")

    static let cliFolderURL = rootFolderURL
        .apollo.childFolderURL(folderName: "AthleticApolloTypes")

    static let schemaPath = try! schemaURL.apollo.childFileURL(fileName: "schema.graphqls")

    static var configuration = CommandConfiguration(
        abstract: """
            A swift-based utility for performing Apollo-related tasks.

            NOTE: If running from a compiled binary, prefix subcommands with `swift-script`. Otherwise use `swift run ApolloCodegen [subcommand]`.
            """,
        subcommands: [DownloadSchema.self, GenerateCode.self, DownloadSchemaAndGenerateCode.self]
    )

    /// The sub-command to download a schema from a provided endpoint.
    struct DownloadSchema: ParsableCommand {
        static var configuration = CommandConfiguration(
            commandName: "downloadSchema",
            abstract:
                "Downloads the schema with the settings you've set up in the `DownloadSchema` command in `main.swift`."
        )

        func run() throws {

            let schemaDownloadOptions = ApolloSchemaDownloadConfiguration(
                using: .apolloRegistry(
                    ApolloSchemaDownloadConfiguration.DownloadMethod.ApolloRegistrySettings.init(
                        apiKey: "service:the-athletic:jPu9NZlqpkrp_6Pt9_ZgPg",
                        graphID: "the-athletic"
                    )
                ),
                outputFolderURL: schemaURL
            )

            try ApolloSchemaDownloader.fetch(with: schemaDownloadOptions)
        }
    }

    /// The sub-command to actually generate code.
    struct GenerateCode: ParsableCommand {
        static var configuration = CommandConfiguration(
            commandName: "generate",
            abstract:
                "Generates swift code from your schema + your operations based on information set up in the `GenerateCode` command."
        )

        func run() throws {

            // Get the root of the target for which you want to generate code.
            let targetRootURL = rootFolderURL

            // Make sure the folder exists before trying to generate code.
            try FileManager.default.apollo.createFolderIfNeeded(at: targetRootURL)

            // Create the Codegen options object. This default setup assumes `schema.json` is in the target root folder, all queries are in some kind of subfolder of the target folder and will output as a single file to `API.swift` in the target folder. For alternate setup options, check out https://www.apollographql.com/docs/ios/api/ApolloCodegenLib/structs/ApolloCodegenOptions/
            let codegenOptions: ApolloCodegenOptions = .init(
                mergeInFieldsFromFragmentSpreads: false,
                namespace: "GQL",
                operationIDsURL: operationIdsURL,
                outputFormat: .multipleFiles(inFolderAtURL: outputURL),
                customScalarFormat: .passthrough,
                urlToSchemaFile: schemaPath,
                downloadTimeout: 30.0
            )

            // Actually attempt to generate code.
            try ApolloCodegen.run(
                from: targetRootURL,
                with: cliFolderURL,
                options: codegenOptions
            )
        }
    }

    /// A sub-command which lets you download the schema then generate swift code.
    ///
    /// NOTE: This will both take significantly longer than code generation alone and fail when you're offline, so this is not recommended for use in a Run Phase Build script that runs with every build of your project.
    struct DownloadSchemaAndGenerateCode: ParsableCommand {
        static var configuration = CommandConfiguration(
            commandName: "all",
            abstract:
                "Downloads the schema and generates swift code. NOTE: Not recommended for use as part of a Run Phase Build Script."
        )

        func run() throws {
            try DownloadSchema().run()
            try GenerateCode().run()
        }
    }
}

// This will set up the command and parse the arguments when this executable is run.
SwiftScript.main()
