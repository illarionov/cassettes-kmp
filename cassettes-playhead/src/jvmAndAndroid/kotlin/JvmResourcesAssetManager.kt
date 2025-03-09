/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import at.released.cassettes.base.AssetUrl
import kotlinx.io.RawSource
import kotlinx.io.asSource
import java.net.URI

public class JvmResourcesAssetManager : AssetManager {
    override fun getStorageCandidates(url: AssetUrl): List<AssetStorage> {
        return listOf(
            object : AssetStorage {
                override val path: String = url.toString()
                override fun open(): RawSource = URI(url.url).toURL().openStream().asSource()
            },
        )
    }
}
