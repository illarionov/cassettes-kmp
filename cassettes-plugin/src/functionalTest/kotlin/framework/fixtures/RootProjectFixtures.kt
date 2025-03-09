/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.framework.fixtures

import at.released.cassettes.plugin.framework.FileContent
import at.released.cassettes.plugin.framework.fixtures.Paths.withInvariantSeparator

object RootProjectFixtures {
    public fun getGradleProperties(): FileContent = FileContent(
        "gradle.properties",
        """
            |org.gradle.jvmargs=-Xmx2G -XX:MaxMetaspaceSize=768M -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError
            |org.gradle.workers.max=2
            |org.gradle.vfs.watch=false
            |org.gradle.parallel=false
            |org.gradle.caching=true
            |org.gradle.configuration-cache=true
        """.trimMargin(),
    )

    public fun getSettingsGradleKts(vararg includeSubprojects: String): FileContent {
        val includes = includeSubprojects.joinToString("\n") { """include("$it")""" }
        val functionalTestsMaven = Paths.functionTestPluginRepository.withInvariantSeparator()
        val kotlinVersion = System.getenv("TEST_KOTLIN_VERSION") ?: "2.1.10"
        val cassettesVersion = System.getenv("TEST_CASSETTES_VERSION") ?: "0.1-alpha01"

        val content = """
            |buildscript {
            |    repositories {
            |        exclusiveContent {
            |            forRepository {
            |                maven { url = uri("file://$functionalTestsMaven") }
            |            }
            |            filter {
            |                includeGroupAndSubgroups("at.released.cassettes")
            |            }
            |        }
            |        mavenCentral()
            |        google()
            |    }
            |    dependencies {
            |        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
            |        classpath("at.released.cassettes:cassettes-plugin:$cassettesVersion")
            |    }
            |}
            |
            |dependencyResolutionManagement {
            |    repositories {
            |        google()
            |        mavenCentral()
            |    }
            |}
            |$includes
        """.trimMargin()
        return FileContent("settings.gradle.kts", content)
    }
}
