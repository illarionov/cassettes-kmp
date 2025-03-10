/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.framework

import at.released.cassettes.plugin.framework.fixtures.RootProjectFixtures
import java.io.File

public class RootProjectDsl private constructor(
    public val rootDir: File,
) {
    public fun writeFiles(
        vararg files: FileContent,
    ) {
        files.forEach {
            val dst = rootDir.resolve(it.dstPath)
            dst.parentFile.mkdirs()
            dst.writeText(it.content)
        }
    }

    internal companion object {
        public fun setupRoot(
            rootDir: File,
            vararg submodules: String,
        ) = RootProjectDsl(rootDir).apply {
            writeFiles(
                files = arrayOf(
                    RootProjectFixtures.getGradleProperties(),
                    RootProjectFixtures.getSettingsGradleKts(includeSubprojects = submodules),
                ),
            )
        }
    }
}
