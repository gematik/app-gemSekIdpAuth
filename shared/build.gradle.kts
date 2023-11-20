plugins {
    id("com.android.library")
    kotlin("multiplatform")

    // kotlin("native.cocoapods")

    // id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"


    // need to be imported to put compose functions in common section
    id("org.jetbrains.compose") version "1.5.3"

    id("dev.icerock.mobile.multiplatform-resources")
}

// ./gradlew generateMRcommonMain kann keine MR für iOS erzeugen. Dafür ist ein Mac notwendig
multiplatformResources {
    multiplatformResourcesPackage = "de.gematik.common"
}

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

/* iOS app will be added in near future
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }
 */

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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {

            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("androidx.activity:activity-compose:1.7.2")
            }

        }

        /* source: https://ktor.io/docs/getting-started-ktor-client-multiplatform-mobile.html#ktor-dependencies
        // has to be tested
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        */
    }
}

dependencies {
    commonMainApi("dev.icerock.moko:resources:0.22.0")
    commonMainApi("dev.icerock.moko:resources-compose:0.22.0") // for compose multiplatform

    commonTestImplementation("dev.icerock.moko:resources-test:0.22.0") // for testing
}

android {
    namespace = "de.gematik.gsia"
    compileSdk = 33
    defaultConfig {
        minSdk = 28
    }

    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}