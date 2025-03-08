/*
 * Copyright 2024, the wasm-sqlite-open-helper project authors and contributors. Please see the AUTHORS file
 * for details. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg

internal expect val platformXdgEnvReader: PlatformXdgEnvReader

internal interface PlatformXdgEnvReader {
    fun getEnv(name: String): String?
    fun getUserHomeDirectory(): String?
}
