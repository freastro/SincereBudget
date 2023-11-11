pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.9.20")

            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")

            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
        }
    }
}

rootProject.name = "SincereBudget"
include("core")
