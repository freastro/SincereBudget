plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    js {
        browser()
    }
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
