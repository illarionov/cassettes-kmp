/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import at.released.cassettes.gradle.settings.repository.googleFiltered

pluginManagement {
    includeBuild("../settings")
}

plugins {
    id("at.released.cassettes.gradle.settings.root")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
    repositories {
        googleFiltered()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("documentation")
include("lint")
include("multiplatform")

rootProject.name = "cassettes-gradle-project-plugins"
