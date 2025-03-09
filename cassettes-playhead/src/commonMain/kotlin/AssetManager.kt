/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import at.released.cassettes.base.AssetUrl

internal expect fun getDefaultAssetManager(): AssetManager

public fun interface AssetManager {
    public fun getStorageCandidates(url: AssetUrl): List<AssetStorage>

    public companion object : AssetManager by getDefaultAssetManager()
}
