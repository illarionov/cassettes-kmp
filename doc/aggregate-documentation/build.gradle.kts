/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask

/*
 * Module responsible for aggregating Dokka documentation from subprojects
 */
plugins {
    id("at.released.cassettes.gradle.documentation.dokka.base")
}

group = "at.released.cassettes"

private val websiteOutputDirectory = layout.buildDirectory.dir("outputs/website")

dokka {
    dokkaPublications.configureEach {
        moduleName.set("Cassettes KMP")
        includes.from("FRONTPAGE.md")
    }
}

val dokkaHtmlOutput = tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")
    .flatMap(DokkaGeneratePublicationTask::outputDirectory)

tasks.register<Sync>("buildWebsite") {
    description = "Assembles the final website from Dokka output"
    from(dokkaHtmlOutput)
    from(layout.projectDirectory.dir("root"))
    into(websiteOutputDirectory)
}

dependencies {
    dokka(projects.cassettesBase)
    dokka(projects.cassettesCommonXdg)
    dokka(projects.cassettesPlayhead)
    dokka(projects.cassettesPlugin)
}
