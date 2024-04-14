rootProject.name = "SincereBudget"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.9.20")
            version("ktor", "2.3.5")

            plugin("compose", "org.jetbrains.compose").version("1.6.2")
            plugin("multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")

            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.7.3")
            library("kotlinx-html", "org.jetbrains.kotlinx", "kotlinx-html-js").version("0.9.1")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.6.0")
            library("selenium", "org.seleniumhq.selenium", "selenium-java").version("4.15.0")
        }
    }
}

rootProject.name = "SincereBudget"
include("api:iex-parser")
include("app")
include("scraper:common")
include("scraper:browser")
include("scraper:runner")
include("core")
