/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

internal expect val platformXdgEnvReader: PlatformXdgEnvReader

internal interface PlatformXdgEnvReader {
    fun getEnv(name: String): String?
    fun getUserHomeDirectory(): String?
}
