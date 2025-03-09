/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.multiplatform

import com.android.build.api.dsl.LibraryExtension

/*
 * Convention plugin that configures Android target in projects with the Kotlin Multiplatform plugin
 */
plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("at.released.cassettes.gradle.multiplatform.kotlin")
    id("at.released.cassettes.gradle.lint.android-lint")
    id("com.android.library")
}

kotlin {
    androidTarget {
        publishLibraryVariants = listOf("release")
    }
}

extensions.configure<LibraryExtension>("android") {
    compileSdk = versionCatalogs.named("libs").findVersion("androidCompileSdk").get().displayName.toInt()
    defaultConfig {
        minSdk = versionCatalogs.named("libs").findVersion("androidMinSdk").get().displayName.toInt()
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = false
    }
}
