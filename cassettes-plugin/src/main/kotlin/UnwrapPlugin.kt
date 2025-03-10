/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin

import at.released.cassettes.plugin.ext.capitalizeAscii
import at.released.cassettes.plugin.publish.CASSETTES_SUBDIRECTORY
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.ComposeKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.plugin.mpp.resources.KotlinTargetResourcesPublication
import java.io.File

/**
 * A plugin that activates Kotlin Multiplatform resources and gathers all cassettes from dependencies into
 * directories, making them accessible to executable native binaries, such as test executables.
 *
 * Source: https://github.com/JetBrains/compose-multiplatform/blob/5da48904e5c24aa567da17fb780707073f920af3/gradle-plugins/compose/src/main/kotlin/org/jetbrains/compose/resources/KmpResources.kt
 */
public class UnwrapPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply(CassettesBasePlugin::class.java)
        target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            UnwrapCassettesConfiguration(target).apply()
        }
    }
}

@OptIn(ComposeKotlinGradlePluginApi::class)
private class UnwrapCassettesConfiguration(private val project: Project) {
    private val multiplatformExtension: KotlinMultiplatformExtension =
        project.extensions.getByType(KotlinMultiplatformExtension::class.java)

    fun apply() {
        val platformsForSetupCompilation = setOf(
            KotlinPlatformType.native,
            KotlinPlatformType.js,
            KotlinPlatformType.wasm,
        )
        val kmpResources = project.extraProperties.get("multiplatformResourcesPublication")
                as KotlinTargetResourcesPublication

        multiplatformExtension.targets
            .matching { target -> target.platformType in platformsForSetupCompilation }
            .configureEach {
                val allResources = kmpResources.resolveResources(this)
                compilations.all {
                    if (this.name == "main") {
                        configureResourcesForCompilation(this, allResources)
                    }
                }
            }
        configureAppleTestResources()
    }

    /**
     * Add resolved resources to a kotlin compilation to include it into a resulting platform artefact
     * It is required for JS and Native targets.
     * For JVM and Android it works automatically via jar files
     */
    private fun configureResourcesForCompilation(
        compilation: KotlinCompilation<*>,
        directoryWithAllResourcesForCompilation: Provider<File>,
    ) {
        compilation.defaultSourceSet.resources.srcDir(directoryWithAllResourcesForCompilation)

        // JS packaging requires explicit dependency
        if (compilation is KotlinJsCompilation) {
            project.tasks.named(compilation.processResourcesTaskName).configure {
                dependsOn(directoryWithAllResourcesForCompilation)
            }
        }
    }

    /**
     * Place assets into the test binary's output directory on Apple platforms so that they can be accessed using
     * NSBundle.mainBundle.
     */
    private fun configureAppleTestResources() {
        val appleTargetsWithResources = setOf("iosSimulatorArm64", "iosArm64", "iosX64", "macosArm64", "macosX64")
        multiplatformExtension.targets
            .withType(KotlinNativeTarget::class.java)
            .matching { target -> target.name in appleTargetsWithResources }
            .configureEach { configureCopyTestResources(this) }
    }

    private fun configureCopyTestResources(
        nativeTarget: KotlinNativeTarget,
    ) {
        @Suppress("GENERIC_VARIABLE_WRONG_DECLARATION")
        val copyResourcesTask: TaskProvider<Copy> = project.tasks.register(
            "copyTestComposeCassettesFor${nativeTarget.name.capitalizeAscii()}",
            Copy::class.java,
        )

        nativeTarget.binaries.withType(TestExecutable::class.java).all {
            val testExec = this
            val resourcesDirectories: Provider<List<SourceDirectorySet>> = project.provider {
                (testExec.compilation.associatedCompilations + testExec.compilation).flatMap {
                    it.allKotlinSourceSets.map(KotlinSourceSet::resources)
                }
            }
            copyResourcesTask.configure {
                from(resourcesDirectories)
                into(testExec.outputDirectory.resolve(CASSETTES_SUBDIRECTORY))
            }

            testExec.linkTaskProvider.configure {
                dependsOn(copyResourcesTask)
            }
        }
    }
}
