/*
 * Copyright 2024, the wasm-sqlite-open-helper project authors and contributors. Please see the AUTHORS file
 * for details. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("at.released.cassettes.gradle.multiplatform.kotlin")
    id("at.released.cassettes.gradle.multiplatform.publish.multiplatform")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

group = "at.released.cassettes"
version = cassettesVersions.getSubmoduleVersionProvider(
    propertiesFileKey = "cassettes_common_xdg_version",
    envVariableName = "CASSETTES_COMMON_XDG_VERSION",
).get()

kotlin {
    jvm()
    js {
      browser()
      nodejs()
    }
    wasmJs {
        browser()
        nodejs()
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    linuxArm64()
    linuxX64()
    macosArm64()
    macosX64()
    mingwX64 {
        binaries.all {
            linkerOpts("-lole32")
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.io)
        }
        commonTest.dependencies {
            implementation(projects.testIgnoreAnnotations)
            implementation(kotlin("test"))
            implementation(libs.assertk)
        }
    }
}
