pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }
}

include(":core")
include(":core:test")

include(":analytics")
include(":automation")
include(":mobile", ":annotation", ":codegen")

// Data modules
include(":data")
include(":api", ":api:graphql", ":api:rest")
include(":db")
include(":entity")

// Ui
include(":ui")

// Libraries
include(":lib:audio")

// Ads Modules
include(":ads")
include(":ads:models")
include(":ads:ui")

// Feature Modules
include(":brackets")
include(":scores", ":scores:boxscore")
include(":feed")
include(":featureintro")

include(":instrumentation")
include(":followables")

// Comments Modules
include(":comments")
include(":reader")

// Hub Modules - team, league and game
include(":hub")
include(":hub:game")

include(":profile")
include(":billing")
include(":podcast")
include(":links")
include(":location")
include(":slidestories")