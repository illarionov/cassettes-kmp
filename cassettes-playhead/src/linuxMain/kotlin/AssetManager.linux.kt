/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import at.released.cassettes.base.AssetUrl
import at.released.cassettes.common.xdg.XdgBaseDirectory
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

internal actual fun getDefaultAssetManager(): AssetManager = LinuxAssetManager()

public class LinuxAssetManager(
    private val appName: String = "wasm-sqlite-open-helper",
    private val xdgBaseDirs: List<Path> = XdgBaseDirectory.getBaseDataDirectories(),
    private val fileSystem: FileSystem = SystemFileSystem,
) : AssetManager {
    override fun getStorageCandidates(url: AssetUrl): List<AssetStorage.Factory> {
        return xdgBaseDirs.map { xdgBaseDir ->
            val candidate = Path(xdgBaseDir, appName, url.url)
            AssetStorage.Factory {
                object : AssetStorage {
                    override val path: String = candidate.toString()
                    override fun open(): Source = fileSystem.source(candidate).buffered()
                }
            }
        }
    }
}
