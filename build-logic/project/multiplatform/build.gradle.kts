/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
}

group = "at.released.cassettes.gradle.multiplatform"

dependencies {
    implementation(project(":lint"))
    implementation(project(":documentation"))
    implementation(libs.agp.plugin.api)
    runtimeOnly(libs.agp.plugin)
    implementation(libs.gradle.maven.publish.plugin)
    implementation(libs.kotlin.gradle.plugin)
}
