/*
 * SPDX-FileCopyrightText: 2024-2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.lint

import com.android.build.api.dsl.Lint

/**
 * Android Lint defaults
 */
internal fun Lint.configureCommonAndroidLint() {
    quiet = false
    ignoreWarnings = false
    htmlReport = true
    xmlReport = true
    sarifReport = true
    checkDependencies = false
    ignoreTestSources = false

    disable += "ObsoleteSdkInt"
    informational += "GradleDependency"
}
