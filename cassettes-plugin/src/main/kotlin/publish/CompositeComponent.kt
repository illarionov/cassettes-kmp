/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.publish

import at.released.cassettes.plugin.InternalCassettesPluginApi
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.component.ComponentWithCoordinates
import org.gradle.api.component.ComponentWithVariants
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinSoftwareComponentWithCoordinatesAndPublication
import javax.inject.Inject

/**
 * Workaround for https://youtrack.jetbrains.com/issue/KT-58830 to publish in common source set on Kotlin < 2.1.20
 */
@InternalCassettesPluginApi
public open class CompositeComponent @Inject constructor(
    private val softwareComponentFactory: SoftwareComponentFactory,
    private val parent: KotlinSoftwareComponentWithCoordinatesAndPublication,
) : SoftwareComponentInternal by parent, ComponentWithVariants by parent, ComponentWithCoordinates by parent {
    val adHocComponent: AdhocComponentWithVariants = softwareComponentFactory.adhoc("compositeAdHoc")

    override fun getName(): String = parent.name

    override fun getUsages(): Set<UsageContext> {
        return parent.usages + (adHocComponent as SoftwareComponentInternal).usages
    }
}
