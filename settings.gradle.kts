/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

pluginManagement {
    includeBuild("build-logic/settings")
    includeBuild("build-logic/project")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    id("at.released.cassettes.gradle.settings.root")
}

// Workaround for https://github.com/gradle/gradle/issues/26020
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.31.0")
        classpath("org.jetbrains.dokka:org.jetbrains.dokka.gradle.plugin:2.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
        classpath(
            "org.jetbrains.kotlinx.binary-compatibility-validator:" +
                    "org.jetbrains.kotlinx.binary-compatibility-validator.gradle.plugin:0.17.0",
        )
    }
}

rootProject.name = "cassettes-kmp"

include("aggregate-distribution")
include("cassettes-common-xdg")
include("doc:aggregate-documentation")
include("test-ignore-annotations")
include("cassettes-base")
include("cassettes-playhead")
include("cassettes-plugin")
