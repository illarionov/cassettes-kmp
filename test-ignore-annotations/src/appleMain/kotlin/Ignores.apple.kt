/*
 * Copyright 2024, the wasi-emscripten-host project authors and contributors. Please see the AUTHORS file
 * for details. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 * SPDX-License-Identifier: Apache-2.0
 */

package at.released.cassettes.test.ignore.annotations

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

public actual typealias IgnoreApple = kotlin.test.Ignore

@Target(CLASS, FUNCTION)
public actual annotation class IgnoreLinux actual constructor()

@Target(CLASS, FUNCTION)
public actual annotation class IgnoreMingw actual constructor()
