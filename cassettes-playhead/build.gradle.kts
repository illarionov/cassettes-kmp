/*
 * SPDX-FileCopyrightText: 2025 Alexey Illarionov and the cassetes-kmp project contributors
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("at.released.cassettes.gradle.multiplatform.android-library")
    id("at.released.cassettes.gradle.multiplatform.kotlin")
    id("at.released.cassettes.gradle.multiplatform.publish")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

group = "ru.pixnews.cassettes"
version = cassettesVersions.getSubmoduleVersionProvider(
    propertiesFileKey = "cassettes_playhead_version",
    envVariableName = "CASSETTES_PLAYHEAD_VERSION",
).get()

kotlin {
    androidTarget()
    jvm()
    js {
      nodejs()
    }
    wasmJs {
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

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.io)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.assertk)
            implementation(libs.tempfolder)             .
        }
        nativeMain.dependencies {
            implementation(projects.commonXdg)
        }

        val jvmAndAndroid by creating {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(jvmAndAndroid)
        jvmMain.get().dependsOn(jvmAndAndroid)
    }
}

android {
    namespace = "ru.pixnews.cassettes.playhead"
}
