plugins {
    alias(libs.plugins.multiplatform)
}

kotlin {
    js {
        browser()
    }
    jvm()
}
