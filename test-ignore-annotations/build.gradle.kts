/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassettes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("OPT_IN_USAGE")

plugins {
    id("at.released.cassettes.gradle.lint.android-lint")
    id("at.released.cassettes.gradle.multiplatform.android-library")
    id("at.released.cassettes.gradle.multiplatform.kotlin")
}

group = "at.released.cassettes"

android {
    namespace = "at.released.cassettes.ignore.annotations"
}

kotlin {
    androidTarget()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    jvm()
    js(IR) {
        nodejs()
    }
    wasmJs {
        browser()
        nodejs()
    }
    wasmWasi {
        nodejs()
    }
    iosSimulatorArm64()
    iosArm64()
    iosX64()
    linuxArm64()
    linuxX64()
    macosArm64()
    macosX64()
    mingwX64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    sourceSets {
        commonMain.dependencies {
            api(kotlin("test"))
        }
        jvmMain.dependencies {
            api(kotlin("test-junit"))
        }
        androidMain.dependencies {
            api(kotlin("test-junit"))
        }
    }
}
