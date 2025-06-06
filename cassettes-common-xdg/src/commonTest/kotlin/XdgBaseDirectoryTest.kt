/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

import assertk.Assert
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.tableOf
import at.released.cassettes.test.ignore.annotations.IgnoreJs
import at.released.cassettes.test.ignore.annotations.IgnoreWasmJs
import kotlinx.io.files.Path
import kotlin.test.Test

@IgnoreWasmJs // TODO
@IgnoreJs // TODO
class XdgBaseDirectoryTest {
    @Test
    fun getBaseDataDirectories_should_return_xdg_data_home_and_xdg_data_dirs() {
        val envReader = TestPlatformXdgEnvReader(
            xdgDataHome = "/home/user/data",
            xdgDataDirs = "/usr/share/gnome:/usr/local/share/:/usr/share/:/var/lib/snapd/desktop",
        )

        val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
        val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

        assertThat(dataDirs).containsExactlyWithInvariantPath(
            "/home/user/data",
            "/usr/share/gnome",
            "/usr/local/share",
            "/usr/share",
            "/var/lib/snapd/desktop",
        )
    }

    @Test
    fun getBaseDataDirectories_directories_should_be_in_correct_priority_order() {
        val envReader = TestPlatformXdgEnvReader(
            xdgDataHome = "/path0",
            xdgDataDirs = "/path10:/path9:/path8:/path0:/path9:/path7:/path8:/path0",
        )

        val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
        val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

        assertThat(dataDirs).containsExactlyWithInvariantPath(
            "/path0",
            "/path10",
            "/path9",
            "/path8",
            "/path7",
        )
    }

    @Test
    fun getBaseDataDirectories_xdg_data_home_should_be_default_if_not_set_or_empty() {
        tableOf("xdgDataHome")
            .row<String?>(null)
            .row("")
            .forAll { xdgDataHome ->
                val envReader = TestPlatformXdgEnvReader(
                    xdgDataHome = xdgDataHome,
                    xdgDataDirs = "/opt/data",
                    realUserHome = "/home/user",
                )
                val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
                val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

                assertThat(dataDirs).containsExactlyWithInvariantPath("/home/user/.local/share", "/opt/data")
            }
    }

    @Test
    fun getBaseDataDirectories_xdg_data_home_should_be_empty_if_not_valid() {
        tableOf("xdgDataHome")
            .row(" ")
            .row("./data")
            .row("user/home")
            .row("not:a:path")
            .forAll { xdgDataHome ->
                val envReader = TestPlatformXdgEnvReader(
                    xdgDataHome = xdgDataHome,
                    xdgDataDirs = "/opt/data",
                    realUserHome = "/home/user",
                )
                val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
                val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

                assertThat(dataDirs).containsExactlyWithInvariantPath("/opt/data")
            }
    }

    @Test
    fun getBaseDataDirectories_xdg_data_dirs_should_be_default_if_not_set_or_empty() {
        tableOf("xdgDataDirs")
            .row<String?>(null)
            .row("")
            .forAll { xdgDataDirs ->
                val envReader = TestPlatformXdgEnvReader(
                    xdgDataHome = "/home/user/data",
                    xdgDataDirs = xdgDataDirs,
                )
                val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
                val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

                assertThat(dataDirs).containsExactlyWithInvariantPath(
                    "/home/user/data",
                    "/usr/local/share",
                    "/usr/share",
                )
            }
    }

    @Test
    fun getBaseDataDirectories_xdg_data_dirs_should_be_empty_if_not_valid() {
        tableOf("xdgDataDirs")
            .row(" ")
            .row("./data")
            .row("user/home")
            .row("not^;a;path::")
            .forAll { xdgDataDirs ->
                val envReader = TestPlatformXdgEnvReader(
                    xdgDataHome = "/home/user/data",
                    xdgDataDirs = xdgDataDirs,
                )
                val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
                val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

                assertThat(dataDirs).containsExactlyWithInvariantPath("/home/user/data")
            }
    }

    @Test
    fun getBaseDataDirectories_should_use_home_var_if_xdg_data_home_not_defined() {
        val envReader = TestPlatformXdgEnvReader(
            xdgDataHome = null,
            xdgDataDirs = "/opt/data",
            homeEnvVar = "/home/user",
            realUserHome = null,
        )

        val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
        val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

        assertThat(dataDirs).containsExactlyWithInvariantPath(
            "/home/user/.local/share",
            "/opt/data",
        )
    }

    @Test
    fun getBaseDataDirectories_should_use_real_home_if_home_invalid() {
        tableOf("homeEnvVar")
            .row<String?>(null)
            .row("")
            .row("home")
            .row("./data")
            .row("not^;a;path")
            .forAll { homeEnvVar ->
                val envReader = TestPlatformXdgEnvReader(
                    xdgDataHome = null,
                    xdgDataDirs = "/opt/data",
                    homeEnvVar = homeEnvVar,
                    realUserHome = "/usr/home/user",
                )

                val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
                val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

                assertThat(dataDirs).containsExactlyWithInvariantPath(
                    "/usr/home/user/.local/share",
                    "/opt/data",
                )
            }
    }

    @Test
    fun getBaseDataDirectories_should_be_empty_if_home_can_not_be_determined() {
        val envReader = TestPlatformXdgEnvReader(
            xdgDataHome = null,
            xdgDataDirs = "/opt/data",
            homeEnvVar = null,
            realUserHome = null,
        )

        val xdgBaseDirectory: XdgBaseDirectory = DefaultXdgBaseDirectory(envReader)
        val dataDirs = xdgBaseDirectory.getBaseDataDirectories().map(Path::toString)

        assertThat(dataDirs).containsExactlyWithInvariantPath("/opt/data")
    }

    private open class TestPlatformXdgEnvReader(
        private val xdgDataHome: String? = null,
        private val xdgDataDirs: String? = null,
        private val homeEnvVar: String? = "/home/user",
        private val realUserHome: String? = "/home/user",
    ) : PlatformXdgEnvReader {
        override fun getEnv(name: String): String? = when (name) {
            "HOME" -> homeEnvVar
            "XDG_DATA_HOME" -> xdgDataHome
            "XDG_DATA_DIRS" -> xdgDataDirs
            else -> null
        }

        override fun getUserHomeDirectory(): String? = realUserHome
    }

    private companion object {
        fun Assert<List<String>>.containsExactlyWithInvariantPath(vararg elements: String) {
            return this
                .transform { src -> src.map { it.replace('\\', '/') } }
                .containsExactly(*elements)
        }
    }
}
