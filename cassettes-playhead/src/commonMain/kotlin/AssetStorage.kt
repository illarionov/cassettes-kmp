/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

import kotlinx.io.RawSource

public interface AssetStorage {
    public val path: String

    public fun open(): RawSource
}
