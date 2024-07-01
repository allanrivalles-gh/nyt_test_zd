plugins {
    id(Plugins.library)
    id(Plugins.apollo).version(Plugins.apollo_version)
}

android {
    namespace = "com.theathletic.api.graphql"
}

apollo {

    service("athletic") {
        useVersion2Compat()

        generateDataBuilders.set(true)

        srcDir("src/main/graphql/")
        excludes.add("**/schema.json.graphql")
        excludes.add("**/schema.json")

        mapScalar("Timestamp", "kotlin.Long")

        registry {
            key.set(System.getenv("APOLLO_KEY") ?: "")
            graph.set("the-athletic")
            // The path is interpreted relative to the current project here, no need to prepend 'app'
            schemaFile.set(file("src/main/graphql/com/theathletic/schema.graphqls"))
        }
    }
}

dependencies {
    // Apollo GraphQL
    // TODO api -> implementation when we move API classes to this module
    api(Dependencies.Apollo.runtime)
    api(Dependencies.Apollo.cache)
}