/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import assertk.assertThat
import assertk.assertions.isEqualTo
import at.released.cassettes.base.AssetUrl
import org.junit.Test

class JvmResourcesAssetManagerTest {
    @Test
    fun jvmWasmSourceReader_shouldReadResourcePath() {
        val url = requireNotNull(
            JvmResourcesAssetManagerTest::class.java.getResource("resource.txt"),
        ).toString()
        val wasmUrl = AssetUrl(url)

        val reader = JvmResourcesAssetManager()
        val resourceContent = reader.readBytesOrThrow(wasmUrl).decodeToString().trim()
        assertThat(resourceContent).isEqualTo("Test Resource")
    }
}
