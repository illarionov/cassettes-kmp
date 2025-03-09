/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import android.content.res.AssetManager
import at.released.cassettes.base.AssetUrl
import kotlinx.io.RawSource
import kotlinx.io.asSource

public class AndroidAssetsAssetManager(
    private val assertManager: AssetManager,
) : at.released.cassettes.playhead.AssetManager {
    private val jvmSourceReader = JvmResourcesAssetManager()

    override fun getStorageCandidates(url: AssetUrl): List<AssetStorage.Factory> {
        return if (!url.url.startsWith(ANDROID_ASSET_URL_PREFIX)) {
            jvmSourceReader.getStorageCandidates(url)
        } else {
            listOf(
                AssetStorage.Factory {
                    AndroidAssetsBinarySource(url, assertManager)
                },
            )
        }
    }

    private class AndroidAssetsBinarySource(
        private val url: AssetUrl,
        private val assertManager: AssetManager,
    ) : AssetStorage {
        override val path: String = url.url
        override fun open(): RawSource {
            val fileName = url.url.substringAfter(ANDROID_ASSET_URL_PREFIX)
            return assertManager.open(fileName).asSource()
        }
    }

    private companion object {
        private const val ANDROID_ASSET_URL_PREFIX = "file:///android_asset/"
    }
}
