/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.settings

/*
 * Base settings convention plugin for the use in library modules
 */
plugins {
    id("at.released.cassettes.gradle.settings.common")
    id("at.released.cassettes.gradle.settings.repositories")
}
