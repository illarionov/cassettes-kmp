/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.plugin

import kotlin.annotation.AnnotationRetention.BINARY

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is internal in cassettes-kmp and should not be used. " +
            "It could be removed or changed without notice.",
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(BINARY)
public annotation class InternalCassettesPluginApi
