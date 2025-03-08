/*
 * Copyright 2024, the wasm-sqlite-open-helper project authors and contributors. Please see the AUTHORS file
 * for details. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

internal actual val platformXdgEnvReader: PlatformXdgEnvReader = JvmPlatformXdgEnvReader

private object JvmPlatformXdgEnvReader : PlatformXdgEnvReader {
    override fun getEnv(name: String): String? = System.getenv(name)

    override fun getUserHomeDirectory(): String? = System.getProperty("user.home")
}
