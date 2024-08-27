import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")

    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.androidLibrary)

    // id("dev.icerock.mobile.multiplatform-resources")
}

/*
multiplatformResources {
    multiplatformResourcesPackage = "de.gematik.common"
}
 */

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    // targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    val xcf = XCFramework()
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
        }
    }

    sourceSets {
        val ktorVersion = "2.3.4"

        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client)
                implementation(libs.kotlinx.coroutine)
                implementation(libs.compose.viewmodel)

                // check if all components are really necessary
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                // Shared Preferences for multiplatform
                implementation(libs.multiplatform.settings.no.arg)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.activity.compose)
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
    compileSdk = 34
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