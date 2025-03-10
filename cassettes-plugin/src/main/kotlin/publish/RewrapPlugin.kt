/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.publish

import at.released.cassettes.plugin.CassettesBasePlugin
import at.released.cassettes.plugin.CassettesExtension
import at.released.cassettes.plugin.InternalCassettesPluginApi
import at.released.cassettes.plugin.PublishMethod.COMMON_MODULE
import at.released.cassettes.plugin.PublishMethod.TARGETS
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.api.publish.plugins.PublishingPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/*
 * Convention plugin that configures the creation and publication of binaries as resources
 */
public class RewrapPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply(CassettesBasePlugin::class.java)
        target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            RewrapKotlinMultiplatformConfiguration(target).apply()
        }
    }
}

private class RewrapKotlinMultiplatformConfiguration(private val project: Project) {
    private val objects: ObjectFactory = project.objects
    private val multiplatformExtension: KotlinMultiplatformExtension =
        project.extensions.getByType(KotlinMultiplatformExtension::class.java)
    val cassettesExtension = project.extensions.getByType(CassettesExtension::class.java)
    val resourcesConfigurator = project.objects.newInstance(PublishedAssetsConfigurator::class.java)

    fun apply() {
        when (cassettesExtension.publishMethod.get()) {
            COMMON_MODULE -> setupCommonResources(cassettesExtension.files)
            TARGETS -> setupNativeOrJsTargetsResources(cassettesExtension.files)
        }
        setupAndroidAssets(cassettesExtension.files)
        return setupJvmResources(cassettesExtension.files)
    }

    @OptIn(InternalCassettesPluginApi::class)
    private fun setupCommonResources(cassettes: FileCollection) {
        @Suppress("INVISIBLE_MEMBER")
        val rootKotlinSoftwareComponent = multiplatformExtension.rootSoftwareComponent

        val compositeComponent: CompositeComponent =
            objects.newInstance(CompositeComponent::class.java, rootKotlinSoftwareComponent)
        resourcesConfigurator.setupCommonResources(
            targetComponent = compositeComponent.adHocComponent,
            cassettes = cassettes,
            projectName = project.name,
            projectVersion = project.provider { project.version.toString() },
            archiveBaseName = project.extensions.getByType(BasePluginExtension::class.java).archivesName,
        )

        project.plugins.withType(PublishingPlugin::class.java) {
            // Replace "KotlinMultiplatform" maven publication with own copy with added resources
            project.extensions.getByType(PublishingExtension::class.java).publications.create(
                "kotlinMultiplatform2",
                MavenPublication::class.java,
            ) {
                from(compositeComponent)
                (this as MavenPublicationInternal).publishWithOriginalFileName()
            }

            project.tasks.matching {
                it.name.startsWith("publishKotlinMultiplatformPublication")
            }.configureEach {
                enabled = false
            }
        }
    }

    // Publishing resources in target publications.
    private fun setupNativeOrJsTargetsResources(cassettes: FileCollection) {
        val targetsWithResources = setOf(
            "iosArm64",
            "iosSimulatorArm64",
            "iosX64",
            "linuxArm64",
            "linuxX64",
            "macosArm64",
            "macosX64",
            "js",
            "mingwX64",
        )

        multiplatformExtension.targets.matching { it.name in targetsWithResources }.configureEach {
            resourcesConfigurator.setupNativeOrJsResources(
                target = this,
                cassettes = cassettes,
                projectName = project.name,
                projectVersion = project.provider { project.version.toString() },
                archiveBaseName = project.extensions.getByType(BasePluginExtension::class.java).archivesName,
            )
        }
    }

    private fun setupAndroidAssets(cassettes: FileCollection) {
        project.plugins.withId("com.android.library") {
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants {
                resourcesConfigurator.setupAndroidAssets(
                    cassettes = cassettes,
                    androidVariant = it,
                    projectName = project.name,
                )
            }
        }
    }

    private fun setupJvmResources(cassettes: FileCollection) {
        multiplatformExtension.sourceSets
            .matching { it.name == "jvmMain" }
            .configureEach {
                resourcesConfigurator.setupJvmResources(
                    kotlinJvmSourceSet = this,
                    cassettes = cassettes,
                    jvmResourcesPackage = cassettesExtension.targetPackage,
                )
            }
    }
}
