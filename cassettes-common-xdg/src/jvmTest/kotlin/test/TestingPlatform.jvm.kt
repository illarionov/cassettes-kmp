/*
 * Copyright 2024, the wasm-sqlite-open-helper project authors and contributors. Please see the AUTHORS file
 * for details. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.common.xdg.test

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public actual annotation class IgnoreJs actual constructor()

@Target(CLASS, FUNCTION)
public actual annotation class IgnoreWasmJs actual constructor()
