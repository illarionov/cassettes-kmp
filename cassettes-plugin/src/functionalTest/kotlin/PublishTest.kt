/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFile
import at.released.cassettes.plugin.framework.MultiplatformProjectExtension
import at.released.cassettes.plugin.framework.MultiplatformProjectExtension.Companion.build
import at.released.cassettes.plugin.framework.RootProjectDsl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File
import java.util.zip.ZipFile

class PublishTest {
    @JvmField
    @RegisterExtension
    var projectBuilder = MultiplatformProjectExtension()

    @Test
    fun `can publish project with resources`() {
        val testProject: RootProjectDsl = projectBuilder.setupTestProject("lib-simple-producer")
        val result = projectBuilder.build("publishAllPublicationsToTestRepository")

        val commonResultFile = File(
            testProject.rootDir,
            "lib-simple-producer/build/repo/com/example/" +
                    "lib-simple-producer/9999/lib-simple-producer-9999-kotlin_resources.zip",
        )

        assertThat(result.output).contains("BUILD SUCCESSFUL")
        assertResourcesText(commonResultFile)
    }

    private fun assertResourcesText(
        resourcesFile: File,
    ) {
        assertThat(resourcesFile).isFile()
        val linuxX64ResourceText = ZipFile(resourcesFile).use { zipFile ->
            val resourceEntry = zipFile.getEntry("cassettes/lib_simple_producer/resource.txt.wasm")
            zipFile.getInputStream(resourceEntry).bufferedReader().use {
                it.readText()
            }
        }

        assertThat(linuxX64ResourceText.trim()).isEqualTo("test resource")
    }
}
