/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package at.released.cassettes.gradle.multiplatform.distribution

import at.released.cassettes.gradle.multiplatform.publish.createCassettesVersionsExtension
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register

private val rootVersion = createCassettesVersionsExtension().rootVersion

private val downloadableReleaseDirName: Provider<String> = rootVersion.map {
    "cassettes-kmp-$it"
}
private val distributionDir: Provider<Directory> = layout.buildDirectory.dir("distribution")
private val aggregateConfigurations = DistributionAggregationConfigurations(objects, configurations)

@Suppress("GENERIC_VARIABLE_WRONG_DECLARATION")
tasks.register<Zip>("foldDistribution") {
    archiveBaseName = "maven-cassettes-kmp"
    archiveVersion = rootVersion
    destinationDirectory = distributionDir

    from(aggregateConfigurations.mavenSnapshotAggregationFiles.get().asFileTree)
    into(downloadableReleaseDirName)

    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
