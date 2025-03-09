/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import assertk.assertThat
import assertk.assertions.isEqualTo
import at.released.cassettes.base.AssetUrl
import at.released.tempfolder.sync.TempDirectory
import at.released.tempfolder.sync.createTempDirectory
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.test.AfterTest
import kotlin.test.Test

class AppleFileManagerAssetManagerTest {
    private val tempFolder: TempDirectory<*> = createTempDirectory { prefix = "wasm-binary-reader-" }
    private val fileSystem: FileSystem = SystemFileSystem

    @AfterTest
    fun cleanup() {
        tempFolder.delete()
    }

    @Test
    fun appleFileManagerSourceReader_shouldRedPath() {
        val url = AssetUrl("resource.txt")
        val path = Path(tempFolder.append("src/macosMain/${url.url}").asString())
        SystemFileSystem.run {
            createDirectories(path.parent!!)
            sink(path).buffered().use {
                it.writeString("Test Resource")
            }
        }

        val reader = AppleFileManagerAssetManager(
            fileSystem = fileSystem,
            basePath = tempFolder.absolutePath().asString(),
        )

        val resourceContent = reader.readBytesOrThrow(url).decodeToString()
        assertThat(resourceContent).isEqualTo("Test Resource")
    }
}
