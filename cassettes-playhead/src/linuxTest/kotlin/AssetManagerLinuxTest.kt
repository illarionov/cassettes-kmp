/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import assertk.assertThat
import assertk.assertions.isEqualTo
import at.released.cassettes.base.AssetUrl
import at.released.tempfolder.sync.TempDirectory
import at.released.tempfolder.sync.createTempDirectory
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import kotlin.test.AfterTest
import kotlin.test.Test

class AssetManagerLinuxTest {
    val tempFolder: TempDirectory<*> = createTempDirectory { prefix = "sqlite3binary" }

    @AfterTest
    fun cleanup() {
        tempFolder.delete()
    }

    @Test
    fun linuxWasmSourceReader_shouldReadPath() {
        val url = AssetUrl("wsohResources/resource.txt")
        val path = Path(tempFolder.append("testApp/${url.url}").asString())
        SystemFileSystem.run {
            createDirectories(path.parent!!)
            sink(path).buffered().use {
                it.writeString("Test Resource")
            }
        }

        val am = LinuxAssetManager(
            appName = "testApp",
            xdgBaseDirs = listOf(Path(tempFolder.absolutePath().asString())),
        )

        val resourceContent = am.readBytesOrThrow(url).decodeToString()
        assertThat(resourceContent).isEqualTo("Test Resource")
    }
}
