plugins {
    id("com.android.library")
    kotlin("multiplatform")

    id("org.jetbrains.compose") version "1.5.3"

    // id("dev.icerock.mobile.multiplatform-resources")
}

/*
multiplatformResources {
    multiplatformResourcesPackage = "de.gematik.common"
}
 */

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    iosArm64().binaries.framework {
        baseName = "shared"
        isStatic = true
    }

    sourceSets {
        val ktorVersion = "2.3.4"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

                // check if all components are really necessary
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                // Shared Preferences for multiplatform
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        /*
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
        */

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("androidx.activity:activity-compose:1.8.1")
            }

        }
    }
}

/*
dependencies {
    commonMainApi("dev.icerock.moko:resources:0.22.0")
    commonMainApi("dev.icerock.moko:resources-compose:0.22.0") // for compose multiplatform

    commonTestImplementation("dev.icerock.moko:resources-test:0.22.0") // for testing
}
 */

android {
    namespace = "de.gematik.gsia"
    compileSdk = 33
    defaultConfig {
        minSdk = 28
    }

    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}