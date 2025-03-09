/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.multiplatform.publish

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

/*
 * Convention plugin with publishing defaults
 */
plugins {
    id("at.released.cassettes.gradle.multiplatform.publish.base")
    id("org.jetbrains.kotlin.multiplatform")
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
        ),
    )

    pom {
        description.set("Assets for Kotlin Multiplatform projects")
    }
}
