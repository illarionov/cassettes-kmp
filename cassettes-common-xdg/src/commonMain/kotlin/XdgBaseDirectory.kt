/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

import kotlinx.io.files.Path

/**
 * Simple helper for getting base directories according to the [XDG Base Directory Specification](https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html)
 */
public interface XdgBaseDirectory {
    public fun getBaseDataDirectories(): List<Path>

    public companion object : XdgBaseDirectory by DefaultXdgBaseDirectory()
}

internal class DefaultXdgBaseDirectory(
    private val envReader: PlatformXdgEnvReader = platformXdgEnvReader,
) : XdgBaseDirectory {
    override fun getBaseDataDirectories(): List<Path> = buildSet {
        getXdgDataHome()?.let { add(it) }
        addAll(getXdgDataDirs())
    }.toList()

    fun getXdgDataHome(): Path? {
        val dataHome = envReader.getEnv("XDG_DATA_HOME").toXdgAbsolutePath()
        return when (dataHome) {
            XdgPathResult.NullOrEmpty -> getUserHome()?.let { Path(it, ".local", "share") }
            XdgPathResult.NotValid -> null
            is XdgPathResult.Valid -> dataHome.absolutePath
        }
    }

    fun getUserHome(): Path? {
        val envUserHome = envReader.getEnv("HOME").toXdgAbsolutePath()
        if (envUserHome is XdgPathResult.Valid) {
            return envUserHome.absolutePath
        }

        val userHome = envReader.getUserHomeDirectory().toXdgAbsolutePath()
        return if (userHome is XdgPathResult.Valid) {
            userHome.absolutePath
        } else {
            null
        }
    }

    fun getXdgDataDirs(): List<Path> {
        val xdgDataDirs = envReader.getEnv("XDG_DATA_DIRS")
        if (xdgDataDirs.isNullOrEmpty()) {
            return DEFAULT_XDG_DATA_DIRS
        }
        return xdgDataDirs.splitToSequence(':')
            .mapNotNull {
                val xdgPathResult = it.toXdgAbsolutePath()
                if (xdgPathResult is XdgPathResult.Valid) {
                    xdgPathResult.absolutePath
                } else {
                    null
                }
            }
            .toList()
    }

    @Suppress("ConvertObjectToDataObject")
    private sealed class XdgPathResult {
        object NullOrEmpty : XdgPathResult()
        object NotValid : XdgPathResult()
        class Valid(val absolutePath: Path) : XdgPathResult()
    }

    private companion object {
        private val DEFAULT_XDG_DATA_DIRS = listOf(
            Path("/usr/local/share/"),
            Path("/usr/share/"),
        )

        private fun String?.toXdgAbsolutePath(): XdgPathResult {
            if (this.isNullOrEmpty()) {
                return XdgPathResult.NullOrEmpty
            }

            val path = Path(this) // XXX: no path validation

            return if (path.isAbsolute || path.toString().startsWith("/")) {
                XdgPathResult.Valid(path)
            } else {
                XdgPathResult.NotValid
            }
        }
    }
}
