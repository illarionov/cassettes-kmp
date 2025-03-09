/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.multiplatform.test

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

/*
 * Convention plugin that configures unit tests in Kotlin Multiplatform projects for JVM targets
 */
plugins.withId("org.jetbrains.kotlin.multiplatform") {
    extensions.configure<KotlinMultiplatformExtension> {
        targets.withType<KotlinJvmTarget>().configureEach {
            testRuns.configureEach {
                executionTask.configure {
                    configureTestTaskDefaults(this)
                }
            }
        }
    }
}

tasks.withType<Test> {
    configureTestTaskDefaults(this)
}
