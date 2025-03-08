/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
}

group = "at.released.cassettes.gradle.lint"

dependencies {
    implementation(libs.detekt.plugin)
    implementation(libs.agp.plugin.api)
    implementation(libs.kotlinx.binary.compatibility.validator.plugin)
    implementation(libs.spotless.plugin)
}
