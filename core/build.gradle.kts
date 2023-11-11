plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain {

        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
