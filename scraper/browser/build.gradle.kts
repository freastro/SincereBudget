import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    alias(libs.plugins.multiplatform)
}

val scraper by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
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
                mainOutputFileName = "${scraper.name}.js"
                output.library = "Scraper"
                output.libraryTarget = "var"
            }
        }
    }
}

artifacts {
    val webpack = tasks.named<KotlinWebpack>("jsBrowserProductionWebpack").get()
    add(scraper.name, webpack.outputDirectory.get()) {
        builtBy(webpack)
    }
}
