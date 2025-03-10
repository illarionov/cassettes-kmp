/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.framework.fixtures

import java.io.File

object Paths {
    internal val userDir: String
        get() = System.getProperty("user.dir")
    public val testProjectsRoot: File
        get() = File(userDir, "src/testProjects")
    public val functionTestPluginRepository: File
        get() = File(userDir, "build/functional-tests-plugin-repository")

    internal fun File.withInvariantSeparator(): String = this.toString().replace('\\', '/')
}
