/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin.ext

import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE
import org.gradle.api.attributes.Bundling.EXTERNAL
import org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE
import org.gradle.api.attributes.Category.LIBRARY
import org.gradle.api.attributes.LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE
import org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE
import org.gradle.api.attributes.java.TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.Companion.konanTargetAttribute
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsCompilerAttribute
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

internal fun AttributeContainer.addMultiplatformNativeResourcesAttributes(
    objects: ObjectFactory,
    target: KotlinTarget?,
) {
    attribute(CATEGORY_ATTRIBUTE, objects.named(LIBRARY))
    attribute(BUNDLING_ATTRIBUTE, objects.named(EXTERNAL))
    attribute(TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named("non-jvm"))
    attribute(LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(target.multiplatformResourcesUsageAttribute))
    attribute(USAGE_ATTRIBUTE, objects.named(target.multiplatformResourcesUsageAttribute))

    when (target) {
        null -> {
            // We do not add the KotlinPlatformType attribute to allow resources from all Kotlin multiplatform targets
            // to match with common resources. This way, common resource dependencies are transitively added
            // to the configurations of the targets.
            // attribute(KotlinPlatformType.attribute, KotlinPlatformType.common)
        }
        is KotlinNativeTarget -> {
            attribute(konanTargetAttribute, target.konanTarget.name)
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.native)
        }
        is KotlinJsIrTarget -> {
            attribute(KotlinJsCompilerAttribute.jsCompilerAttribute, KotlinJsCompilerAttribute.ir)
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.js)
        }
    }
}

private val KotlinTarget?.multiplatformResourcesUsageAttribute: String
    get() = when {
        this is KotlinJsIrTarget -> "kotlin-multiplatformresourcesjs"
        else -> "kotlin-multiplatformresources"
    }
