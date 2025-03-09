/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.posix.getenv
import platform.windows.CoTaskMemFree
import platform.windows.FOLDERID_Profile
import platform.windows.PWSTRVar
import platform.windows.SHGetKnownFolderPath

internal actual val platformXdgEnvReader: PlatformXdgEnvReader = WindowsXdgEnvReader

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
internal object WindowsXdgEnvReader : PlatformXdgEnvReader {
    override fun getEnv(name: String): String? = getenv(name)?.toKString()

    override fun getUserHomeDirectory(): String? = memScoped {
        val out: PWSTRVar = this.alloc()
        val userProfile: String? = if (SHGetKnownFolderPath(
                rfid = FOLDERID_Profile.ptr,
                dwFlags = 0u,
                hToken = null,
                ppszPath = out.ptr,
            ) != 0
        ) {
            null
        } else {
            out.value?.toKString()
        }
        CoTaskMemFree(out.value)
        userProfile
    }
}
