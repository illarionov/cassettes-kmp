/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("at.released.cassettes.gradle.multiplatform.distribution.aggregate")
}

dependencies {
    mavenSnapshotAggregation(projects.cassettesBase)
    mavenSnapshotAggregation(projects.cassettesCommonXdg)
    mavenSnapshotAggregation(projects.cassettesPlayhead)
    mavenSnapshotAggregation(projects.cassettesPlugin)
}
