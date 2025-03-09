/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin

import at.released.cassettes.plugin.PublishMethod.COMMON_MODULE
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public abstract class CassettesExtension @Inject internal constructor(
    objects: ObjectFactory,
) {
    public val files: ConfigurableFileCollection = objects.fileCollection()

    public val publishMethod: Property<PublishMethod> = objects.property<PublishMethod>().convention(COMMON_MODULE)

    public val targetPackage: Property<String> = objects.property()
}
