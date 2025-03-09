/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.framework

import at.released.cassettes.plugin.framework.fixtures.Paths
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.io.File
import java.nio.file.Files
import java.util.Optional

@Suppress("TooManyFunctions")
public class MultiplatformProjectExtension : BeforeEachCallback, TestWatcher {
    private lateinit var rootDir: File

    override fun beforeEach(context: ExtensionContext?) {
        rootDir = Files.createTempDirectory("test").toFile()
    }

    override fun testSuccessful(context: ExtensionContext?) {
        cleanup()
    }

    override fun testAborted(context: ExtensionContext?, cause: Throwable?) {
        cleanup()
    }

    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
        // do not clean up, leave a temporary rootDir directory for future inspection
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?): Unit = Unit

    public fun setupTestProject(
        submoduleProjectName: String,
    ): RootProjectDsl = RootProjectDsl.setupRoot(
        rootDir,
        submoduleProjectName,
    ).apply {
        val testProjectDir = Paths.testProjectsRoot.resolve(submoduleProjectName)
        testProjectDir.copyRecursively(
            target = rootDir.resolve(submoduleProjectName),
            overwrite = true,
        )
    }

    public fun buildWithGradleVersion(
        gradleVersion: String = "8.13",
        expectFail: Boolean,
        vararg args: String,
    ): BuildResult {
        val runner = GradleRunner.create().apply {
            forwardOutput()
            withArguments(
                "--stacktrace",
                *args,
            )
            withProjectDir(rootDir)
            withGradleVersion(gradleVersion)
        }
        return if (!expectFail) {
            runner.build()
        } else {
            runner.buildAndFail()
        }
    }

    private fun cleanup() {
        rootDir.deleteRecursively()
    }

    public companion object {
        public fun MultiplatformProjectExtension.build(
            vararg args: String,
        ): BuildResult = buildWithGradleVersion(
            expectFail = false,
            args = args,
        )

        public fun MultiplatformProjectExtension.buildAndFail(
            vararg args: String,
        ): BuildResult = buildWithGradleVersion(
            expectFail = true,
            args = args,
        )
    }
}
