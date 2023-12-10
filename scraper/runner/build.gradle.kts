plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":scraper:common"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                runtimeOnly(project(path = ":scraper:browser", configuration = "scraper"))
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.selenium)
            }
        }
    }
}
