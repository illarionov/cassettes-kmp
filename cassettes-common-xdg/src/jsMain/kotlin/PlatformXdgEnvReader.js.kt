/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

internal actual val platformXdgEnvReader: PlatformXdgEnvReader = DummyPlatformXdgEnvReader

private object DummyPlatformXdgEnvReader : PlatformXdgEnvReader {
    override fun getEnv(name: String): String? = null
    override fun getUserHomeDirectory(): String? = null
}
