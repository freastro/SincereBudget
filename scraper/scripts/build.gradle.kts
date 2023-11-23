plugins {
    alias(libs.plugins.multiplatform)
}

dependencies {
    commonMainImplementation(project(":scraper:common"))
    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotlinx.html)
}

kotlin {
    js {
        binaries.executable()
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
            webpackTask {
                mainOutputFileName = "scraper.js"
                output.library = "Scraper"
                output.libraryTarget = "var"
            }
        }
    }
}
