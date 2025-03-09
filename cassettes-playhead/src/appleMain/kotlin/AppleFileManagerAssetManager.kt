/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import at.released.cassettes.base.AssetUrl
import kotlinx.io.RawSource
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import platform.Foundation.NSFileManager

/**
 * Temporary reader directly from the `build/processedResource` until we find out how to embed resources into
 * the application
 */
public class AppleFileManagerAssetManager(
    private val fileSystem: FileSystem = SystemFileSystem,
    private val basePath: String = NSFileManager.defaultManager().currentDirectoryPath,
) : AssetManager {
    override fun getStorageCandidates(url: AssetUrl): List<AssetStorage.Factory> {
        val subpath = url.url
        return listOf(
            "$basePath/build/processedResources/macosX64/main/$subpath",
            "$basePath/src/macosMain/$subpath",
            "$basePath/src/macosTest/$subpath",
            "$basePath/src/commonMain/$subpath",
            "$basePath/src/commonTest/$subpath",
        ).map { path ->
            AssetStorage.Factory { AppleWasmBinarySource(path, fileSystem) }
        }
    }

    private class AppleWasmBinarySource(
        override val path: String,
        private val fileSystem: FileSystem = SystemFileSystem,
    ) : AssetStorage {
        override fun open(): RawSource {
            return fileSystem.source(Path(path))
        }
    }
}
