plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("athletic-test") {
            id = "com.theathletic.plugin.test"
            implementationClass = "com.theathletic.plugin.TestPlugin"
        }

        register("athletic-config") {
            id = "com.theathletic.plugin.config"
            implementationClass = "com.theathletic.plugin.ConfigPlugin"
        }

        register("athletic-compose") {
            id = "com.theathletic.plugin.compose"
            implementationClass = "com.theathletic.plugin.ComposePlugin"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
}