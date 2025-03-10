/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package at.released.cassettes.plugin.publish

import at.released.cassettes.plugin.ext.addMultiplatformNativeResourcesAttributes
import at.released.cassettes.plugin.ext.capitalizeAscii
import com.android.build.api.variant.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.register
import org.gradle.language.cpp.CppBinary
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import javax.inject.Inject

internal const val CASSETTES_SUBDIRECTORY = "cassettes"

internal open class PublishedAssetsConfigurator @Inject constructor(
    layout: ProjectLayout,
    private val tasks: TaskContainer,
    private val objects: ObjectFactory,
    private val providers: ProviderFactory,
    private val configurations: ConfigurationContainer,
) {
    private val cassettesBuildPaths = CassettesBuildPaths(layout)

    internal fun setupAndroidAssets(
        cassettes: FileCollection,
        androidVariant: Variant,
        projectName: String,
        resourcePackage: String = getResourcePackage(projectName),
    ) {
        val assetsSubdirectory = "$CASSETTES_SUBDIRECTORY/$resourcePackage/"
        val variantName = androidVariant.name.capitalizeAscii()

        @Suppress("GENERIC_VARIABLE_WRONG_DECLARATION")
        val rewrapAssetsTask = tasks.register<RewrapAssetsTask>("rewrapCassettesTo${variantName}Assets") {
            this.cassettes.from(cassettes)
            this.subdirectoryInAssets.set(assetsSubdirectory)
        }

        androidVariant.sources.assets?.addGeneratedSourceDirectory(
            rewrapAssetsTask,
            RewrapAssetsTask::outputDirectory,
        )
    }

    internal fun setupJvmResources(
        kotlinJvmSourceSet: KotlinSourceSet,
        cassettes: FileCollection,
        jvmResourcesPackage: Provider<String>,
    ) {
        val resourcesJvmDir: Provider<Directory> = cassettesBuildPaths.jvmRoot
        val jvmPackageSubdir: Provider<String> = jvmResourcesPackage.map { it.replace(".", "/") }

        @Suppress("GENERIC_VARIABLE_WRONG_DECLARATION")
        val rewrapJvmResourcesTask = tasks.register<Sync>("rewrapCassettesToJvmResources") {
            from(cassettes)
            into(resourcesJvmDir.zip<String, Directory>(jvmPackageSubdir, Directory::dir))
        }

        val resourcesDir = objects.fileCollection().apply {
            from(resourcesJvmDir)
            builtBy(rewrapJvmResourcesTask)
        }

        kotlinJvmSourceSet.resources.srcDir(resourcesDir)
    }

    internal fun setupCommonResources(
        targetComponent: AdhocComponentWithVariants,
        cassettes: FileCollection,
        projectName: String,
        projectVersion: Provider<String>,
        archiveBaseName: Provider<String> = providers.provider { projectName },
    ) {
        val packZipForPublicationTask = setupPackageResourcesTask(
            targetName = "common",
            cassettes = cassettes,
            projectName = projectName,
            projectVersion = projectVersion,
            archiveBaseName = archiveBaseName,
        )
        val publishedConfiguration = createConfigurationWithArchiveArtifact(
            null,
            packZipForPublicationTask.flatMap { it.archiveFile },
        )
        targetComponent.addVariantsFromConfiguration(publishedConfiguration) {
            mapToMavenScope("runtime")
        }
    }

    internal fun setupNativeOrJsResources(
        target: KotlinTarget,
        cassettes: FileCollection,
        projectName: String,
        projectVersion: Provider<String>,
        archiveBaseName: Provider<String> = providers.provider { projectName },
    ) {
        val packZipForPublicationTask = setupPackageResourcesTask(
            targetName = target.targetName,
            cassettes = cassettes,
            projectName = projectName,
            projectVersion = projectVersion,
            archiveBaseName = archiveBaseName,
        )
        val publishedConfiguration = createConfigurationWithArchiveArtifact(
            target,
            packZipForPublicationTask.flatMap { it.archiveFile },
        )
        target.addVariantsFromConfigurationsToPublication(publishedConfiguration) {
            mapToMavenScope("runtime")
        }
    }

    @Suppress("LongParameterList")
    private fun setupPackageResourcesTask(
        targetName: String,
        cassettes: FileCollection,
        projectName: String,
        projectVersion: Provider<String>,
        archiveBaseName: Provider<String>,
        resourcePackage: String = getResourcePackage(projectName),
    ): Provider<Zip> {
        val zipForPublicationDir = cassettesBuildPaths.rootForTarget(targetName).map { it.dir("zip-for-publication") }
        val subdirInsideZip = "${CASSETTES_SUBDIRECTORY}/$resourcePackage/"

        return tasks.register<Zip>("package${targetName.capitalizeAscii()}Resources") {
            this.archiveBaseName.set(archiveBaseName)
            archiveVersion.set(projectVersion)
            archiveClassifier.set("kotlin_resources")
            archiveExtension.set("zip")

            this.destinationDirectory.set(zipForPublicationDir)

            from(cassettes)
            into(subdirInsideZip)

            isReproducibleFileOrder = true
            isPreserveFileTimestamps = false
        }
    }

    private fun getResourcePackage(
        projectName: String,
    ): String {
        return projectName.lowercase().replace("-", "_")
    }

    private fun createConfigurationWithArchiveArtifact(
        target: KotlinTarget?,
        archiveForPublication: Provider<RegularFile>,
        isDebuggable: Boolean = false,
    ): ConsumableConfiguration {
        val targetName = target?.targetName?.capitalizeAscii() ?: "Common"
        return createConfigurationWithArchiveArtifact(
            configurationName = "cassettesReleasePacked${targetName}Elements",
            description = "Cassettes published as Kotlin Multiplatform Resources for $targetName",
            archiveForPublication = archiveForPublication,
            isDebuggable = isDebuggable,
        ).get()
    }

    private fun createConfigurationWithArchiveArtifact(
        configurationName: String,
        description: String,
        archiveForPublication: Provider<RegularFile>,
        isDebuggable: Boolean = false,
    ): Provider<ConsumableConfiguration> = configurations.consumable(configurationName) {
        this.description = description
        attributes {
            addMultiplatformNativeResourcesAttributes(objects, null)
            attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, isDebuggable)
        }
        outgoing {
            artifact(archiveForPublication) {
                extension = "zip"
                classifier = "kotlin_resources"
            }
        }
    }

    class CassettesBuildPaths(
        layout: ProjectLayout,
    ) {
        val root: Provider<Directory> = layout.buildDirectory.dir("cassettesLibraries")
        val jvmRoot: Provider<Directory> = root.map { it.dir("jvm") }

        fun rootForTarget(targetName: String): Provider<Directory> = root.map { it.dir(targetName) }
    }

    abstract class RewrapAssetsTask @Inject constructor(
        private val fileOperations: FileOperations,
    ) : DefaultTask() {
        @get:InputFiles
        abstract val cassettes: ConfigurableFileCollection

        @get:Input
        abstract val subdirectoryInAssets: Property<String>

        @get:OutputDirectory
        abstract val outputDirectory: DirectoryProperty

        @TaskAction
        fun copy() {
            val dir = outputDirectory.get().dir(subdirectoryInAssets.get())
            fileOperations.sync {
                from(cassettes)
                into(dir)
            }
        }
    }
}
