/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class CassettesBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("cassettes", CassettesExtension::class.java)
    }
}
