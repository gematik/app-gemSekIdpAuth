plugins {
    kotlin("android").version("1.8.21").apply(false)
    kotlin("multiplatform").version("1.9.10").apply(false)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
}

buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.resources.generator)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
