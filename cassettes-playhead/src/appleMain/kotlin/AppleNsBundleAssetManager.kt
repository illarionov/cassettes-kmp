/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import at.released.cassettes.base.AssetUrl
import kotlinx.io.RawSource
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import platform.Foundation.NSBundle

/**
 * Reader from the main bundle.
 *
 * Requires placing resources into the test binary's output directory on the consumer side so that they can be accessed
 * using `NSBundle.mainBundle`.
 */
public class AppleNsBundleAssetManager(
    private val bundle: NSBundle = NSBundle.mainBundle,
    private val fileSystem: FileSystem = SystemFileSystem,
    private val wshohResourcesRoot: String = "wsoh-resources",
) : AssetManager {
    override fun getStorageCandidates(url: AssetUrl): List<AssetStorage> = listOf(NsBundleBinarySource(url))

    private inner class NsBundleBinarySource(
        private val url: AssetUrl,
    ) : AssetStorage {
        override val path: String = url.url

        override fun open(): RawSource {
            val fullPath = url.url
            val absolutePath = bundle.pathForResource(
                name = fullPath.substringBeforeLast("."),
                ofType = fullPath.substringAfterLast("."),
                inDirectory = wshohResourcesRoot,
            ) ?: throw FileNotFoundException("File not found in bundle: $fullPath. Bundle base: ${bundle.bundlePath}")
            return fileSystem.source(Path(absolutePath))
        }
    }
}
