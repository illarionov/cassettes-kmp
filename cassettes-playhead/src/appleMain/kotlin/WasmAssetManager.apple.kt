/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.playhead

public actual fun getDefaultAssetManager(): AssetManager = AppleFileManagerAssetManager()
