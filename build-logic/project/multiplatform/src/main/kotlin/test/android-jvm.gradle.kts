/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.gradle.multiplatform.test

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryPlugin

/*
 * Convention plugin that configures android unit tests
 */
plugins.withType(LibraryPlugin::class.java) {
    extensions.configure(CommonExtension::class.java) {
        @Suppress("UnstableApiUsage")
        testOptions {
            unitTests {
                isReturnDefaultValues = false
                isIncludeAndroidResources = false
                all { testTask: Test ->
                    configureTestTaskDefaults(testTask)
                }
            }
        }
    }
}
