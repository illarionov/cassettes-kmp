/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.posix.getenv
import platform.posix.getpwuid
import platform.posix.getuid

internal actual val platformXdgEnvReader: PlatformXdgEnvReader = LinuxXdgEnvReader

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
internal object LinuxXdgEnvReader : PlatformXdgEnvReader {
    override fun getEnv(name: String): String? = getenv(name)?.toKString()

    override fun getUserHomeDirectory(): String? {
        val uid = getuid()
        // TODO: getpwuid is thread-unsafe
        return getpwuid(uid)?.let {
            it.pointed.pw_dir!!.toKString()
        }
    }
}
